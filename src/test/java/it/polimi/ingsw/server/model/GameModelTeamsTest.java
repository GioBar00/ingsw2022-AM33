package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.GameModel;
import it.polimi.ingsw.server.model.GameModelTeams;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTeamsTest {

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
        assertTrue(gmTeams.addPlayer("whiteLeader"));
        assertTrue(gmTeams.removePlayer("whiteLeader"));
        assertTrue(gmTeams.addPlayer("whiteLeader"));
        assertFalse(gmTeams.addPlayer("whiteLeader"));
        assertTrue(gmTeams.addPlayer("blackLeader"));
        assertTrue(gmTeams.addPlayer("whiteOther"));
        assertTrue(gmTeams.addPlayer("blackOther"));

        assertTrue(gmTeams.changeTeam("whiteLeader", Tower.WHITE));
        assertTrue(gmTeams.changeTeam("blackLeader", Tower.BLACK));
        assertTrue(gmTeams.changeTeam("whiteOther", Tower.WHITE));
        assertTrue(gmTeams.changeTeam("blackOther", Tower.BLACK));
        assertFalse(gmTeams.changeTeam("lallo", Tower.BLACK));
        assertFalse(gmTeams.changeTeam("whiteLeader", Tower.GREY));

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

        assertFalse(gmTeams.addPlayer("extra"));
    }

    /**
     * test for the method swapTowers: it's checked that the swapping of a single tower works, that the swapping of
     * a group of towers works and that if the method removes all the remaining towers from the player,
     * that player is named the winner of the game.
     */
    @Test
    void swapTowers(){
        GameModel gmTeams = new GameModelTeams();

        assertTrue(gmTeams.addPlayer("whiteLeader"));
        assertTrue(gmTeams.addPlayer("blackLeader"));
        assertTrue(gmTeams.addPlayer("whiteOther"));
        assertTrue(gmTeams.addPlayer("blackOther"));

        assertTrue(gmTeams.changeTeam("whiteLeader", Tower.WHITE));
        assertTrue(gmTeams.changeTeam("blackLeader", Tower.BLACK));
        assertTrue(gmTeams.changeTeam("whiteOther", Tower.WHITE));
        assertTrue(gmTeams.changeTeam("blackOther", Tower.BLACK));

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

        for(int i = 7; i > 0; i--){
            gmTeams.islandsManager.setTower(Tower.WHITE, i);
            gmTeams.checkMergeIslandGroups(i);
        }
        assertTrue(gmTeams.playersManager.getSchoolBoard(pl1).removeTowers(7));


        gmTeams.swapTowers(1, Tower.BLACK);
        assertEquals(0, gmTeams.playersManager.getSchoolBoard(pl2).getNumTowers());
        assertTrue(gmTeams.roundManager.getWinners().contains(gmTeams.playersManager.getSchoolBoard(pl2).getTower()));
    }

    /**
     * check that the members of the teams are changed correctly (in the case that the teams where already formed)
     */
    @Test
    void changeSidesTest(){
        GameModelTeams gmTeams = new GameModelTeams();

        assertTrue(gmTeams.addPlayer("whiteLeader"));
        assertTrue(gmTeams.addPlayer("blackLeader"));
        assertTrue(gmTeams.addPlayer("traitor1"));
        assertTrue(gmTeams.addPlayer("traitor2"));

        assertTrue(gmTeams.changeTeam("whiteLeader", Tower.WHITE));
        assertTrue(gmTeams.changeTeam("blackLeader", Tower.BLACK));
        assertTrue(gmTeams.changeTeam("traitor1", Tower.WHITE));
        assertTrue(gmTeams.changeTeam("traitor2", Tower.BLACK));

        Player wl = gmTeams.playersManager.getPlayers().get(0);
        Player bl = gmTeams.playersManager.getPlayers().get(1);
        Player t1 = gmTeams.playersManager.getPlayers().get(2);
        assertEquals("traitor1", t1.getNickname());
        Player t2 = gmTeams.playersManager.getPlayers().get(3);
        assertEquals("traitor2", t2.getNickname());

        assertTrue(gmTeams.playersManager.getTeams().get(Tower.WHITE).contains(wl));
        assertTrue(gmTeams.playersManager.getTeams().get(Tower.WHITE).contains(t1));
        assertTrue(gmTeams.playersManager.getTeams().get(Tower.BLACK).contains(bl));
        assertTrue(gmTeams.playersManager.getTeams().get(Tower.BLACK).contains(t2));

        gmTeams.playersManager.changeTeam("traitor1", Tower.BLACK);
        assertEquals("traitor1", gmTeams.playersManager.getTeams().get(Tower.BLACK).get(2).getNickname());
        assertTrue(gmTeams.playersManager.getTeams().get(Tower.WHITE).contains(wl));
        assertTrue(gmTeams.playersManager.getTeams().get(Tower.BLACK).contains(bl));
        assertTrue(gmTeams.playersManager.getTeams().get(Tower.BLACK).contains(t2));

        gmTeams.playersManager.changeTeam("traitor2", Tower.WHITE);
        assertEquals("traitor2", gmTeams.playersManager.getTeams().get(Tower.WHITE).get(1).getNickname());
        assertTrue(gmTeams.playersManager.getTeams().get(Tower.WHITE).contains(wl));
        assertTrue(gmTeams.playersManager.getTeams().get(Tower.BLACK).contains(bl));
        assertEquals("traitor1", gmTeams.playersManager.getTeams().get(Tower.BLACK).get(1).getNickname());
    }

    @Test
    void leaderChangesTeam(){
        GameModelTeams gmTeams = new GameModelTeams();

        assertTrue(gmTeams.addPlayer("whiteLeader"));
        assertTrue(gmTeams.addPlayer("blackLeader"));
        assertTrue(gmTeams.addPlayer("nextWhiteLeader"));
        assertTrue(gmTeams.addPlayer("otherBlack"));

        assertTrue(gmTeams.changeTeam("whiteLeader", Tower.WHITE));
        assertTrue(gmTeams.changeTeam("blackLeader", Tower.BLACK));
        assertTrue(gmTeams.changeTeam("nextWhiteLeader", Tower.WHITE));
        assertTrue(gmTeams.changeTeam("otherBlack", Tower.BLACK));

        assertTrue(gmTeams.changeTeam("whiteLeader", Tower.BLACK));
        
        Player newLeader = null;
        for(Player p : gmTeams.playersManager.getPlayers()){
            if (p.getNickname().equals("nextWhiteLeader"))
                newLeader = p;
        }

        assert newLeader != null;
        assertEquals(8, gmTeams.playersManager.getSchoolBoard(newLeader).getNumTowers());

    }
}