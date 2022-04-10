package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

public class ActivatedCharacterCard extends Message {

    private final Integer characterCardIndex;

    public ActivatedCharacterCard(Integer characterCardIndex) {
        this.characterCardIndex = characterCardIndex;
    }

    public Integer getCharacterCardIndex() {
        return characterCardIndex;
    }

    @Override
    public boolean isValid() {
        return characterCardIndex != null;
    }
}
