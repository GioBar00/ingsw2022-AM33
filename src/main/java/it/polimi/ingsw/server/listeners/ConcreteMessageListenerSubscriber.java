package it.polimi.ingsw.server.listeners;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to subscribe to message listeners.
 */
public abstract class ConcreteMessageListenerSubscriber implements MessageListenerSubscriber {
    /**
     * List of message listeners.
     */
    private final List<MessageListener> listeners = new LinkedList<>();

    /**
     * Adds a message listener.
     * @param listener the listener to add
     */
    @Override
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a message listener.
     * @param listener the listener to remove
     */
    @Override
    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners.
     * @param event of the message to notify
     */
    @Override
    public void notifyListeners(MessageEvent event) {
        for (MessageListener listener : listeners) {
            listener.onMessage(event);
        }
    }

    /**
     * Notifies a specific listener.
     *
     * @param identifier of the listener to notify
     * @param event      of the message to notify
     */
    @Override
    public void notifyListener(String identifier, MessageEvent event) {
        for (MessageListener listener : listeners) {
            if (listener.getIdentifier().equals(identifier)) {
                listener.onMessage(event);
            }
        }
    }
}
