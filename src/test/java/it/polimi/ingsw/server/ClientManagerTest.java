package it.polimi.ingsw.server;

import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.server.lobby.LobbyConstructor;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePreset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests class for {@link ClientManager}
 */
class ClientManagerTest {

    /**
     * Tests the methods addPlayer (adds a new player and an existing one), getVirtualClient and reconnectPlayer.
     */
    @Test
    public void getAndSetOnClientManager() {
        ClientManager clientManager = new ClientManager(new Server());
        clientManager.resetGame();
        clientManager.getController().setModelAndLobby(GamePreset.TWO, GameMode.EXPERT, LobbyConstructor.getLobby(GamePreset.TWO));
        assertTrue(clientManager.addPlayer(new CommunicationHandler(false), "p1"));
        assertEquals("p1", clientManager.getVirtualClient("p1").getIdentifier());
        assertNull(clientManager.getVirtualClient("p2"));
        assertFalse(clientManager.addPlayer(new CommunicationHandler(false), "p1"));
        clientManager.reconnectPlayer(new CommunicationHandler(false), "p1");
    }
}