package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

/**
 * This message is used to notify the controller if the client is disconnected
 */
public class SkipTurn implements Message {
    @Override
    public boolean isValid() {
        return true;
    }
}
