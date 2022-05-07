package it.polimi.ingsw.network.listeners;

import it.polimi.ingsw.network.messages.Message;

/**
 * This interface is used to subscribe to the request event.
 */
public interface ViewListenerSubscriber {

    /**
     * Sets the view listener.
     * @param listener the listener to set
     */
    void setViewListener(ViewListener listener);

    /**
     * Notifies the listener that a request has occurred.
     * @param event the request to notify
     */
    void notifyListener(Message event);
}
