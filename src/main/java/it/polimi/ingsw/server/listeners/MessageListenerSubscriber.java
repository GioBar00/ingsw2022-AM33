package it.polimi.ingsw.server.listeners;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to manage message listeners.
 */
public abstract class MessageListenerSubscriber {

    /**
     * List of message listeners.
     */
    private final List<MessageListener> listeners = new LinkedList<>();

    /**
     * Adds a message listener.
     * @param listener the listener to add
     */
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a message listener.
     * @param listener the listener to remove
     */
    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners.
     * @param event of the message to notify
     */
    public void notifyListeners(MessageEvent event) {
        for (MessageListener listener : listeners) {
            listener.onMessage(event);
        }
    }
}
