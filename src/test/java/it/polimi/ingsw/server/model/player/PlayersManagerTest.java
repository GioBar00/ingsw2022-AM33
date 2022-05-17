package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.PlayerConvertor;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.Wizard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayersManagerTest {
    PlayersManager pm = new PlayersManager(GamePreset.THREE);

    private final PlayerConvertor pC = new PlayerConvertor();

    /**
     * Test the methods addPlayer, tries to add a new player when there's no space left and to add a player with an existing nickname
     */
    @Test
    void CreatePm(){

        //TestingAdd
        assertTrue(pm.addPlayer(pC.getPlayer("p1",Wizard.WITCH)));
        assertTrue(pm.addPlayer(pC.getPlayer("p2" ,Wizard.WITCH)));
        assertTrue(pm.removePlayer("p2"));
        assertFalse(pm.removePlayer("Pl3"));
        assertTrue(pm.addPlayer(pC.getPlayer("p1",Wizard.WITCH)));
        assertEquals(1,pm.getAvailablePlayerSlots());
        assertTrue(pm.addPlayer(pC.getPlayer("p3",Wizard.WITCH)));
        assertFalse(pm.addPlayer(pC.getPlayer("p4",Wizard.WITCH)));
    }

    /**
     * Check the correct implementation of nextPlayer and getPlayer by cycling on the players order
     */
    @Test
    void playersManagerTest() {
        PlayerConvertor pC = new PlayerConvertor();

        assertTrue(pm.addPlayer(pC.getPlayer("Pl1",Wizard.WITCH)));
        assertTrue(pm.addPlayer(pC.getPlayer("Pl2",Wizard.WITCH)));
        assertTrue(pm.addPlayer(pC.getPlayer("Pl3",Wizard.WITCH)));

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
        PlayerConvertor pC = new PlayerConvertor();

        assertTrue(pm.addPlayer(pC.getPlayer("Pl1",Wizard.WITCH)));
        assertTrue(pm.addPlayer(pC.getPlayer("Pl2",Wizard.WITCH)));
        assertTrue(pm.addPlayer(pC.getPlayer("Pl3",Wizard.WITCH)));

        assertTrue(pm.currentPlayerPlayed(AssistantCard.EAGLE));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.OSTRICH));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.EAGLE));
        pm.nextPlayer();

        pm.calculatePlayerOrder();
        assertEquals("Pl2",pm.getPlayers().get(0).getNickname());
        assertEquals("Pl1",pm.getPlayers().get(1).getNickname());
        assertEquals("Pl3",pm.getPlayers().get(2).getNickname());
        assertTrue(pm.currentPlayerPlayed(AssistantCard.OCTOPUS));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.OSTRICH));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.CHEETAH));

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
        PlayerConvertor pC = new PlayerConvertor();

        assertTrue(pm.addPlayer(pC.getPlayer("Pl1",Wizard.WITCH)));
        assertTrue(pm.addPlayer(pC.getPlayer("Pl2",Wizard.WITCH)));
        assertTrue(pm.addPlayer(pC.getPlayer("Pl3",Wizard.WITCH)));

        assertTrue(pm.currentPlayerPlayed(AssistantCard.EAGLE));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.OSTRICH));
        pm.nextPlayer();
        assertTrue(pm.currentPlayerPlayed(AssistantCard.EAGLE));
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
        PlayerConvertor pC = new PlayerConvertor();

        assertTrue(pm.addPlayer(pC.getPlayer("Pl1",Wizard.WITCH)));
        assertTrue(pm.addPlayer(pC.getPlayer("Pl2",Wizard.WITCH)));
        assertTrue(pm.addPlayer(pC.getPlayer("Pl3",Wizard.WITCH)));

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

