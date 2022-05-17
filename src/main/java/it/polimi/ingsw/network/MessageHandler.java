package it.polimi.ingsw.network;

import it.polimi.ingsw.network.messages.Message;

/**
 * This class is used to handle the received messages.
 */
public interface MessageHandler {
    /**
     * This method is called when a message is received.
     *
     * @param message the message received
     */
    void handleMessage(Message message);

}
