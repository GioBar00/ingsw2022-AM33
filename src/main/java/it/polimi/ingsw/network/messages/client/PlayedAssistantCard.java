package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.network.messages.Message;

/**
 * This message signifies that the player played an assistant card.
 */
public class PlayedAssistantCard extends Message {

    /**
     * assistant card that was played.
     */
    private final AssistantCard assistantCard;

    /**
     * Creates the message
     * @param card played.
     */
    public PlayedAssistantCard(AssistantCard card) {
        assistantCard = card;
    }

    /**
     * @return played assistant card.
     */
    public AssistantCard getAssistantCard() {
        return assistantCard;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return assistantCard != null;
    }
}
