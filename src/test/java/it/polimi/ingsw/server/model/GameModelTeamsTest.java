package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link GameModelTeams} class.
 */
class GameModelTeamsTest {

    private final PlayerConvertor pC = new PlayerConvertor();


    /**
     * tests that the players are added correctly to the game: the four players should be divided into the two
     * teams and attributed the right number and color of towers. If the model tries to add more player than
     * it's supposed to or add a player with the same nickname as a previous one, the correct exception
     * should be thrown.
     */
    @Test
    void addPlayer() {
        GameModel gmTeams = new GameModelTeams();

        assertFalse(gmTeams.removePlayer("whiteLeader"));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("whiteLeader", Wizard.SENSEI, Tower.WHITE)));
        assertTrue(gmTeams.removePlayer("whiteLeader"));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("whiteLeader", Wizard.SENSEI, Tower.WHITE)));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("blackLeader", Wizard.SENSEI, Tower.BLACK)));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("whiteOther", Wizard.SENSEI, Tower.WHITE)));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("blackOther", Wizard.SENSEI, Tower.BLACK)));


        Player pl1 = gmTeams.playersManager.getPlayers().get(0);
        Player pl2 = gmTeams.playersManager.getPlayers().get(1);
        Player pl3 = gmTeams.playersManager.getPlayers().get(2);
        Player pl4 = gmTeams.playersManager.getPlayers().get(3);

        assertEquals(gmTeams.playersManager.getPlayers().get(0).getNickname(), "whiteLeader");
        assertEquals(gmTeams.playersManager.getSchoolBoard(pl1).getTower(), Tower.WHITE);
        assertEquals(8, gmTeams.playersManager.getSchoolBoard(pl1).getNumTowers());

        assertEquals(gmTeams.playersManager.getPlayers().get(1).getNickname(), "blackLeader");
        assertEquals(gmTeams.playersManager.getSchoolBoard(pl2).getTower(), Tower.BLACK);
        assertEquals(8, gmTeams.playersManager.getSchoolBoard(pl2).getNumTowers());

        assertEquals(gmTeams.playersManager.getPlayers().get(2).getNickname(), "whiteOther");
        assertEquals(gmTeams.playersManager.getSchoolBoard(pl3).getTower(), Tower.WHITE);
        assertEquals(0, gmTeams.playersManager.getSchoolBoard(pl3).getNumTowers());

        assertEquals(gmTeams.playersManager.getPlayers().get(3).getNickname(), "blackOther");
        assertEquals(gmTeams.playersManager.getSchoolBoard(pl4).getTower(), Tower.BLACK);
        assertEquals(0, gmTeams.playersManager.getSchoolBoard(pl4).getNumTowers());

        assertFalse(gmTeams.addPlayer(pC.getPlayer("whiteOther", Wizard.SENSEI, Tower.WHITE)));
    }

    /**
     * test for the method swapTowers: it's checked that the swapping of a single tower works, that the swapping of
     * a group of towers works and that if the method removes all the remaining towers from the player,
     * that player is named the winner of the game.
     */
    @Test
    void swapTowers() {
        GameModel gmTeams = new GameModelTeams();

        assertTrue(gmTeams.addPlayer(pC.getPlayer("whiteLeader", Wizard.SENSEI, Tower.WHITE)));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("blackLeader", Wizard.SENSEI, Tower.BLACK)));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("whiteOther", Wizard.SENSEI, Tower.WHITE)));
        assertTrue(gmTeams.addPlayer(pC.getPlayer("blackOther", Wizard.SENSEI, Tower.BLACK)));

        Player pl1 = gmTeams.playersManager.getPlayers().get(0);
        Player pl2 = gmTeams.playersManager.getPlayers().get(1);
        Player pl3 = gmTeams.playersManager.getPlayers().get(2);
        Player pl4 = gmTeams.playersManager.getPlayers().get(3);

        gmTeams.islandsManager.setTower(Tower.WHITE, 0);

        assertTrue(gmTeams.playersManager.getSchoolBoard(pl1).removeTowers(1));

        assertEquals(gmTeams.islandsManager.getTower(0), Tower.WHITE);
        assertEquals(7, gmTeams.playersManager.getSchoolBoard(pl1).getNumTowers());

        gmTeams.swapTowers(0, Tower.BLACK);

        assertEquals(8, gmTeams.playersManager.getSchoolBoard(pl1).getNumTowers());
        assertEquals(7, gmTeams.playersManager.getSchoolBoard(pl2).getNumTowers());
        assertEquals(0, gmTeams.playersManager.getSchoolBoard(pl3).getNumTowers());
        assertEquals(0, gmTeams.playersManager.getSchoolBoard(pl4).getNumTowers());
        assertEquals(gmTeams.islandsManager.getTower(0), Tower.BLACK);

        for (int i = 7; i > 0; i--) {
            gmTeams.islandsManager.setTower(Tower.WHITE, i);
            gmTeams.checkMergeIslandGroups(i);
        }
        assertTrue(gmTeams.playersManager.getSchoolBoard(pl1).removeTowers(7));


        gmTeams.swapTowers(1, Tower.BLACK);
        assertEquals(0, gmTeams.playersManager.getSchoolBoard(pl2).getNumTowers());
        assertTrue(gmTeams.roundManager.getWinners().contains(gmTeams.playersManager.getSchoolBoard(pl2).getTower()));
    }

}