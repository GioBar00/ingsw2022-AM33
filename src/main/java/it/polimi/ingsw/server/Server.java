package it.polimi.ingsw.server;

import it.polimi.ingsw.network.MessageExchange;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.listeners.EndPartyEvent;
import it.polimi.ingsw.server.listeners.EndPartyListener;
import it.polimi.ingsw.network.listeners.MessageEvent;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Class for instantiate a new Game and handle new network requests
 */
public class Server implements EndPartyListener {

    /**
     * a collection of player nickname and their request handler
     */
    private final HashMap<String, VirtualClient> virtualClients;

    /**
     * tcp port of the controller
     */
    private final int port;

    /**
     * Game Controller
      */
    private Controller controller;


    /**
     * Server's constructor method
     */
    public Server() {
        virtualClients = new HashMap<>();
        //Todo how we chose the port
        port = 1234; // modified to 1234 because the first 1024 numbers of port are reserved
        controller = new Controller(this);

    }

    /**
     * Main Method used for instantiate Virtual Client if is permitted.
     * Each of this Virtual Client are run on different threads
     */
     public void handleRequest() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("S: server ready");
            while (!Thread.interrupted()) {
                try {
                    System.out.println("S: waiting for client");
                    Socket socket = serverSocket.accept();
                    String nickname = handleFirstConnection(socket);
                    if(nickname != null) {
                        synchronized (this) {
                            VirtualClient vc = new VirtualClient(nickname);
                            vc.setSocket(socket);
                            vc.addMessageListener(controller);
                            controller.addModelListener(vc);
                            virtualClients.put(nickname, vc);
                            vc.start();
                            controller.sendAvailableWizard(vc);
                        }
                    }
                } catch(IOException e) {
                    System.out.println("S: no connection available");
                    break;
                }

            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Verifies if the new request is a valid one. Call the controller and try to add a player. If the controller doesn't exist
     * request the game mode to the client who is the first player and instantiate a new party.
     * In case of disconnection verify that the request came from a player who was already in the party.
     * @param socket used for send reply messages.
     * @return a String sets as null if the request is invalid or a String with the player nickname if the request is valid.
     */
    public String handleFirstConnection(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Message message = MessageExchange.receiveMessage(in);
            if (MessageType.retrieveByMessage(message).equals(MessageType.LOGIN)) {
                Login mex = (Login) message;

                String nickname = mex.getNickname();
                //nickname not null
                if (!mex.isValid()) {
                    MessageExchange.sendMessage(new CommMessage(CommMsgType.ERROR_NULL_NICKNAME), out);
                    in.close();
                    out.close();
                    socket.close();
                    return null;
                }

                //player already in the party -> the model exist
                if(virtualClients.containsKey(nickname) && !virtualClients.get(nickname).isConnected()){
                    VirtualClient vc = virtualClients.get(nickname);
                    in.close();
                    out.close();
                    vc.setSocket(socket);
                    controller.addModelListener(vc);
                    vc.addMessageListener(controller);
                    vc.start();
                    return null;
                }
                else {
                    //model didn't exist
                    if (!controller.isInstantiated()) {
                        ExecutorService executor = Executors.newFixedThreadPool(1);

                        MessageExchange.sendMessage(new CommMessage(CommMsgType.CHOOSE_GAME), out);

                        Future <String> result = executor.submit(() -> {
                            String mes = in.readLine();
                            Message m = MessageBuilder.fromJson(mes);
                            if (MessageType.retrieveByMessage(m).equals(MessageType.CHOSEN_GAME)) {
                                ChosenGame choice = (ChosenGame) m;
                                if (choice.isValid()) {

                                    controller.setModelAndLobby(choice.getPreset(), choice.getMode(), LobbyConstructor.getLobby(choice.getPreset()));
                                    controller.addPlayer(nickname);
                                    return nickname;
                                }
                            }
                            return null;
                        });
                        try {
                            return result.get(30, TimeUnit.SECONDS);
                        }
                        catch (InterruptedException | ExecutionException e){
                            e.printStackTrace();
                        }
                        catch (TimeoutException e){
                            MessageExchange.sendMessage(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE), out);
                            socket.close();
                            return null;
                        }
                    }
                    else {
                        //model exists
                        if (controller.addPlayer(nickname)) {
                            return nickname;
                        }
                        else {
                            //Was the player already in the party but had some connection issue?
                            MessageExchange.sendMessage(new CommMessage(CommMsgType.ERROR_NO_SPACE), out);
                            return null;
                         }

                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method called by Virtual Client if the connection is lost. Server removes the thread from execution and remove the
     * virtual Client bonded with the nickname of disconnected player.
     * Calls method skip turn on controller.
     * @param event of the connection
     */
    @Override
    synchronized public void onEndPartyEvent(EndPartyEvent event) {
        for(VirtualClient vc : virtualClients.values()){
            if(vc.isConnected()){
                vc.removeMessageListener(controller);
                vc.onMessage(new MessageEvent(this, new CommMessage(CommMsgType.ERROR_HOST_DISCONNECTED)));
            }
        }
        try {
            wait(10*100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(VirtualClient vc : virtualClients.values()){
            if(vc.isConnected()){
                vc.stop();
            }
        }

        virtualClients.clear();
        controller.removeListener();
        controller = new Controller(this);
    }
}
