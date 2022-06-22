package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.network.listeners.DisconnectEvent;
import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.client.ChosenTeam;
import it.polimi.ingsw.network.messages.client.ChosenWizard;
import it.polimi.ingsw.network.messages.client.StartGame;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.Disconnected;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.VirtualClient;
import it.polimi.ingsw.server.lobby.LobbyConstructor;
import it.polimi.ingsw.server.model.enums.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

//todo javacdoc
class ControllerTest {

    Server srv;

    Controller controller;
    ModelListeners modelListeners;

    static class ModelListener extends VirtualClient implements MessageListener {

        ModelListener(String name, MessageListener controller) {
            super(name);
            super.addMessageListener(controller);
        }

        @Override
        public synchronized boolean isConnected() {
            return true;
        }

        boolean queueContains(MessageType type) {

            for (Message m : communicationHandler.getQueue()) {
                if (MessageType.retrieveByMessage(m).equals(type)) {
                    communicationHandler.clearQueue();
                    return true;
                }
            }
            return false;

        }

        void clearQueue() {
            communicationHandler.clearQueue();
        }

        @Override
        public void onMessage(MessageEvent event) {
            communicationHandler.sendMessage(event.getMessage());
        }
    }

    static class ModelListeners {
        private final ArrayList<ModelListener> mL;

        ModelListeners() {
            mL = new ArrayList<>();
        }

        void add(ModelListener m) {
            mL.add(m);
        }

        ModelListener getByNickname(String Nickname) {
            for (ModelListener m : mL) {
                if (m.getIdentifier().equals(Nickname))
                    return m;
            }
            return null;
        }
    }


