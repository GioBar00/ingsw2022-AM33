package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.player.PlayersManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayersManagerTest {
    PlayersManager pm = new PlayersManager(GamePreset.THREE);

    /**
     * Test the methods addPlayer, tries to add a new player when there's no space left and to add a player with an existing nickname
     */
    @Test
    void CreatePm(){
        //TestingAdd
        assertTrue(pm.addPlayer("Pl1"));

        assertTrue(pm.addPlayer("Pl2"));
        assertTrue(pm.removePlayer("Pl2"));
        assertFalse(pm.removePlayer("Pl3"));
        assertTrue(pm.addPlayer("Pl2"));
        assertFalse(pm.addPlayer("Pl1"));
        assertEquals(1,pm.getAvailablePlayerSlots());
        assertTrue(pm.addPlayer("Pl3"));
        assertFalse(pm.addPlayer("Pl4"));
    }

    /**
     * Check the correct implementation of nextPlayer and getPlayer by cycling on the players order
     */
    @Test
    void playersManagerTest() {
        assertTrue(pm.addPlayer("Pl1"));
        assertTrue(pm.addPlayer("Pl2"));
        assertTrue(pm.addPlayer("Pl3"));

        //Check slots
        assertEquals(0,pm.getAvailablePlayerSlots());

        //Check arrayList
        assertEquals("Pl1",pm.getCurrentPlayer().getNickname());
        assertEquals("Pl1",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl2",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(2).getNickname());
        assertEquals("Pl1",pm.getCurrentPlayer().getNickname());

        //Check next
        pm.nextPlayer();
        assertEquals("Pl2",pm.getCurrentPlayer().getNickname());
        pm.nextPlayer();
        pm.nextPlayer();
        assertEquals("Pl1",pm.getCurrentPlayer().getNickname());
    }

    /**
     * Test the right implementation of calculatePlayerOrder assign a new played card to each player. A limit case is when we have more player playing the same card
     */
    @Test
    void SortingTest(){
        assertTrue(pm.addPlayer("Pl1"));
        assertTrue(pm.addPlayer("Pl2"));
        assertTrue(pm.addPlayer("Pl3"));

        assertTrue(pm.currentPlayerPlayed(AssistantCard.FOUR));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.TWO));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.FOUR));
        pm.nextPlayer();

        pm.calculatePlayerOrder();
        assertEquals("Pl2",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(2).getNickname());
        assertTrue(pm.currentPlayerPlayed(AssistantCard.SEVEN));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.TWO));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.ONE));

        pm.calculatePlayerOrder();
        assertEquals("Pl3",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl2",pm.getPlayers().get(2).getNickname());
    }

    /**
     * Test the right implementation of clockWiseOrder assign a new played card to each player.
     */
    @Test
    void clockwiseTest (){
        assertTrue(pm.addPlayer("Pl1"));
        assertTrue(pm.addPlayer("Pl2"));
        assertTrue(pm.addPlayer("Pl3"));

        assertTrue(pm.currentPlayerPlayed(AssistantCard.FOUR));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.TWO));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.FOUR));
        pm.nextPlayer();

        pm.calculatePlayerOrder();
        assertEquals("Pl2",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(2).getNickname());
        pm.calculateClockwiseOrder();

        assertEquals("Pl2",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(2).getNickname());


    }

    /**
     * Test the implementation of setFirstPlayer
     */
    @Test
    void firstPlayerTest(){
        assertTrue(pm.addPlayer("Pl1"));
        assertTrue(pm.addPlayer("Pl2"));
        assertTrue(pm.addPlayer("Pl3"));

        pm.setFirstPlayer(2 - 1);
        assertEquals("Pl2",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(2).getNickname());

        pm.setFirstPlayer(3 - 1);
        assertEquals("Pl3",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl2",pm.getPlayers().get(2).getNickname());

        pm.setFirstPlayer(0);
        assertEquals("Pl1",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl2",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(2).getNickname());
    }
}