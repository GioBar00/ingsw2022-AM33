package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;

import java.util.List;

public class PlayAssistantCard extends Message {
    private final List<Integer> playableAssistantCardsIndexes;

    public PlayAssistantCard(List<Integer> cardIndexes) {
        playableAssistantCardsIndexes = cardIndexes;
    }

    public List<Integer> getPlayableAssistantCardsIndexes() {
        return playableAssistantCardsIndexes;
    }

    @Override
    public boolean isValid() {
        return playableAssistantCardsIndexes != null;
    }
}
