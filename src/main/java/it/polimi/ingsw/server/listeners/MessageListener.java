package it.polimi.ingsw.server.listeners;

import java.util.EventListener;

/**
 * Interface for the message listener
 */
public interface MessageListener extends EventListener {
    /**
     * Method called when a message is received
     * @param event of the received message
     */
    void onMessage(MessageEvent event);
}
