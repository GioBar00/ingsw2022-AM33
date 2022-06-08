package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

/**
 * Message for request the start of a game
 */
public class StartGame implements Message {

    /**
     * Used to check the validity of the message
     * @return true if the message is valid
     */
    public boolean isValid() {
        return true;
    }

}
