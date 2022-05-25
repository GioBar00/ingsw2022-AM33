package it.polimi.ingsw.server;

import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.network.listeners.DisconnectEvent;
import it.polimi.ingsw.network.listeners.DisconnectListener;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.enums.ServerState;
import it.polimi.ingsw.server.listeners.EndGameEvent;
import it.polimi.ingsw.server.listeners.EndGameListener;
import it.polimi.ingsw.server.lobby.LobbyConstructor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for instantiate a new Game and handle new network requests
 */
public class Server implements EndGameListener, DisconnectListener {

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
     * Current server state
     */
    private ServerState state;

    /**
     * Server's constructor method
     */
    public Server() {
        this(1234);
    }

    /**
     * Server's constructor method
     *
     * @param port tcp port of the server
     */
    public Server(int port) {
        virtualClients = new HashMap<>();
        this.port = port;
        resetGame();
    }

    /**
     * Reset the controller
     */
    private void resetGame() {
        controller = new Controller();
        controller.setEndGameListener(this);
        controller.setDisconnectListener(this);
        startController();
        state = ServerState.EMPTY;
    }

    /**
     * Main Method used for instantiate Virtual Client if is permitted.
     * Each of this Virtual Client are run on different threads
     */
    public void handleRequests() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("S: server ready");
            while (!Thread.interrupted()) {
                try {
                    System.out.println("S: waiting for client");
                    Socket socket = serverSocket.accept();
                    CommunicationHandler communicationHandler = new CommunicationHandler(true);
                    communicationHandler.setSocket(socket);
                    synchronized (this) {
                        switch (state) {
                            case EMPTY -> {
                                state = ServerState.HANDLING_FIRST;
                                new Thread(() -> handleFirstPlayer(communicationHandler)).start();
                            }
                            case HANDLING_FIRST -> Executors.newSingleThreadExecutor().submit(() -> {
                                communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_SERVER_UNAVAILABLE));
                                communicationHandler.stop();
                            });
                            case NORMAL ->
                                    new Thread(() -> handleNewPlayer(communicationHandler)).start();
                        }
                    }
                } catch (Throwable e) {
                    System.out.println("S: NOT HANDLED ERROR!!!!");
                    e.printStackTrace();
                    break;
                }

            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Handle the first player connection
     *
     * @param communicationHandler the communication handler of the first player
     */
    private void handleFirstPlayer(CommunicationHandler communicationHandler) {
        try {
            String nickname = getPlayerNickname(communicationHandler);
            if (nickname != null) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                communicationHandler.setMessageHandler((message) -> {
                    if (message.isValid() && MessageType.retrieveByMessage(message) == MessageType.CHOSEN_GAME) {
                        ChosenGame choice = (ChosenGame) message;
                        controller.setModelAndLobby(choice.getPreset(), choice.getMode(), LobbyConstructor.getLobby(choice.getPreset()));
                        synchronized (this) {
                            state = ServerState.NORMAL;
                            countDownLatch.countDown();
                            addPlayer(communicationHandler, nickname);
                        }
                    } else
                        communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE));
                });

                communicationHandler.sendMessage(new CommMessage(CommMsgType.CHOOSE_GAME));

                try {
                    if (!countDownLatch.await(30, TimeUnit.SECONDS)) {
                        throw new TimeoutException();
                    }
                } catch (InterruptedException ignored) {
                }

            } else {
                communicationHandler.stop();
                synchronized (this) {
                    state = ServerState.EMPTY;
                }
            }
        } catch (TimeoutException e) {
            communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_TIMEOUT));
            communicationHandler.stop();
            synchronized (this) {
                state = ServerState.EMPTY;
            }
        }
    }

    /**
     * Gets the nickname of the player
     *
     * @param communicationHandler the communication handler of the player
     * @return the nickname of the player
     * @throws TimeoutException if the player doesn't send a nickname in time
     */
    private String getPlayerNickname(CommunicationHandler communicationHandler) throws TimeoutException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> nickname = new AtomicReference<>();
        communicationHandler.setMessageHandler((message) -> {
            if (message.isValid() && MessageType.retrieveByMessage(message) == MessageType.LOGIN) {
                Login login = (Login) message;
                nickname.set(login.getNickname());
            } else {
                communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE));
                nickname.set(null);
            }
            latch.countDown();
        });

        communicationHandler.start();

        try {
            if (latch.await(10, TimeUnit.SECONDS) && nickname.get() != null) {
                communicationHandler.setMessageHandler(null);
                return nickname.get();
            } else if (nickname.get() == null) {
                communicationHandler.setMessageHandler(null);
                return null;
            }
        } catch (InterruptedException ignored) {
        }
        communicationHandler.setMessageHandler(null);
        throw new TimeoutException();
    }

    /**
     * Handles the connection of a new player
     *
     * @param communicationHandler the communication handler of the player
     */
    private void handleNewPlayer(CommunicationHandler communicationHandler) {
        try {
            String nickname = getPlayerNickname(communicationHandler);

            if (nickname != null) {
                communicationHandler.setMessageHandler((m) -> {});
                if (controller.isGameStarted()) {
                    synchronized (this) {
                        if (virtualClients.containsKey(nickname) && !virtualClients.get(nickname).isConnected()) {
                            virtualClients.get(nickname).reconnect(communicationHandler);
                        } else {
                            communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_NO_SPACE));
                            communicationHandler.stop();
                        }
                    }
                } else if (virtualClients.containsKey(nickname)) {
                    communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_NICKNAME_UNAVAILABLE));
                    communicationHandler.stop();
                } else {
                    if (!addPlayer(communicationHandler, nickname)) {
                        communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_NO_SPACE));
                        communicationHandler.stop();
                    }
                }
            } else
                communicationHandler.stop();
        } catch (TimeoutException e) {
            communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_TIMEOUT));
            communicationHandler.stop();
        }
    }

    /**
     * Creates the virtual client for the player
     *
     * @param communicationHandler the communication handler of the player
     * @param nickname             the nickname of the player
     * @return true if the player was added, false otherwise
     */
    private boolean addPlayer(CommunicationHandler communicationHandler, String nickname) {
        if (controller.addPlayer(nickname)) {
            System.out.println("S: added player " + nickname);
            VirtualClient vc = new VirtualClient(nickname);
            connectToVirtualClient(vc, communicationHandler);
            virtualClients.put(nickname, vc);
            controller.sendInitialStats(vc);
            return true;
        }
        return false;
    }

    /**
     * Starts a virtual client and adds it to the model listeners. Adds the controller to the VirtualClient listeners
     *
     * @param vc     the VirtualClient
     * @param communicationHandler handler for connection
     */
    private void connectToVirtualClient(VirtualClient vc, CommunicationHandler communicationHandler) {
        vc.setCommunicationHandler(communicationHandler);
        communicationHandler.setDisconnectListener(vc);
        communicationHandler.setMessageHandler(vc);
        controller.addModelListener(vc);
        vc.addMessageListener(controller);
    }

    /**
     * Method called by Virtual Client if the connection is lost. Server removes the thread from execution and remove the
     * virtual Client bonded with the nickname of disconnected player.
     * Calls method skip turn on controller.
     *
     * @param event of the connection
     */
    @Override
    public synchronized void onEndGameEvent(EndGameEvent event) {
        for (VirtualClient vc : virtualClients.values()) {
            controller.removeModelListener(vc);
            vc.stop();
        }
        virtualClients.clear();
        ExecutorService oldController = executor;
        controller.removeEndGameListener();
        controller.setDisconnectListener(null);
        resetGame();
        System.out.println("S: disconnected all players");
        oldController.shutdownNow();
    }

    /**
     * Getter of the controller
     *
     * @return the controller
     */
    public Controller getController() {
        return controller;
    }

    /**
     * Closes the controller thread
     */
    public void stopController() {
        executor.shutdownNow();
    }

    /**
     * Starts the controller
     */
    private void startController() {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(controller::startController);
    }

    /**
     * Invoked when a client disconnects from the server and vice versa.
     *
     * @param event the event object
     */
    @Override
    public synchronized void onDisconnect(DisconnectEvent event) {
        VirtualClient vc = (VirtualClient) event.getSource();
        vc.stop();
        if (!controller.isGameStarted()) {
            controller.removeModelListener(vc);
            virtualClients.remove(vc.getIdentifier());
        }
        System.out.println("S: disconnected player " + vc.getIdentifier());
    }
}
