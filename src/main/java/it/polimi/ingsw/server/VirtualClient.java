package it.polimi.ingsw.server;

import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.network.MessageHandler;
import it.polimi.ingsw.network.listeners.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.SkipTurn;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.server.CommMessage;

import java.net.Socket;

/**
 * Class for handle connection between a specified player and the server. Virtual Client forward valid request to the
 * controller
 */
public class VirtualClient extends ConcreteMessageListenerSubscriber implements MessageListener, MessageHandler, DisconnectListener {

    /**
     * Nickname of the player who interfaces this VirtualClient
     */
    private final String identifier;

    /**
     * The message exchange handler used to send and receive messages to and from the client
     */
    protected final CommunicationHandler communicationHandler;

    /**
     * Constructor of VirtualClient
     * @param identifier the nickname of the player that interfaces with this VirtualClient
     */
    public VirtualClient(String identifier) {
        this.identifier = identifier;
        communicationHandler = new CommunicationHandler(this, true);
    }

    /**
     * Identifier getter
     * @return the nickname
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Method used for know if the client is still connected
     * @return true if is connected, false in other case
     */
    public synchronized boolean isConnected() {
        return communicationHandler.isConnected();
    }

    /**
     * Adding a socket to the virtualClient. Used in case of creation or reconnection
     *
     * @param newSocket the socket of the communication
     */
    public synchronized void setSocket(Socket newSocket) {
        communicationHandler.setSocket(newSocket);
    }

    /**
     * This method starts the message exchange handler
     */
    public synchronized void start() {
        communicationHandler.start();
    }

    /**
     * This method stops the message exchange handler
     */
    public synchronized void stop() {
        communicationHandler.stop();
    }

    /**
     * Send a Communication Message(ERROR_NOT_YOUR_TURN) to the client
     */
    public void sendNotYourTurnMessage() {
        communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_NOT_YOUR_TURN));
    }

    /**
     * Send a Communication Message(ERROR_IMPOSSIBLE_MOVE) to the client
     */
    public void sendImpossibleMessage() {
        communicationHandler.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
    }


    /**
     * Methods from MessageListener Interface for notify to the VirtualClient changes in the model
     * @param event of the received message
     */
    @Override
    public synchronized void onMessage(MessageEvent event) {
        if (!isConnected())
            notifyListeners(new MessageEvent(this, new SkipTurn()));
        else
            communicationHandler.sendMessage(event.getMessage());

    }

    /**
     * This method is called when a message is received.
     *
     * @param message the message received
     */
    @Override
    public void handleMessage(Message message) {
        System.out.println("VC " + identifier + ": received message ");
        System.out.println(MessageBuilder.toJson(message));
        notifyListeners(new MessageEvent(this, message));
    }

    /**
     * Invoked when a client disconnects from the server and vice versa.
     *
     * @param event the event object
     */
    @Override
    public void onDisconnect(DisconnectEvent event) {
        notifyListeners(new MessageEvent(this, new SkipTurn()));
    }
}

