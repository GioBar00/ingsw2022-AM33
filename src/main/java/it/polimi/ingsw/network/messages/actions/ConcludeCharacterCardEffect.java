package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.Action;

/**
 * This message signifies that the player concluded the activated character card effect.
 */
public class ConcludeCharacterCardEffect implements Action {

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return true;
    }
}
