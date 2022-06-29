package it.polimi.ingsw.server;

import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.network.MessageHandler;
import it.polimi.ingsw.network.listeners.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.Disconnected;

/**
 * Class for handle connection between a specified player and the server. Virtual Client forward valid request to the
 * controller
 */
public class VirtualClient extends ConcreteMessageListenerSubscriber implements MessageListener, MessageHandler, DisconnectListener, DisconnectListenerSubscriber {

    /**
     * Nickname of the player who interfaces this VirtualClient
     */
    private final String identifier;

    /**
     * The message exchange handler used to send and receive messages to and from the client
     */
    protected CommunicationHandler communicationHandler;

    /**
     * The disconnect listener used to notify the server that the client has disconnected.
     */
    private DisconnectListener disconnectListener;

    /**
     * Constructor of VirtualClient
     *
     * @param identifier the nickname of the player that interfaces with this VirtualClient
     */
    public VirtualClient(String identifier) {
        this.identifier = identifier;
        communicationHandler = null;
    }

    /**
     * Identifier getter
     *
     * @return the nickname
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Method used to know if the client is still connected
     *
     * @return true if is connected, false in other case
     */
    public synchronized boolean isConnected() {
        return communicationHandler.isConnected();
    }

    /**
     * Setting the CommunicationHandler to the virtualClient. Used in case of creation or reconnection
     *
     * @param communicationHandler the communication handler of the communication
     */
    public synchronized void setCommunicationHandler(CommunicationHandler communicationHandler) {
        if (this.communicationHandler != null) {
            this.communicationHandler.setMessageHandler(e -> {});
            this.communicationHandler.setDisconnectListener(e -> {});
            this.communicationHandler.stop();
        }
        this.communicationHandler = communicationHandler;
    }

    /**
     * Reconnects the virtual client.
     *
     * @param ch the communication handler of the communication
     */
    public void reconnect(CommunicationHandler ch) {
        setCommunicationHandler(ch);
        ch.setDisconnectListener(this);
        ch.setMessageHandler(this);
    }

    /**
     * This method stops the message exchange handler
     */
    public void stop() {
        communicationHandler.stop();
    }

    /**
     * Send a Message to the client
     */
    public void sendMessage(Message message) {
        if (isConnected()) {
//            System.out.println("VC : send to " + identifier);
//            System.out.println(MessageBuilder.toJson(message));
            communicationHandler.sendMessage(message);
        }
    }


    /**
     * Methods from MessageListener Interface for notify to the VirtualClient changes in the model
     *
     * @param event of the received message
     */
    @Override
    public synchronized void onMessage(MessageEvent event) {
        if (!isConnected()) notifyMessageListeners(new MessageEvent(this, new Disconnected()));
        else sendMessage(event.getMessage());

    }

    /**
     * This method is called when a message is received.
     *
     * @param message the message received
     */
    @Override
    public void handleMessage(Message message) {
//        System.out.println("VC " + identifier + ": received message ");
//        System.out.println(MessageBuilder.toJson(message));
        notifyMessageListeners(new MessageEvent(this, message));
    }

    /**
     * Invoked when a client disconnects from the server and vice versa.
     *
     * @param event the event object
     */
    @Override
    public void onDisconnect(DisconnectEvent event) {
        System.out.println("VC " + identifier + ": disconnected");
        notifyDisconnectListener(new DisconnectEvent(this));
    }

    /**
     * Sets the disconnection listener.
     *
     * @param listener the listener to set
     */
    @Override
    public void setDisconnectListener(DisconnectListener listener) {
        disconnectListener = listener;
    }

    /**
     * Notifies the listener that a disconnection has occurred.
     *
     * @param event the event to notify
     */
    @Override
    public void notifyDisconnectListener(DisconnectEvent event) {
        if (disconnectListener != null) disconnectListener.onDisconnect(event);
    }
}

