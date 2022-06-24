package it.polimi.ingsw.server;

import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.enums.ServerState;
import it.polimi.ingsw.server.lobby.LobbyConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for instantiate a new Game and handle new network requests
 */
public class Server {

    /**
     * tcp port of the server
     */
    private final int port;

    
    private final ClientManager clientManager;

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
        this.port = port;
        clientManager = new ClientManager(this);
        clientManager.resetGame();
    }

    /**
     * @return the clientManager
     */
    public ClientManager getClientManager() {
        return clientManager;
    }

    /**
     * @return the state of the server
     */
    public ServerState getState() {
        return state;
    }

    /**
     * Reset the server to the initial state
     */
    public synchronized void resetGame() {
        System.out.println("S: Resetting game");
        state = ServerState.EMPTY;
    }

    /**
     * Main Method used to instantiate Virtual Client if is permitted.
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
                            case HANDLING_FIRST -> new Thread(() -> communicationHandler.sendLastMessage(new CommMessage(CommMsgType.ERROR_SERVER_UNAVAILABLE))).start();
                            case NORMAL -> new Thread(() -> handleNewPlayer(communicationHandler)).start();
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
                        communicationHandler.setDisconnectListener(null);
                        ChosenGame choice = (ChosenGame) message;
                        clientManager.getController().setModelAndLobby(choice.getPreset(), choice.getMode(), LobbyConstructor.getLobby(choice.getPreset()));
                        synchronized (this) {
                            countDownLatch.countDown();
                            clientManager.addPlayer(communicationHandler, nickname);
                            state = ServerState.NORMAL;
                        }
                    } else
                        communicationHandler.sendLastMessage(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE));
                });
                communicationHandler.setDisconnectListener((e) -> {
                    synchronized (this) {
                        state = ServerState.EMPTY;
                    }
                    System.out.println("S: EMPTY");
                });

                communicationHandler.sendMessage(new CommMessage(CommMsgType.CHOOSE_GAME));

                try {
                    if (!countDownLatch.await(30, TimeUnit.SECONDS)) {
                        throw new TimeoutException();
                    }
                } catch (InterruptedException ignored) {
                }

            } else {
                synchronized (this) {
                    state = ServerState.EMPTY;
                }
            }
        } catch (TimeoutException e) {
            if (communicationHandler.isConnected()) {
                communicationHandler.sendLastMessage(new CommMessage(CommMsgType.ERROR_TIMEOUT));
                synchronized (this) {
                    state = ServerState.EMPTY;
                }
                System.out.println("S: EMPTY");
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
                communicationHandler.sendLastMessage(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE));
                nickname.set(null);
            }
            latch.countDown();
        });
        communicationHandler.setDisconnectListener((e) -> {
            nickname.set(null);
            latch.countDown();
        });

        communicationHandler.start();

        try {
            if (latch.await(10, TimeUnit.SECONDS) && nickname.get() != null) {
                communicationHandler.setMessageHandler(null);
                communicationHandler.setDisconnectListener(e -> {});
                return nickname.get();
            } else if (nickname.get() == null) {
                communicationHandler.setMessageHandler(null);
                communicationHandler.setDisconnectListener(e -> {});
                return null;
            }
        } catch (InterruptedException ignored) {
        }
        communicationHandler.setMessageHandler(null);
        communicationHandler.setDisconnectListener(e -> {});
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
                communicationHandler.setMessageHandler((m) -> {
                });
                synchronized (this) {
                    synchronized (clientManager) {
                        if (clientManager.getController().isGameStarted()) {
                            if (clientManager.getVirtualClient(nickname) != null && !clientManager.getVirtualClient(nickname).isConnected()) {
                                clientManager.reconnectPlayer(communicationHandler, nickname);
                            } else {
                                communicationHandler.sendLastMessage(new CommMessage(CommMsgType.ERROR_NO_SPACE));
                            }
                        } else if (clientManager.getVirtualClient(nickname) != null) {
                            communicationHandler.sendLastMessage(new CommMessage(CommMsgType.ERROR_NICKNAME_UNAVAILABLE));
                        } else {
                            if (!clientManager.addPlayer(communicationHandler, nickname)) {
                                communicationHandler.sendLastMessage(new CommMessage(CommMsgType.ERROR_NO_SPACE));
                            }
                        }
                    }
                }
            }
        } catch (TimeoutException e) {
            communicationHandler.sendLastMessage(new CommMessage(CommMsgType.ERROR_TIMEOUT));
        }
    }


}
