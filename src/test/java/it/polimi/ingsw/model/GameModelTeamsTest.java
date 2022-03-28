package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.Tower;
import it.polimi.ingsw.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;

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

        try {
            gmTeams.addPlayer("whiteLeader");
            assertThrows(NameAlreadyBoundException.class, () -> gmTeams.addPlayer("whiteLeader"));
            gmTeams.addPlayer("blackLeader");
            gmTeams.addPlayer("whiteOther");
            gmTeams.addPlayer("blackOther");
        } catch (NoPermissionException | NameAlreadyBoundException e){
            e.printStackTrace();
        }
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

        assertThrows(NoPermissionException.class, () -> gmTeams.addPlayer("extra") );
    }

    /**
     * test for the method swapTowers: it's checked that the swapping of a single tower works, that the swapping of
     * a group of towers works and that if the method removes all the remaining towers from the player,
     * that player is named the winner of the game.
     */
    @Test
    void swapTowers(){
        GameModel gmTeams = new GameModelTeams();

        try {
            gmTeams.addPlayer("whiteLeader");
            gmTeams.addPlayer("blackLeader");
            gmTeams.addPlayer("whiteOther");
            gmTeams.addPlayer("blackOther");
        } catch (NoPermissionException | NameAlreadyBoundException e){
            e.printStackTrace();
        }
        Player pl1 = gmTeams.playersManager.getPlayers().get(0);
        Player pl2 = gmTeams.playersManager.getPlayers().get(1);
        Player pl3 = gmTeams.playersManager.getPlayers().get(2);
        Player pl4 = gmTeams.playersManager.getPlayers().get(3);

        gmTeams.islandsManager.setTower(Tower.WHITE, 0);
        try{
        gmTeams.playersManager.getSchoolBoard(pl1).removeTowers(1);
        } catch (LimitExceededException e) {
            e.printStackTrace();
        }
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
        try{
            gmTeams.playersManager.getSchoolBoard(pl1).removeTowers(7);
        } catch (LimitExceededException e) {
            e.printStackTrace();
            fail();
        }

        gmTeams.swapTowers(1, Tower.BLACK);
        assertEquals(0, gmTeams.playersManager.getSchoolBoard(pl2).getNumTowers());
        assertEquals(gmTeams.roundManager.getWinner(), pl2);
    }
}