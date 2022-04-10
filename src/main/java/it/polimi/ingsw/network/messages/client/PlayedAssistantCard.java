package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

public class PlayedAssistantCard extends Message {

    private final Integer assistantCardIndex;

    public PlayedAssistantCard(Integer index) {
        assistantCardIndex = index;
    }

    public int getAssistantCardIndex() {
        return assistantCardIndex;
    }


    @Override
    public boolean isValid() {
        return assistantCardIndex != null;
    }
}
