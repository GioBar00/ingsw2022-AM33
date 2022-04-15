package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.network.messages.Message;

import java.util.EnumSet;

/**
 * This message tells the player to play an assistant card.
 */
public class PlayAssistantCard extends Message {

    /**
     * playable assistant cards.
     */
    private final EnumSet<AssistantCard> playableAssistantCards;

    /**
     * Creates message.
     * @param cards playable assistant cards.
     */
    public PlayAssistantCard(EnumSet<AssistantCard> cards) {
        playableAssistantCards = cards;
    }

    /**
     * @return playable assistant cards.
     */
    public EnumSet<AssistantCard> getPlayableAssistantCards() {
        return EnumSet.copyOf(playableAssistantCards);
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return playableAssistantCards != null && !playableAssistantCards.isEmpty();
    }
}
