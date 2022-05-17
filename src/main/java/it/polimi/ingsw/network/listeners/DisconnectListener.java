package it.polimi.ingsw.network.listeners;

import java.util.EventListener;

/**
 * Interface for the disconnect listener
 */
public interface DisconnectListener extends EventListener {

    /**
     * Invoked when a client disconnects from the server and vice versa.
     *
     * @param event the event object
     */
    void onDisconnect(DisconnectEvent event);
}
