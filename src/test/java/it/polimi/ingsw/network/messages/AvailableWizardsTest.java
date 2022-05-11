package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CurrentGameState;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.VirtualClient;
import it.polimi.ingsw.server.model.enums.Wizard;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.network.messages.MessageBuilderTest.toAndFromJson;
import static org.junit.jupiter.api.Assertions.*;

class AvailableWizardsTest {

    @Test
    void availableWizardsTest(){
        Lobby lobby = new Lobby(4, new VirtualClient("player1"));
        lobby.addPlayer("player1");
        lobby.addPlayer("player2");
        lobby.addPlayer("player3");
        lobby.addPlayer("player4");

        AvailableWizards original = new AvailableWizards(lobby.getWizardsView());
        Message m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertTrue(m instanceof AvailableWizards);
        assertEquals(4, ((AvailableWizards) m).getWizardsView().getAvailableWizards().size());
        assertTrue(((AvailableWizards) m).getWizardsView().getAvailableWizards().contains(Wizard.SENSEI));
        assertTrue(((AvailableWizards) m).getWizardsView().getAvailableWizards().contains(Wizard.WITCH));
        assertTrue(((AvailableWizards) m).getWizardsView().getAvailableWizards().contains(Wizard.MERLIN));
        assertTrue(((AvailableWizards) m).getWizardsView().getAvailableWizards().contains(Wizard.KING));

        lobby.setWizard(Wizard.SENSEI, "player1");
        lobby.setWizard(Wizard.WITCH, "player2");
        lobby.setWizard(Wizard.MERLIN, "player3");
        lobby.setWizard(Wizard.KING, "player4");
        original = new AvailableWizards(lobby.getWizardsView());
        m = toAndFromJson(original);
        assertTrue(m.isValid());
        assertEquals(0, ((AvailableWizards) m).getWizardsView().getAvailableWizards().size());
    }
}