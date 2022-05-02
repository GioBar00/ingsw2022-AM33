package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.client.ChosenTeam;
import it.polimi.ingsw.network.messages.client.SkipTurn;
import it.polimi.ingsw.network.messages.client.StartGame;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.server.LobbyConstructor;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.VirtualClient;
import it.polimi.ingsw.server.listeners.MessageEvent;
import it.polimi.ingsw.server.listeners.MessageListener;
import it.polimi.ingsw.server.model.enums.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    Controller controller;
    ModelListeners modelListeners;

    static class ModelListener extends VirtualClient implements MessageListener {

        ModelListener(String name,MessageListener controller ){
            super(name);
            super.addListener(controller);
        }

        void request(Message message) {
            notifyListeners(new MessageEvent(this, message));
        }

        boolean queueContains(MessageType type){
            for(Message m : queue){
                if(MessageType.retrieveByMessageClass(m).equals(type)){
                    queue.clear();
                    return true;
                }
            }
            return false;
        }

        void clearQueue(){
            queue.clear();
        }

        @Override
        public void onMessage(MessageEvent event) {
            queue.add(event.getMessage());
        }
    }

    static class ModelListeners{
        private final ArrayList<ModelListener> mL;

        ModelListeners(){
            mL = new ArrayList<>();
        }
        void add(ModelListener m){
            mL.add(m);
        }

        ModelListener getByNickname(String Nickname){
            for(ModelListener m : mL){
                if(m.getIdentifier().equals(Nickname))
                    return m;
            }
            return null;
        }
    }


    /**
     * Tests the setting up of the Controller and the first games phases
     */
    void controllerCreationTest(){
        Server server = new Server();
        controller = new Controller(server);

        modelListeners = new ModelListeners();
        ModelListener m1 = new ModelListener("p1", controller);

        modelListeners.add(m1);

        assertNull(controller.getCurrentPlayer());
        assertFalse(controller.isInstantiated());
        assertFalse(controller.addPlayer(m1.getIdentifier()));

        controller.setModelAndLobby(GamePreset.TWO, GameMode.EXPERT, LobbyConstructor.getLobby(GamePreset.TWO));
        controller.addModelListener(m1);

        assertTrue(controller.isInstantiated());
        assertTrue(controller.addPlayer(m1.getIdentifier()));

        //start when there's only a player
        m1.request(new StartGame());
        assertTrue(m1.queueContains(MessageType.COMM_MESSAGE));

        //invalid message for the current phase
        m1.request(new ChosenIsland(1));
        assertTrue(m1.queueContains(MessageType.COMM_MESSAGE));

        ModelListener m2 = new ModelListener("p2", controller);
        modelListeners.add(m2);
        controller.addModelListener(m2);

        controller.addPlayer(m2.getIdentifier());
        m2.request(new StartGame());
        assertTrue(m2.queueContains(MessageType.COMM_MESSAGE));


        m1.request(new StartGame());
        assertTrue(m1.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(m2.queueContains(MessageType.CURRENT_GAME_STATE));

    }


    @Test
    void partySimulation() {
        controllerCreationTest();
        ModelListener current = modelListeners.getByNickname(controller.getCurrentPlayer());

        //first player play a card
        current.request(new PlayedAssistantCard(AssistantCard.FOUR));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        for(ModelListener m : modelListeners.mL)
            m.clearQueue();

        //first player play a card when is not his turn
        current.request(new PlayedAssistantCard(AssistantCard.TWO));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //second player send an invalid message for the phase
        current = modelListeners.getByNickname(controller.getCurrentPlayer());
        current.request(new StartGame());
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //second player try a not legit move
        current.request(new PlayedAssistantCard(AssistantCard.FOUR));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));
        assertEquals(current.getIdentifier(),controller.getCurrentPlayer());

        //second player play a card
        current.request(new PlayedAssistantCard(AssistantCard.TWO));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        for(ModelListener m : modelListeners.mL)
            m.clearQueue();

        moveStudentPhaseTest(current);

        current.request(new StartGame());
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        moveMotherNaturePhaseTest(current);

        current.request(new ChosenCloud(10));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        current.request(new ChosenCloud(1));
        assertFalse(current.queueContains(MessageType.COMM_MESSAGE));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        //skip turn
        ModelListener skip = modelListeners.getByNickname(controller.getCurrentPlayer());
        skip.request(new SkipTurn());


        assertEquals(current.getIdentifier(), controller.getCurrentPlayer());

    }

    /**
     * Test the possible combinations of messages during a moveStudent phase
     * @param current the current player
     */
    void moveStudentPhaseTest(ModelListener current){
        //send a valid request
        current.request(new MovedStudent(MoveLocation.ENTRANCE,2,MoveLocation.HALL,4));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        //send an invalid request
        current.request(new MovedStudent(MoveLocation.ENTRANCE,2,MoveLocation.ISLAND,4));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send an invalid request
        current.request(new MovedStudent(MoveLocation.ENTRANCE,2,MoveLocation.HALL,4));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send an invalid request
        current.request(new MovedStudent(MoveLocation.ENTRANCE,2,MoveLocation.CARD,4));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid request
        current.request(new MovedStudent(MoveLocation.ISLAND,2,MoveLocation.ENTRANCE,5));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid request
        current.request(new MovedStudent(MoveLocation.CARD,1,MoveLocation.ENTRANCE,2));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid request
        current.request(new MovedStudent(MoveLocation.HALL,1,MoveLocation.ENTRANCE,2));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid choice
        current.request(new ChosenIsland(2));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid choice
        current.request(new ChosenStudentColor(StudentColor.BLUE));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send invalid request
        current.request(new ConcludeCharacterCardEffect());
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send a valid request
        current.request(new MovedStudent(MoveLocation.ENTRANCE,1,MoveLocation.ISLAND,4));
        assertFalse(current.queueContains(MessageType.COMM_MESSAGE));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        //not a legit message for the current game phase
        current.request(new StartGame());
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        //send a valid request
        current.request(new MovedStudent(MoveLocation.ENTRANCE,5,MoveLocation.ISLAND,4));
        assertFalse(current.queueContains(MessageType.COMM_MESSAGE));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));
    }

    /**
     *Test the possible combinations of messages during a moveMotherNature phase
     *@param current the current player
     */
    void moveMotherNaturePhaseTest(ModelListener current){
        current.request(new MovedMotherNature(10));
        assertFalse(current.queueContains(MessageType.CURRENT_GAME_STATE));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));

        current.request(new MovedMotherNature(1));
        assertFalse(current.queueContains(MessageType.COMM_MESSAGE));
        assertTrue(current.queueContains(MessageType.CURRENT_GAME_STATE));

        current.request(new ChosenIsland(4));
        assertTrue(current.queueContains(MessageType.COMM_MESSAGE));
    }


    /**
     * Tests the situation when players disconnect beforw the beginning of the match
     */
    @Test
    void EarlyDisconnectionTest(){
        Controller c = new Controller(new Server());
        c.setModelAndLobby(GamePreset.FOUR,GameMode.EASY,LobbyConstructor.getLobby(GamePreset.TWO));

        assertTrue(c.isInstantiated());
        ModelListener m1 = new ModelListener("p1", c);
        c.addPlayer(m1.getIdentifier());
        c.addModelListener(m1);

        ModelListener m2 = new ModelListener("p2", c);
        c.addPlayer(m2.getIdentifier());
        c.addModelListener(m2);
        assertFalse(c.addPlayer("p2"));

        ModelListener m3 = new ModelListener("p3", c);
        c.addPlayer(m3.getIdentifier());
        c.addModelListener(m3);

        ModelListener m4 = new ModelListener("p4", c);
        c.addPlayer(m4.getIdentifier());
        c.addModelListener(m4);

        m2.request(new SkipTurn());

        m2 = new ModelListener("p2", c);
        assertTrue(c.addPlayer(m2.getIdentifier()));
        c.addModelListener(m2);

        assertFalse(c.addPlayer("p10"));

        m1.request(new SkipTurn());
        assertFalse(c.isInstantiated());
    }

    /**
     * Test change team calls
     */
    @Test
    void chooseTeamTest(){
        Controller c = new Controller(new Server());
        c.setModelAndLobby(GamePreset.FOUR,GameMode.EASY,LobbyConstructor.getLobby(GamePreset.FOUR));

        ModelListener m1 = new ModelListener("p1", c);
        c.addPlayer(m1.getIdentifier());
        c.addModelListener(m1);

        ModelListener m2 = new ModelListener("p2", c);
        c.addPlayer(m2.getIdentifier());
        c.addModelListener(m2);

        ModelListener m3 = new ModelListener("p3", c);
        c.addPlayer(m3.getIdentifier());
        c.addModelListener(m3);

        ModelListener m4 = new ModelListener("p4", c);
        c.addPlayer(m4.getIdentifier());
        c.addModelListener(m4);

        m1.request(new ChosenTeam(Tower.BLACK));
        m2.request(new ChosenTeam(Tower.BLACK));
        m3.request(new ChosenTeam(Tower.BLACK));
        m4.request(new ChosenTeam(Tower.WHITE));

        m1.clearQueue();
        m2.clearQueue();
        m3.clearQueue();
        m4.clearQueue();

        m1.request(new StartGame());
        assertTrue(m1.queueContains(MessageType.COMM_MESSAGE));

        m1.request(new ChosenTeam(Tower.WHITE));
        m1.request(new StartGame());
        assertTrue(m1.queueContains(MessageType.CURRENT_GAME_STATE));
    }
}

