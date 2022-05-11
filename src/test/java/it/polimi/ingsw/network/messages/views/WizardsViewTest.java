package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.VirtualClient;
import it.polimi.ingsw.server.model.enums.Wizard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WizardsViewTest {

    @Test
    void wizardsViewTest(){
        Lobby lobby = new Lobby(4, new VirtualClient("player1"));

        lobby.addPlayer("player1");
        lobby.addPlayer("player2");
        lobby.addPlayer("player3");
        lobby.addPlayer("player4");

        WizardsView wizardsView = lobby.getWizardsView();
        assertEquals(4, wizardsView.getAvailableWizards().size());
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.SENSEI));
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.WITCH));
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.MERLIN));
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.KING));

        lobby.setWizard(Wizard.SENSEI, "player1");
        wizardsView = lobby.getWizardsView();
        assertEquals(3, wizardsView.getAvailableWizards().size());
        assertFalse(wizardsView.getAvailableWizards().contains(Wizard.SENSEI));
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.WITCH));
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.MERLIN));
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.KING));

        lobby.setWizard(Wizard.WITCH, "player2");
        wizardsView = lobby.getWizardsView();
        assertEquals(2, wizardsView.getAvailableWizards().size());
        assertFalse(wizardsView.getAvailableWizards().contains(Wizard.SENSEI));
        assertFalse(wizardsView.getAvailableWizards().contains(Wizard.WITCH));
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.MERLIN));
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.KING));

        lobby.setWizard(Wizard.MERLIN, "player3");
        wizardsView = lobby.getWizardsView();
        assertEquals(1, wizardsView.getAvailableWizards().size());
        assertFalse(wizardsView.getAvailableWizards().contains(Wizard.SENSEI));
        assertFalse(wizardsView.getAvailableWizards().contains(Wizard.WITCH));
        assertFalse(wizardsView.getAvailableWizards().contains(Wizard.MERLIN));
        assertTrue(wizardsView.getAvailableWizards().contains(Wizard.KING));

        lobby.setWizard(Wizard.KING, "player4");
        wizardsView = lobby.getWizardsView();
        assertEquals(0, wizardsView.getAvailableWizards().size());
    }
}