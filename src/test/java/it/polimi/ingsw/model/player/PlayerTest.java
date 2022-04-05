package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enums.AssistantCard;
import it.polimi.ingsw.enums.Tower;
import it.polimi.ingsw.enums.Wizard;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    Player p = new Player("p1", Wizard.TWO, new SchoolBoard(7, Tower.GREY, 6));

    /**
     * check the correct implementation of playAssistantCard,clearPlayedCard and getHand by forcing the remove in an empty list
     */
    @Test
    void assistantCardHandling() {
        // check that player can play every card
        for(AssistantCard c: AssistantCard.values()) {
            assertTrue(p.playAssistantCard(c));
            assertEquals(c,p.getAssistantCard());
            assertFalse(p.getHand().contains(c));
            p.clearPlayedCard();
            assertNull(p.getAssistantCard());
        }
        // check clearPlayedCard
        p.clearPlayedCard();
        assertNull(p.getAssistantCard());
        // check that there are no more cards to be played
        for(AssistantCard c: AssistantCard.values()) {
            assertFalse(p.playAssistantCard(c));
        }
    }
}