    /**
     * Tests the setting up of the Controller and the first games phases
     */
    void controllerCreationTest() {
        srv = new Server();
        srv.getClientManager().getController().stop();
        controller = srv.getClientManager().getController();

        modelListeners = new ModelListeners();
        ModelListener m1 = new ModelListener("p1", controller);

        modelListeners.add(m1);

        assertNull(controller.getCurrentPlayer());
        assertFalse(controller.isInstantiated());
        assertFalse(controller.isGameStarted());
        assertFalse(controller.addPlayer(m1.getIdentifier()));


        controller.setModelAndLobby(GamePreset.TWO, GameMode.EXPERT, LobbyConstructor.getLobby(GamePreset.TWO));
        controller.addModelListener(m1);
        controller.sendInitialStats(m1);
        assertTrue(m1.queueContains(MessageType.AVAILABLE_WIZARDS));

        assertTrue(controller.isInstantiated());
        assertTrue(controller.addPlayer(m1.getIdentifier()));

        controller.handleMessage(new MessageEvent(m1, new ChosenWizard(Wizard.SENSEI)));

        //start when there's only a player
        controller.handleMessage(new MessageEvent(m1, new StartGame()));

        assertTrue(m1.queueContains(MessageType.COMM_MESSAGE));

        //invalid message for the current phase
        controller.handleMessage(new MessageEvent(m1, new ChosenIsland(1)));

        assertTrue(m1.queueContains(MessageType.COMM_MESSAGE));

        ModelListener m2 = new ModelListener("p2", controller);
        modelListeners.add(m2);
        controller.addModelListener(m2);
        controller.addPlayer("p2");
        controller.sendInitialStats(m2);
        assertTrue(m2.queueContains(MessageType.AVAILABLE_WIZARDS));
        assertFalse(controller.isGameStarted());

        controller.handleMessage(new MessageEvent(m2, new ChosenWizard(Wizard.SENSEI)));
        assertTrue(m2.queueContains(MessageType.COMM_MESSAGE));

        controller.handleMessage(new MessageEvent(m2, new ChosenWizard(Wizard.WITCH)));

        controller.addPlayer(m2.getIdentifier());
        controller.handleMessage(new MessageEvent(m2, new StartGame()));
        assertTrue(m2.queueContains(MessageType.COMM_MESSAGE));

        m1.clearQueue();
        m2.clearQueue();

        srv.getClientManager().addVirtualClient(m1);
        srv.getClientManager().addVirtualClient(m2);

        controller.handleMessage(new MessageEvent(m1, new StartGame()));
        assertTrue(m1.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(m2.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(controller.isGameStarted());
    }

    @Test
    void partySimulation() {
        controllerCreationTest();
        ModelListener current = modelListeners.getByNickname(controller.getCurrentPlayer());

        //first player play a card
        controller.handleMessage(new MessageEvent(current, new PlayedAssistantCard(AssistantCard.EAGLE)));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        for (ModelListener m : modelListeners.mL)
            m.clearQueue();

        //first player play a card when is not his turn
        controller.handleMessage(new MessageEvent(current, new PlayedAssistantCard(AssistantCard.OSTRICH)));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //second player send an invalid message for the phase
        current = modelListeners.getByNickname(controller.getCurrentPlayer());
        controller.handleMessage(new MessageEvent(current, new StartGame()));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //second player try a not legit move
        controller.handleMessage(new MessageEvent(current, new PlayedAssistantCard(AssistantCard.EAGLE)));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));
        assertEquals(current.getIdentifier(), controller.getCurrentPlayer());

        //second player play a card
        controller.handleMessage(new MessageEvent(current, new PlayedAssistantCard(AssistantCard.OSTRICH)));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        for (ModelListener m : modelListeners.mL)
            m.clearQueue();

        moveStudentPhaseTest(current);

        controller.handleMessage(new MessageEvent(current, new StartGame()));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        moveMotherNaturePhaseTest(current);

        controller.handleMessage(new MessageEvent(current, new ChosenCloud(10)));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        controller.handleMessage(new MessageEvent(current, new ChosenCloud(1)));
        assertFalse(current.queueContains(MessageType.COMM_MESSAGE));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        // disconnect
        ModelListener skip = modelListeners.getByNickname(controller.getCurrentPlayer());
        srv.getClientManager().onDisconnect(new DisconnectEvent(skip));

        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));


        srv.getClientManager().connected(skip);

        assertEquals(skip.getIdentifier(), controller.getCurrentPlayer());
        skip.clearQueue();

        controller.notifyCurrentGameStateToPlayer(current.getIdentifier());
        assertFalse(current.queueContains(MessageType.COMM_MESSAGE));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        controller.setWaiting(true);
        controller.handleMessage(new MessageEvent(current, new ChosenCloud(1)));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
    }

    /**
     * Test the possible combinations of messages during a moveStudent phase
     *
     * @param current the current player
     */
    void moveStudentPhaseTest(ModelListener current) {
        //send a valid request
        controller.handleMessage(new MessageEvent(current, new MovedStudent(MoveLocation.ENTRANCE, 2, MoveLocation.HALL, 4)));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        //send an invalid request
        controller.handleMessage(new MessageEvent(current, new MovedStudent(MoveLocation.ENTRANCE, 2, MoveLocation.ISLAND, 4)));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send an invalid request
        controller.handleMessage(new MessageEvent(current, new MovedStudent(MoveLocation.ENTRANCE, 2, MoveLocation.HALL, 4)));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send an invalid request
        controller.handleMessage(new MessageEvent(current, new MovedStudent(MoveLocation.ENTRANCE, 2, MoveLocation.CARD, 4)));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid request
        controller.handleMessage(new MessageEvent(current, new MovedStudent(MoveLocation.ISLAND, 2, MoveLocation.ENTRANCE, 5)));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid request
        controller.handleMessage(new MessageEvent(current, new MovedStudent(MoveLocation.CARD, 1, MoveLocation.ENTRANCE, 2)));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid request
        controller.handleMessage(new MessageEvent(current, new MovedStudent(MoveLocation.HALL, 1, MoveLocation.ENTRANCE, 2)));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid choice
        controller.handleMessage(new MessageEvent(current, new ChosenIsland(2)));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid choice
        controller.handleMessage(new MessageEvent(current, new ChosenStudentColor(StudentColor.BLUE)));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid request
        controller.handleMessage(new MessageEvent(current, new ConcludeCharacterCardEffect()));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send a valid request
        controller.handleMessage(new MessageEvent(current, new MovedStudent(MoveLocation.ENTRANCE, 1, MoveLocation.ISLAND, 4)));
        assertFalse(current.queueContains(MessageType.COMM_MESSAGE));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        //not a legit message for the current game phase
        controller.handleMessage(new MessageEvent(current, new StartGame()));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send a valid request
        controller.handleMessage(new MessageEvent(current, new MovedStudent(MoveLocation.ENTRANCE, 5, MoveLocation.ISLAND, 4)));
        assertFalse(current.queueContains(MessageType.COMM_MESSAGE));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));


        controller.handleMessage(new MessageEvent(current, new SwappedStudents(MoveLocation.ENTRANCE, 5, MoveLocation.HALL, 4)));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));

        controller.handleMessage(new MessageEvent(current, new SwappedStudents(MoveLocation.CARD, 4, MoveLocation.ENTRANCE, 4)));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
    }

    /**
     * Test the possible combinations of messages during a moveMotherNature phase
     *
     * @param current the current player
     */
    void moveMotherNaturePhaseTest(ModelListener current) {
        controller.handleMessage(new MessageEvent(current, new MovedMotherNature(10)));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        controller.handleMessage(new MessageEvent(current, new MovedMotherNature(1)));
        assertFalse(current.queueContains(MessageType.COMM_MESSAGE));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        controller.handleMessage(new MessageEvent(current, new ChosenIsland(4)));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));
    }


    /**
     * Tests the situation when players disconnect before the beginning of the match
     */
    @Test
    void EarlyDisconnectionTest() {

        Server server = new Server();
        server.getClientManager().getController().stop();
        Controller c = server.getClientManager().getController();
        c.setModelAndLobby(GamePreset.FOUR, GameMode.EASY, LobbyConstructor.getLobby(GamePreset.FOUR));

        assertTrue(c.isInstantiated());
        ModelListener m1 = new ModelListener("p1", c);
        assertTrue(c.addPlayer(m1.getIdentifier()));
        c.addModelListener(m1);
        server.getClientManager().addVirtualClient(m1);

        ModelListener m2 = new ModelListener("p2", c);
        assertTrue(c.addPlayer(m2.getIdentifier()));
        c.addModelListener(m2);
        assertFalse(c.addPlayer("p2"));
        server.getClientManager().addVirtualClient(m2);

        ModelListener m3 = new ModelListener("p3", c);
        assertTrue(c.addPlayer(m3.getIdentifier()));
        c.addModelListener(m3);
        server.getClientManager().addVirtualClient(m3);

        ModelListener m4 = new ModelListener("p4", c);
        assertTrue(c.addPlayer(m4.getIdentifier()));
        c.addModelListener(m4);
        server.getClientManager().addVirtualClient(m4);

        server.getClientManager().onDisconnect(new DisconnectEvent(m2));

        m2 = new ModelListener("p2", c);
        assertTrue(c.addPlayer(m2.getIdentifier()));
        c.addModelListener(m2);

        assertFalse(c.addPlayer("p10"));

        c.handleMessage(new MessageEvent(m1, new Disconnected()));
        assertFalse(c.isInstantiated());
    }

    /**
     * Tests the situation when players disconnect during the match in a 3+ player game
     */
    @Test
    void SkipTurn() {
        srv = new Server();
        srv.getClientManager().getController().stop();
        controller = srv.getClientManager().getController();

        modelListeners = new ModelListeners();
        ModelListener m1 = new ModelListener("p1", controller);

        modelListeners.add(m1);

        controller.setModelAndLobby(GamePreset.THREE, GameMode.EXPERT, LobbyConstructor.getLobby(GamePreset.THREE));
        controller.addModelListener(m1);
        controller.sendInitialStats(m1);
        assertTrue(m1.queueContains(MessageType.AVAILABLE_WIZARDS));

        assertTrue(controller.addPlayer(m1.getIdentifier()));

        controller.handleMessage(new MessageEvent(m1, new ChosenWizard(Wizard.SENSEI)));


        ModelListener m2 = new ModelListener("p2", controller);
        modelListeners.add(m2);
        controller.addModelListener(m2);
        controller.addPlayer("p2");
        controller.sendInitialStats(m2);
        assertTrue(m2.queueContains(MessageType.AVAILABLE_WIZARDS));
        assertFalse(controller.isGameStarted());

        controller.handleMessage(new MessageEvent(m2, new ChosenWizard(Wizard.WITCH)));

        controller.addPlayer(m2.getIdentifier());

        ModelListener m3 = new ModelListener("p3", controller);
        modelListeners.add(m3);
        controller.addModelListener(m3);
        controller.addPlayer("p3");
        controller.sendInitialStats(m3);
        assertTrue(m3.queueContains(MessageType.AVAILABLE_WIZARDS));
        assertFalse(controller.isGameStarted());

        controller.handleMessage(new MessageEvent(m3, new ChosenWizard(Wizard.KING)));

        controller.addPlayer(m3.getIdentifier());

        m1.clearQueue();
        m2.clearQueue();
        m3.clearQueue();

        srv.getClientManager().addVirtualClient(m1);
        srv.getClientManager().addVirtualClient(m2);
        srv.getClientManager().addVirtualClient(m3);

        controller.handleMessage(new MessageEvent(m1, new StartGame()));
        assertTrue(m1.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(m2.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(controller.isGameStarted());

        // disconnect
        ModelListener skip = modelListeners.getByNickname(controller.getCurrentPlayer());
        srv.getClientManager().onDisconnect(new DisconnectEvent(skip));

        assertNotEquals(skip.getIdentifier(), controller.getCurrentPlayer());
    }

    /**
     * Test change team calls
     */
    @Test
    void chooseTeamTest() {
        Server server = new Server();
        Controller c = server.getClientManager().getController();
        c.setModelAndLobby(GamePreset.FOUR, GameMode.EASY, LobbyConstructor.getLobby(GamePreset.FOUR));

        ModelListener m1 = new ModelListener("p1", c);
        c.addPlayer(m1.getIdentifier());
        c.addModelListener(m1);
        c.handleMessage(new MessageEvent(m1, new ChosenWizard(Wizard.SENSEI)));


        ModelListener m2 = new ModelListener("p2", c);
        c.addPlayer(m2.getIdentifier());
        c.addModelListener(m2);
        c.handleMessage(new MessageEvent(m2, new ChosenWizard(Wizard.WITCH)));

        ModelListener m3 = new ModelListener("p3", c);
        c.addPlayer(m3.getIdentifier());
        c.addModelListener(m3);
        c.handleMessage(new MessageEvent(m3, new ChosenWizard(Wizard.KING)));

        ModelListener m4 = new ModelListener("p4", c);
        c.addPlayer(m4.getIdentifier());
        c.addModelListener(m4);
        c.handleMessage(new MessageEvent(m4, new ChosenWizard(Wizard.MERLIN)));

        c.handleMessage(new MessageEvent(m1, new ChosenTeam(Tower.BLACK)));
        c.handleMessage(new MessageEvent(m2, new ChosenTeam(Tower.BLACK)));
        c.handleMessage(new MessageEvent(m3, new ChosenTeam(Tower.BLACK)));
        c.handleMessage(new MessageEvent(m4, new ChosenTeam(Tower.WHITE)));


        m1.clearQueue();
        m2.clearQueue();
        m3.clearQueue();
        m4.clearQueue();

        c.handleMessage(new MessageEvent(m1, new StartGame()));
        assertTrue(m1.queueContains(MessageType.COMM_MESSAGE));

        c.handleMessage(new MessageEvent(m1, new ChosenTeam(Tower.WHITE)));
        c.handleMessage(new MessageEvent(m1, new StartGame()));
        assertTrue(m1.queueContains(MessageType.CURRENT_GAME_STATE));

        assertEquals(Tower.WHITE, c.getPlayerTeam(m1.getIdentifier()));

        c.removeModelListener(m1);
        c.notifyCurrentGameStateToPlayer(m1.getIdentifier());
        assertFalse(m1.queueContains(MessageType.CURRENT_GAME_STATE));

        c.notifyCurrentGameStateToPlayer(m2.getIdentifier());
        assertFalse(m1.queueContains(MessageType.CURRENT_GAME_STATE));
    }
}

