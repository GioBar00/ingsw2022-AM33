package it.polimi.ingsw.client;

import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.network.MessageHandler;
import it.polimi.ingsw.network.listeners.DisconnectEvent;
import it.polimi.ingsw.network.listeners.DisconnectListener;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.IgnoreMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class for client-side communication
 */
public class Client implements MessageHandler, ViewListener, Runnable, DisconnectListener {

    /**
     * Address of the server to connect to
     */
    private SocketAddress serverAddress;

    /**
     * The nickname of the user.
     */
    private String nickname;

    /**
     * The message exchange handler used to send and receive messages to and from the client
     */
    private CommunicationHandler communicationHandler;

    /**
     * The interface for the user
     */
    private final UI userInterface;

    /**
     * Queue with arrived messages
     */
    private final LinkedBlockingQueue<Message> queue;

    /**
     * Boolean that indicates if the {@link CommunicationHandler} is active
     */
    private volatile boolean stopped = true;

    /**
     * Constructor of Virtual Server
     */
    public Client(UI ui) {
        queue = new LinkedBlockingQueue<>();
        userInterface = ui;
    }

    /**
     * This method show the start screen in the user interface.
     */
    public void startClient() {
        userInterface.showStartScreen();
    }

    /**
     * This method set the server address.
     *
     * @param hostname the hostname of the server.
     * @param port     the port of the server.
     * @return true if the server address is set, false otherwise.
     */
    public boolean setServerAddress(String hostname, int port) {
        try {
            if (validateServerString(hostname)) {
                serverAddress = new InetSocketAddress(hostname, port);
                return true;
            }
        } catch (IllegalArgumentException ignored) {
        }
        return false;
    }

    /**
     * Setter of the nickname.
     *
     * @param nickname the nickname of the user.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * This method sets up the connection and starts the communicationHandler.
     */
    public boolean startConnection() {
        try {
            Socket socket = new Socket();
            socket.connect(serverAddress, 5 * 1000);
            communicationHandler = new CommunicationHandler(this);
            communicationHandler.setSocket(socket);
            communicationHandler.setDisconnectListener(this);
            communicationHandler.start();
            if (stopped) {
                stopped = false;
                new Thread(this).start();
            }
            return true;
        } catch (IOException e) {
            closeConnection();
            return false;
        }
    }

    /**
     * This method is called when a message is received.
     *
     * @param message the message received.
     */
    @Override
    public void handleMessage(Message message) {
        System.out.println("C: received message - " + MessageBuilder.toJson(message));
        queue.add(message);
    }

    /**
     * Method called when the user want to update the model.
     *
     * @param message the request.
     */
    @Override
    public void onMessage(Message message) {
        System.out.println("C: sending message - " + MessageBuilder.toJson(message));
        communicationHandler.sendMessage(message);
    }

    /**
     * This method is used to send the login message to the server.
     *
     * @return true if the login message is sent, false otherwise.
     */
    public boolean sendLogin() {
        if (nickname != null && serverAddress != null && startConnection()) {
            onMessage(new Login(nickname));
            return true;
        }
        userInterface.serverUnavailable();
        return false;
    }

    /**
     * Task for the Client.
     * Takes the messages from the model and apply the changes to the view.
     */
    @Override
    public void run() {
        while (!stopped) {
            try {
                Message message = queue.take();
                if (MessageType.retrieveByMessage(message) != MessageType.IGNORE) {
                    System.out.println("CL : " + message.getClass().getName());
                    updateView(message);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Client: stopped!!");
    }

    /**
     * Method for updating the view based on the received  messages.
     *
     * @param message a Message from the model.
     */
    public void updateView(Message message) {
        System.out.println(MessageBuilder.toJson(message));

        switch (MessageType.retrieveByMessage(message)) {
            case COMM_MESSAGE -> {
                switch (((CommMessage) message).getType()) {
                    case CHOOSE_GAME -> userInterface.chooseGame();
                    case CAN_START -> userInterface.hostCanStart();
                    case ERROR_CANT_START -> userInterface.hostCantStart();
                    case ERROR_TIMEOUT, ERROR_SERVER_UNAVAILABLE -> userInterface.serverUnavailable();
                    case WAITING -> userInterface.showWaiting();
                    default -> userInterface.showCommMessage((CommMessage) message);
                }
            }
            case AVAILABLE_WIZARDS -> userInterface.setWizardView(((AvailableWizards) message).getWizardsView());
            case CURRENT_TEAMS -> userInterface.setTeamsView(((CurrentTeams) message).getTeamsView());
            case PLAY_ASSISTANT_CARD, MULTIPLE_POSSIBLE_MOVES, CHOOSE_CLOUD, CHOOSE_ISLAND, CHOOSE_STUDENT_COLOR, MOVE_MOTHER_NATURE, MOVE_STUDENT, SWAP_STUDENTS ->
                    userInterface.setPossibleActions(message);

            case CURRENT_GAME_STATE -> userInterface.setGameView(((CurrentGameState) message).getGameView());
            case WINNERS -> userInterface.showWinners(((Winners) message));
        }
    }

    /**
     * End the connection
     */
    public void closeConnection() {
        if(communicationHandler != null)
            communicationHandler.stop();
    }

    /**
     * Invoked when a client disconnects from the server and vice versa.
     *
     * @param event the event object
     */
    @Override
    public void onDisconnect(DisconnectEvent event) {
        closeConnection();
        userInterface.serverUnavailable();
        stopped = true;
        queue.add(new IgnoreMessage());
    }

    /**
     * Validates the server string.
     *
     * @param server the server hostname string
     * @return true if the string is valid, false otherwise
     */
    public static boolean validateServerString(String server) {
        String ipPattern = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        String domainPattern = "^((?!-)[A-Za-z\\d-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
        return server.matches(ipPattern) || server.matches(domainPattern) || server.equals("localhost");
    }

    /**
     * Validates the port string.
     *
     * @param port the port string
     * @return true if the string is valid, false otherwise
     */
    public static boolean validateServerPort(String port) {
        return port.matches("^\\d*$") && Integer.parseInt(port) > 0 && Integer.parseInt(port) < 65536;
    }
}
