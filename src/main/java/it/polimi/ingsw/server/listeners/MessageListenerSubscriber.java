package it.polimi.ingsw.server.listeners;

/**
 * Interface for the message listener subscriber.
 */
public interface MessageListenerSubscriber {

    /**
     * Adds a message listener.
     * @param listener the listener to add
     */
    void addListener(MessageListener listener);

    /**
     * Removes a message listener.
     * @param listener the listener to remove
     */
    void removeListener(MessageListener listener);

    /**
     * Notifies all listeners.
     * @param event of the message to notify
     */
    void notifyListeners(MessageEvent event);
}
