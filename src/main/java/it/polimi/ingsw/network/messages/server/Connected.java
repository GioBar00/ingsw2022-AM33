package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;

/**
 * Internal message to request the current game state
 */
public class Connected implements Message {
    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return true;
    }
}
