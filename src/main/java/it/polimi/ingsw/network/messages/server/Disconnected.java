package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;

/**
 * This message is used to notify the controller if the client is disconnected
 */
public class Disconnected implements Message {
    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return true;
    }
}
