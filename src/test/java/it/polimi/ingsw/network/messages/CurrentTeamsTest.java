package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.server.lobby.TeamLobby;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.network.messages.MessageBuilderTest.toAndFromJson;
import static org.junit.jupiter.api.Assertions.*;

class CurrentTeamsTest {

    /**
     * test for the message CurrentTeams
     */
    @Test
    void CurrentTeamsTest(){
        TeamLobby teamLobby = new TeamLobby(3);
        teamLobby.addPlayer("player1");
        CurrentTeams original = new CurrentTeams(teamLobby.getTeamView());
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof CurrentTeams);
        assertEquals(original.getTeamsView().getTeams(), ((CurrentTeams) m).getTeamsView().getTeams());
        // test null message
        original = new CurrentTeams(null);
        m = toAndFromJson(original);
        assertFalse(m.isValid());
        // there's no test for empty fields because both the teams and the lobby can have null fields
    }
}