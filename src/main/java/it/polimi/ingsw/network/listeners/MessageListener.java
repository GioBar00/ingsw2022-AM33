package it.polimi.ingsw.network.listeners;

import java.util.EventListener;

/**
 * Interface for the message listener
 */
public interface MessageListener extends EventListener {
    /**
     * @return identifier of the listener
     */
    default String getIdentifier() {
        return "";
    }

    /**
     * Method called when a message is received
     * @param event of the received message
     */
    void onMessage(MessageEvent event);
}
