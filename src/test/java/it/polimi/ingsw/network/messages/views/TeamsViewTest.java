package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.server.lobby.Lobby;
import it.polimi.ingsw.server.lobby.LobbyConstructor;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.Tower;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeamsViewTest {

    /**
     * test for CurrentTeams. The creation of CurrentTeams is called twice: once while the player "blackOther" is still
     * in the lobby, the second time when it gets added to Team Black
     */

    @Test
    void teamsViewTest(){
        Lobby lobby = LobbyConstructor.getLobby(GamePreset.FOUR);

        assertTrue(lobby.addPlayer("whiteLeader"));
        assertTrue(lobby.addPlayer("blackLeader"));
        assertTrue(lobby.addPlayer("whiteOther"));
        assertTrue(lobby.addPlayer("blackOther"));

        assertTrue(lobby.changeTeam("whiteLeader", Tower.WHITE));
        assertTrue(lobby.changeTeam("blackLeader", Tower.BLACK));
        assertTrue(lobby.changeTeam("whiteOther", Tower.WHITE));


        TeamsView tv = lobby.getTeamView();
        CurrentTeams ct = new CurrentTeams(tv);

        assertFalse(tv.getTeams().isEmpty());
        assertEquals(2, tv.getTeams().get(Tower.WHITE).size());
        assertTrue(tv.getTeams().get(Tower.WHITE).contains("whiteLeader"));
        assertTrue(tv.getTeams().get(Tower.WHITE).contains("whiteOther"));
        assertEquals(1, tv.getTeams().get(Tower.BLACK).size());
        assertTrue(tv.getTeams().get(Tower.BLACK).contains("blackLeader"));


        assertTrue(ct.isValid());

        assertTrue(lobby.changeTeam("blackOther", Tower.BLACK));


        tv = lobby.getTeamView();
        ct = new CurrentTeams (lobby.getTeamView());

        int lobbySize = ct.getTeamsView().getLobby().size();

        assertFalse(tv.getTeams().isEmpty());
        assertEquals(2, tv.getTeams().get(Tower.WHITE).size());
        assertTrue(tv.getTeams().get(Tower.WHITE).contains("whiteLeader"));
        assertTrue(tv.getTeams().get(Tower.WHITE).contains("whiteOther"));
        assertEquals(2, tv.getTeams().get(Tower.BLACK).size());
        assertTrue(tv.getTeams().get(Tower.BLACK).contains("blackLeader"));
        assertTrue(tv.getTeams().get(Tower.BLACK).contains("blackOther"));
        assertEquals(0, lobbySize);
    }

}