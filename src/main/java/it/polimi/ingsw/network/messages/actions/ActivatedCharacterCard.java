package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.Action;

/**
 * This message signifies that the player activated a character card.
 */
public class ActivatedCharacterCard implements Action {

    /**
     * index of the character card that was activated.
     */
    private final Integer characterCardIndex;

    /**
     * Creates the message
     *
     * @param characterCardIndex index of the activated character card.
     */
    public ActivatedCharacterCard(Integer characterCardIndex) {
        this.characterCardIndex = characterCardIndex;
    }

    /**
     * @return index of the activated character card.
     */
    public Integer getCharacterCardIndex() {
        return characterCardIndex;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return characterCardIndex != null;
    }
}
