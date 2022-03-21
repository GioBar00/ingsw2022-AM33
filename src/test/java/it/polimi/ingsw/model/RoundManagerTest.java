package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoundManagerTest {

    @Test
    void calculatePlayerOrder() {
        // check with different cards
        Player[] players = {
                new Player("p1", Wizard.ONE, new SchoolBoard(7, Tower.BLACK, 8)),
                new Player("p2", Wizard.TWO, new SchoolBoard(7, Tower.WHITE, 8)),
                new Player("p3", Wizard.THREE, new SchoolBoard(7, Tower.BLACK, 0)),
                new Player("p4", Wizard.FOUR, new SchoolBoard(7, Tower.WHITE, 0))
        };
        players[0].playAssistantCard(AssistantCard.ONE);
        players[1].playAssistantCard(AssistantCard.FIVE);
        players[2].playAssistantCard(AssistantCard.EIGHT);
        players[3].playAssistantCard(AssistantCard.FOUR);
        RoundManager rm = new RoundManager(players.length);
        rm.playerOrderIndexes = new Integer[]{0, 1, 2, 3};
        rm.calculatePlayerOrder(players);
        Integer[] expected = new Integer[]{0, 3, 1, 2};
        for(int i = 0; i < rm.playerOrderIndexes.length; i++) {
            assertEquals(rm.playerOrderIndexes[i], expected[i]);
        }

        // check with some equal cards
        players[0].playAssistantCard(AssistantCard.THREE);
        players[1].playAssistantCard(AssistantCard.TEN);
        players[2].playAssistantCard(AssistantCard.SIX);
        players[3].playAssistantCard(AssistantCard.THREE);
        rm.playerOrderIndexes = new Integer[]{3, 1, 2, 0};
        rm.calculatePlayerOrder(players);
        expected = new Integer[]{3, 0, 2, 1};
        for(int i = 0; i < rm.playerOrderIndexes.length; i++) {
            assertEquals(rm.playerOrderIndexes[i], expected[i]);
        }
    }
}