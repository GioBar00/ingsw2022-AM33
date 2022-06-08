package it.polimi.ingsw.network.listeners;

/**
 * Interface for the message listener subscriber.
 */
public interface MessageListenerSubscriber {

    /**
     * Adds a message listener.
     *
     * @param listener the listener to add
     */
    void addMessageListener(MessageListener listener);

    /**
     * Removes a message listener.
     *
     * @param listener the listener to remove
     */
    void removeMessageListener(MessageListener listener);

    /**
     * Notifies all listeners.
     *
     * @param event of the message to notify
     */
    void notifyMessageListeners(MessageEvent event);

    /**
     * Notifies a specific listener.
     *
     * @param identifier of the listener to notify
     * @param event      of the message to notify
     */
    void notifyMessageListener(String identifier, MessageEvent event);
}
