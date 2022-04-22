package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.listeners.ConnectionEvent;
import it.polimi.ingsw.server.listeners.ConnectionListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Class for instantiate a new Game and handle new network requests
 */
public class Server implements ConnectionListener {

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
    private final Controller controller;

    /**
     * Executor for handle virtual Client
     */
    private ThreadPoolExecutor executor;

    /**
     * Server's constructor method
     */
    public Server() {
        virtualClients = new HashMap<>();
        //Todo how we chose the port
        port = 123;
        controller = new Controller();

    }

    /**
     * Main Method used for instantiate Virtual Client if is permitted.
     * Each of this Virtual Client are run on different threads
     */
     public void handleRequest() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Server ready");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                String nickname = handleFirstConnection(socket);
                if(nickname != null) {
                    synchronized (this) {
                        boolean openVirtualClient = false;
                        do {
                            try {
                                VirtualClient vc = new VirtualClient(nickname, socket, this);
                                vc.addListener(controller);
                                virtualClients.put(nickname, vc);
                                openVirtualClient = true;
                                executor.submit(vc);
                            } catch (IOException ignored) {
                            }
                        } while (!openVirtualClient);
                    }
                }
            } catch(IOException e) {
                break;
            }

        }
        executor.shutdown();
    }

    /**
     * Verifies if the new request is a valid one. Call the controller and try to add a player. If the controller doesn't exist
     * request the game mode to the client who is the first player and instantiate a new party.
     * In case of disconnection verify that the request came from a player who was already in the party.
     * @param socket used for send reply messages.
     * @return a String sets as null if the request is invalid or a String with the player nickname if the request is valid.
     */
    private String handleFirstConnection(Socket socket) {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String line = in.nextLine();

            Message message = MessageBuilder.fromJson(line);
            if (MessageType.retrieveByMessageClass(message).equals(MessageType.LOGIN)) {
                Login mex = (Login) message;

                String nickname = mex.getNickname();
                //nickname not null
                if (mex.isValid()) {
                    out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_NULL_NICKNAME)));
                    out.flush();
                    socket.close();
                    return null;
                }
                //model didn't exist
                if (!controller.isInstantiated()) {
                    out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.CHOOSE_PARTY_TYPE)));
                    out.flush();

                    line = in.nextLine();
                    message = MessageBuilder.fromJson(line);
                    if (MessageType.retrieveByMessageClass(message).equals(MessageType.CHOSEN_GAME)) {
                        ChosenGame choice = (ChosenGame) message;
                        if (choice.isValid()) {
                            do {
                                controller.setModel(choice.getPreset(), choice.getMode());
                                this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(choice.getPreset().getPlayersNumber());
                            }
                            while (!controller.addPlayer(nickname));
                            return nickname;
                        }

                    }
                    out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE)));
                    out.flush();
                    socket.close();
                    return null;
                }

                //model exists
                if (controller.addPlayer(nickname)) {
                    return nickname;
                } else {
                    //Was the player already in the party but had some connection issue?
                    synchronized (this) {
                        if (virtualClients.containsKey(nickname) && virtualClients.get(nickname) != null) {
                            virtualClients.remove(nickname);
                            return nickname;
                        } else {
                            out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_NICKNAME_UNAVAILABLE)));
                            out.flush();
                            socket.close();
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
    synchronized public void onConnectionEvent(ConnectionEvent event) {
            VirtualClient vc =(VirtualClient)event.getSource();
            executor.remove(vc);
            virtualClients.remove(vc.getIdentifier());
            virtualClients.put(vc.getIdentifier(),null);
            vc.removeListener();
            //TODO remove model
            //TODO skip turn for that player !!IMPORTANT!! manage the case of player is the master and the party isn't started
    }
}
