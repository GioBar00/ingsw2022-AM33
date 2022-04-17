package it.polimi.ingsw.server.listeners;

import java.util.EventListener;

/**
 * Interface for the message listener
 */
public interface ConnectionListener extends EventListener {

    /**
     * Method called when a connection is closed
     * @param event of the connection
     */
    void onConnectionEvent(ConnectionEvent event);
}
