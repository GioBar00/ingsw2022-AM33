package it.polimi.ingsw.client;

import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.network.MessageHandler;
import it.polimi.ingsw.network.listeners.DisconnectEvent;
import it.polimi.ingsw.network.listeners.DisconnectListener;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.CurrentGameState;
import it.polimi.ingsw.network.messages.server.CurrentTeams;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class for client-side communication
 */
public class Client implements MessageHandler, ViewListener, Runnable , DisconnectListener {

    /**
     * the hostname for the connection
     */
    private final String hostname;

    /**
     * the port for the connection
     */
    private final int port;

    /**
     * The message exchange handler used to send and receive messages to and from the client
     */
    private final CommunicationHandler communicationHandler;

    /**
     * The interface for the user
     */
    private final UI userInterface;

    /**
     * Queue with arrived messages
     */
    private final LinkedBlockingQueue<Message> queue;

    /**
     * The executor used to handle the message exchange.
     */
    private ExecutorService executor;

    /**
     * Constructor of Virtual Server
     */
    public Client(String hostname, int port, UI userInterface) {
        this.hostname = hostname;
        this.port = port;
        executor = Executors.newSingleThreadExecutor();
        executor.shutdownNow();
        queue = new LinkedBlockingQueue<>();
        this.communicationHandler = new CommunicationHandler(this);
        this.userInterface = userInterface;
        userInterface.setViewListener(this);
    }

    /**
     * This method sets up the connection and starts the communicationHandler.
     */
    public void startConnection(){
        try {
            communicationHandler.setSocket(new Socket(hostname, port));
            communicationHandler.setDisconnectListener(this);
            communicationHandler.start();
            if(executor.isShutdown()) {
                executor = Executors.newSingleThreadExecutor();
                executor.submit(this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called when a message is received.
     * @param message the message received.
     */
    @Override
    public void handleMessage(Message message) {
        System.out.println("C: received message - " + MessageBuilder.toJson(message));
        queue.add(message);
    }

    /**
     * Method called when the user want to update the model.
     * @param message the request.
     */
    @Override
    public void onMessage(Message message) {
        if(MessageType.retrieveByMessage(message) == MessageType.LOGIN) {
                startConnection();
        }
        System.out.println("C: sending message - " + MessageBuilder.toJson(message));
        communicationHandler.sendMessage(message);
    }

    /**
     * Task for the Client.
     * Takes the messages from the model and apply the changes to the view.
     */
    @Override
    public void run() {
        Message message;
        while(!Thread.interrupted()){
            try {
                message = queue.take();
                System.out.println("CL : " + message.getClass().getName());
                updateView(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Method for updating the view based on the received  messages.
     * @param message a Message from the model.
     */
    public synchronized void updateView(Message message){
        System.out.println(MessageBuilder.toJson(message));

        switch (MessageType.retrieveByMessage(message)){
            case COMM_MESSAGE -> {
                switch (((CommMessage)message).getType()){
                    case CHOOSE_GAME -> userInterface.chooseGame();
                    case CAN_START -> userInterface.hostCanStart();
                    case ERROR_CANT_START -> userInterface.hostCantStart();
                    default -> userInterface.showCommMessage((CommMessage)message);
                }
            }
            case AVAILABLE_WIZARDS -> {
                userInterface.setWizardView(((AvailableWizards)message).getWizardsView());
                userInterface.showWizardMenu();
            }
            case CURRENT_TEAMS -> userInterface.setTeamsView(((CurrentTeams)message).getTeamsView());
            case PLAY_ASSISTANT_CARD, MULTIPLE_POSSIBLE_MOVES, CHOOSE_CLOUD, CHOOSE_ISLAND, CHOOSE_STUDENT_COLOR, MOVE_MOTHER_NATURE, MOVE_STUDENT, SWAP_STUDENTS ->
                    userInterface.setPossibleMoves(message);

            case CURRENT_GAME_STATE -> {
                userInterface.setGameView(((CurrentGameState)message).getGameView());
                userInterface.showGameScreen();
            }
        }
    }

    /**
     * End the connection
     */
    public void closeConnection(){
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
        userInterface.close();
        executor.shutdownNow();
    }
}
