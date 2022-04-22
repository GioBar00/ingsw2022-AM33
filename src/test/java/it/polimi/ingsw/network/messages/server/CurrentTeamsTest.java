package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.messagesView.TeamsView;
import it.polimi.ingsw.server.model.GameModelTeams;
import it.polimi.ingsw.server.model.enums.Tower;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrentTeamsTest {

    /**
     * test for CurrentTeams. The creation of CurrentTeams is called twice: once while the player "blackOther" is still
     * in the lobby, the second time when it gets added to Team Black
     */
    @Test
    void teamsViewTest(){
        GameModelTeams gmTeams = new GameModelTeams();

        assertTrue(gmTeams.addPlayer("whiteLeader"));
        assertTrue(gmTeams.addPlayer("blackLeader"));
        assertTrue(gmTeams.addPlayer("whiteOther"));
        assertTrue(gmTeams.addPlayer("blackOther"));

        assertTrue(gmTeams.changeTeam("whiteLeader", Tower.WHITE));
        assertTrue(gmTeams.changeTeam("blackLeader", Tower.BLACK));
        assertTrue(gmTeams.changeTeam("whiteOther", Tower.WHITE));

        CurrentTeams ct = new CurrentTeams(gmTeams.getPlayersManager().getTeams(), gmTeams.getPlayersManager().getLobby());
        TeamsView tv = ct.getTeamsView();
        List<String> lobby = ct.getTeamsView().getLobby();

        assertFalse(tv.getTeams().isEmpty());
        assertEquals(2, tv.getTeams().get(Tower.WHITE).size());
        assertTrue(tv.getTeams().get(Tower.WHITE).contains("whiteLeader"));
        assertTrue(tv.getTeams().get(Tower.WHITE).contains("whiteOther"));
        assertEquals(1, tv.getTeams().get(Tower.BLACK).size());
        assertTrue(tv.getTeams().get(Tower.BLACK).contains("blackLeader"));
        assertEquals(1, lobby.size());
        assertTrue(lobby.contains("blackOther"));

        assertTrue(ct.isValid());

        assertTrue(gmTeams.changeTeam("blackOther", Tower.BLACK));

        ct = new CurrentTeams (gmTeams.getPlayersManager().getTeams(), gmTeams.getPlayersManager().getLobby());
        tv = ct.getTeamsView();
        lobby = ct.getTeamsView().getLobby();

        assertFalse(tv.getTeams().isEmpty());
        assertEquals(2, tv.getTeams().get(Tower.WHITE).size());
        assertTrue(tv.getTeams().get(Tower.WHITE).contains("whiteLeader"));
        assertTrue(tv.getTeams().get(Tower.WHITE).contains("whiteOther"));
        assertEquals(2, tv.getTeams().get(Tower.BLACK).size());
        assertTrue(tv.getTeams().get(Tower.BLACK).contains("blackLeader"));
        assertTrue(tv.getTeams().get(Tower.BLACK).contains("blackOther"));
        assertEquals(0, lobby.size());
    }
}