package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

/**
 * This message signifies that the player concluded the activated character card effect.
 */
public class ConcludeCharacterCardEffect extends Message {

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return true;
    }
}
