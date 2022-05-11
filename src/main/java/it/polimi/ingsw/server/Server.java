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
     * The executor used to allocate the controller.
     */
    private ExecutorService executor;

    /**
     * The preset of the party
     */
    private ChosenGame choice;
    /**
     * Server's constructor method
     */
    public Server() {
        this(1234);
    }

    /**
     * Server's constructor method
     * @param port tcp port of the server
     */
    public Server(int port) {
        virtualClients = new HashMap<>();
        choice = null;
        this.port = port;
        controller = new Controller(this);
        startController();
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
                    String nickname = null;
                    System.out.println("S: waiting for client");
                    Socket socket = serverSocket.accept();
                    Future <String> nick =  executor.submit(() -> this.handleFirstConnection(socket));
                    try {
                        nickname = nick.get(35, TimeUnit.SECONDS);
                    }
                    catch (InterruptedException | ExecutionException ignored){ }
                    catch (TimeoutException e){
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        MessageExchange.sendMessage(new CommMessage(CommMsgType.ERROR_SERVER_UNAVAILABLE), out);
                        out.close();
                        socket.close();
                    }
                    finally {
                        if(nickname != null) {
                            synchronized (this) {
                                VirtualClient vc = new VirtualClient(nickname);
                                if(virtualClients.size() == 0 && choice != null){
                                    controller.setModelAndLobby(choice.getPreset(), choice.getMode(), LobbyConstructor.getLobby(choice.getPreset(),vc));
                                    controller.addPlayer(nickname);
                                }
                                startVirtualClient(vc, socket);
                                virtualClients.put(nickname, vc);
                                controller.sendInitialStats(vc);

                            }
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
                    closeCommunication(socket,in,out);
                    return null;
                }

                //player already in the party -> the model exist
                if(virtualClients.containsKey(nickname) && !virtualClients.get(nickname).isConnected()){
                    VirtualClient vc = virtualClients.get(nickname);
                    closeInOut(in, out);
                    startVirtualClient(vc,socket);
                    if(controller.isInstantiated()){
                        controller.addModelListener(vc);
                    }
                    return null;
                }
                else {
                    if(virtualClients.containsKey(nickname) && virtualClients.get(nickname).isConnected()){
                        MessageExchange.sendMessage(new CommMessage(CommMsgType.ERROR_NICKNAME_UNAVAILABLE), out);
                        return null;
                    }
                    if (!controller.isInstantiated()) {
                        //model didn't exist
                        return firstPlayer(nickname,socket,in,out);
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
     * Sends a message to the client if he is the host and wait for the chosen Game.
     * If the message doesn't arrive in the consecutive 30 seconds the server closes the connection
     * @param nickname the nickname of the game host
     * @param socket Socket for the connection
     * @param in BufferedReader for reading input
     * @param out BufferedWriter for writing output
     * @throws IOException if there's some failure in closing connection
     */
    private String firstPlayer(String nickname, Socket socket, BufferedReader in, BufferedWriter out) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        MessageExchange.sendMessage(new CommMessage(CommMsgType.CHOOSE_GAME), out);

        Future <String> result = executor.submit(() -> {
            String mes = in.readLine();
            Message m = MessageBuilder.fromJson(mes);
            if (MessageType.retrieveByMessage(m).equals(MessageType.CHOSEN_GAME)) {
                ChosenGame choice = (ChosenGame) m;
                if (choice.isValid()) {
                    this.choice = choice;
                    return nickname;
                }
            }
            return null;
        });
        try {
            return result.get(30, TimeUnit.SECONDS);
        }
        catch (InterruptedException | ExecutionException ignored){
            return null;
        }
        catch (TimeoutException e){
            MessageExchange.sendMessage(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE), out);
            socket.close();
            return null;
        }
    }

    /**
     * closes the input reader and the output writer
     * @param in BufferedReader for reading input
     * @param out BufferedWriter for writing output
     * @throws IOException if there's some failure in closing connection
     */
    private void closeInOut(BufferedReader in, BufferedWriter out) throws IOException {
        in.close();
        out.close();
    }

    /**
     * closes the socket the input reader and the output writer
     * @param socket Socket for the connection
     * @param in BufferedReader for reading input
     * @param out BufferedWriter for writing output
     * @throws IOException if there's some failure in closing connection
     */
    private void closeCommunication(Socket socket, BufferedReader in, BufferedWriter out) throws IOException {
        closeInOut(in, out);
        socket.close();
    }

    /**
     * Starts a virtual client and adds it to the model listeners. Adds the controller to the VirtualClient listeners
     * @param vc the VirtualClient
     * @param socket Socket for connection
     */
    private void startVirtualClient(VirtualClient vc,Socket socket) {
        vc.setSocket(socket);
        controller.addModelListener(vc);
        vc.addMessageListener(controller);
        vc.start();
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
        stopServer();
        controller.removeListener();
        controller = new Controller(this);
        startController();
    }

    /**
     * Getter of the controller
     * @return the controller
     */
    public Controller getController(){
        return controller;
    }

    /**
     * Closes the controller thread
     */
    public void stopServer(){
        executor.shutdownNow();
    }

    /**
     * Starts the controller
     */
    private void startController(){
        executor = Executors.newFixedThreadPool(2);
        executor.submit(controller::startController);
    }
}
