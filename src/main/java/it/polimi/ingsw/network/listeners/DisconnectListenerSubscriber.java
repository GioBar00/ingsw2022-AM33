package it.polimi.ingsw.network.listeners;

/**
 * This interface is used to subscribe to the disconnection event.
 */
public interface DisconnectListenerSubscriber {

    /**
     * Sets the disconnection listener.
     *
     * @param listener the listener to set
     */
    void setDisconnectListener(DisconnectListener listener);

    /**
     * Notifies the listener that a disconnection has occurred.
     * @param event the event to notify
     */
    void notifyListener(DisconnectEvent event);
}
