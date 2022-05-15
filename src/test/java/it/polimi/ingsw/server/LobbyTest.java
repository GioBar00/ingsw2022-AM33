package it.polimi.ingsw.server;

import it.polimi.ingsw.server.lobby.Lobby;
import it.polimi.ingsw.server.lobby.LobbyConstructor;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Lobby, TeamLobby and LobbyConstructor classes
 */
public class LobbyTest {

    /**
     * Test methods in lobby
     */
    @Test
    void lobbyTest(){
        Lobby lobby = LobbyConstructor.getLobby(GamePreset.THREE);

        assertTrue(lobby.addPlayer("p1"));
        assertFalse(lobby.addPlayer("p1"));

        assertFalse(lobby.canStart());

        assertTrue(lobby.addPlayer("p2"));
        assertTrue(lobby.addPlayer("p3"));

        assertFalse(lobby.addPlayer("p4"));

        assertFalse(lobby.canStart());
        ArrayList<String> playersNickname = new ArrayList<>();
        for(PlayerDetails p : lobby.getPlayers()){
            playersNickname.add(p.getNickname());
        }

        assertTrue(playersNickname.contains("p1"));
        assertTrue(playersNickname.contains("p2"));
        assertTrue(playersNickname.contains("p3"));
        assertEquals(3,playersNickname.size());

        assertTrue(lobby.setWizard(Wizard.MERLIN,"p1"));
        assertFalse(lobby.setWizard(Wizard.SENSEI,"p1"));
        assertFalse(lobby.setWizard(Wizard.MERLIN,"p2"));
        assertTrue(lobby.setWizard(Wizard.SENSEI,"p2"));
        assertTrue(lobby.setWizard(Wizard.KING,"p3"));

        assertFalse(lobby.setWizard(Wizard.WITCH,"p4"));

        assertTrue(lobby.removePlayer("p3"));
        assertTrue(lobby.addPlayer("p4"));
        assertTrue(lobby.setWizard(Wizard.KING,"p4"));
        assertTrue(lobby.canStart());

        assertFalse(lobby.removePlayer("p1"));

        assertFalse(lobby.changeTeam("p1", Tower.WHITE));
        assertNull(lobby.getTeamView());

        EnumSet<Wizard> set = lobby.getWizardsView().getAvailableWizards();
        assertTrue(set.contains(Wizard.WITCH));
        assertEquals(1, set.size());

        assertEquals("p1",lobby.getMaster());
        assertFalse(lobby.removePlayer("p5"));
    }

    /**
     * Test methods in TeamLobby
     */
    @Test
    void TeamLobbyTest(){
        VirtualClient v1 = new VirtualClient("p1");
        Lobby lobby = LobbyConstructor.getLobby(GamePreset.FOUR);

        lobby.addPlayer("p1");
        lobby.addPlayer("p2");
        lobby.addPlayer("p3");
        lobby.addPlayer("p4");
        lobby.notifyAvailableWizards(v1.getIdentifier());
        assertFalse(lobby.canStart());

        lobby.setWizard(Wizard.KING,"p1");
        lobby.setWizard(Wizard.MERLIN,"p2");
        lobby.setWizard(Wizard.WITCH,"p3");
        lobby.setWizard(Wizard.SENSEI,"p4");

        assertFalse(lobby.canStart());

        assertFalse(lobby.changeTeam("p5",Tower.BLACK));
        assertFalse(lobby.changeTeam("p1",Tower.GREY));
        assertTrue(lobby.changeTeam("p1",Tower.WHITE));
        assertTrue(lobby.changeTeam("p2",Tower.WHITE));
        assertTrue(lobby.changeTeam("p3",Tower.WHITE));
        assertTrue(lobby.changeTeam("p4", Tower.BLACK));

        assertFalse(lobby.canStart());
        assertEquals(3, lobby.getTeamView().getTeams().get(Tower.WHITE).size());
        assertEquals(1, lobby.getTeamView().getTeams().get(Tower.BLACK).size());

        assertTrue(lobby.changeTeam("p1", Tower.BLACK));
        assertTrue(lobby.canStart());
    }
}
