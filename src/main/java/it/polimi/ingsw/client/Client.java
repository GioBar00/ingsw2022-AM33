package it.polimi.ingsw.client;

import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.network.MessageHandler;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.CurrentTeams;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class for client-side communication
 */
public class Client implements MessageHandler, ViewListener, Runnable {

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
     * Constructor of Virtual Server
     */
    Client(String hostname, int port, UI userInterface) {
        this.hostname = hostname;
        this.port = port;
        queue = new LinkedBlockingQueue<>();
        this.communicationHandler = new CommunicationHandler(this);
        this.userInterface = userInterface;
        userInterface.setViewListener(this);
    }

    /**
     * This method sets up the connection and starts the communicationHandler.
     */
    private void startConnection(){
        try {
            communicationHandler.setSocket(new Socket(hostname, port));
            communicationHandler.start();
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
        queue.add(message);
    }

    /**
     * Method called when the user want to update the model.
     * @param event the request.
     */
    @Override
    public void onMessage(Message event) {
        if(MessageType.retrieveByMessage(event) == MessageType.LOGIN) {
                startConnection();
                communicationHandler.sendMessage(event);
        }
        communicationHandler.sendMessage(event);
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
    public void updateView(Message message){
        switch (MessageType.retrieveByMessage(message)){
            case COMM_MESSAGE -> {
                switch (((CommMessage)message).getType()){
                    case CHOOSE_GAME ->    userInterface.setHost();
                    default -> userInterface.showError((CommMessage)message);
                }
            }
            case AVAILABLE_WIZARDS -> {
                userInterface.setWizardView(((AvailableWizards)message).getWizardsView());
                userInterface.showWizardMenu();
            }
            case CURRENT_TEAMS -> {
                userInterface.setTeamsView(((CurrentTeams)message).getTeamsView());
                userInterface.showLobbyScreen();
            }
        }
    }
}
