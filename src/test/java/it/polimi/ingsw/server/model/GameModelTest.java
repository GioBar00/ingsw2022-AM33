package it.polimi.ingsw.server.model;

import it.polimi.ingsw.network.messages.ActionRequest;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.server.listeners.MessageEvent;
import it.polimi.ingsw.server.listeners.MessageListener;
import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.islands.Island;
import it.polimi.ingsw.server.model.player.Player;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the GameModel class
 */
class GameModelTest {

    /**
     * Game model
     */
    GameModel model;

    /**
     * Players notified about the CurrentGameState and CurrentTeams
     */
    Set<String> messagesNotified = new HashSet<>();

    /**
     * Players notified about ActionRequests.
     */
    Set<String> actionRequestNotified = new HashSet<>();

    private final PlayerConvertor pC = new PlayerConvertor();

    /**
     * Adds a new listener to the model.
     * @param identifier nickname of the player.
     */
    void addMessageListener(String identifier) {
        model.addListener(new MessageListener() {
            @Override
            public String getIdentifier() {
                return identifier;
            }
            @Override
            public void onMessage(MessageEvent event) {
                Message m = event.getMessage();
                if (m instanceof ActionRequest)
                    actionRequestNotified.add(identifier);
                else
                    messagesNotified.add(identifier);
            }
        });
    }

    /**
     * Clears the notifications.
     */
    void clearNotifications() {
        messagesNotified.clear();
        actionRequestNotified.clear();
    }

    /**
     * Checks that the CurrentGameState or CurrentTeams has been notified to each player.
     * Checks that the ActionRequest has been notified only at the current player if the game has not ended.
     */
    void checkNotifications() {
        for (Player p : model.playersManager.getPlayers())
            assertTrue(messagesNotified.contains(p.getNickname()));
        if (model.gameState == GameState.STARTED) {
            assertEquals(1, actionRequestNotified.size());
            assertTrue(actionRequestNotified.contains(model.playersManager.getCurrentPlayer().getNickname()));
        } else if (model.gameState == GameState.ENDED)
            assertTrue(actionRequestNotified.isEmpty());
        clearNotifications();
    }

    /**
     * Checks that no notification were sent.
     */
    void checkEmptyNotifications() {
        assertTrue(messagesNotified.isEmpty());
        assertTrue(actionRequestNotified.isEmpty());
    }

    /**
     * Tests the return value for the Game interface if the match is not expert.
     */
    @Test
    void interfaceTesting(){
        Game m = new GameModel(GamePreset.THREE);
        assertFalse(m.activateCharacterCard(4));
        assertFalse(m.applyEffect(null));
    }

    /**
     * Test the creation of a three payers game and the setting up of the match
     */
    void createGameModel(GamePreset preset) {
        model = new GameModel(preset);

        for (int i = 0; i < preset.getPlayersNumber(); i++) {
            assertFalse(model.startGame());
            checkEmptyNotifications();

            assertEquals(preset.getPlayersNumber() - i, model.getAvailablePlayerSlots());

            addMessageListener(i + "");
            assertTrue(model.addPlayer(pC.getPlayer( i + "", Wizard.THREE)));
        }

        assertEquals(GameState.UNINITIALIZED, model.getGameState());
        assertTrue(model.startGame());
        checkNotifications();

        assertEquals(GameState.STARTED, model.getGameState());
        assertFalse(model.startGame());
        checkEmptyNotifications();
        assertEquals(0,model.islandsManager.getIslandGroup(model.motherNatureIndex).getIslands().get(0).getNumStudents());
        assertEquals(0,model.islandsManager.getIslandGroup((model.motherNatureIndex + 6) % 12).getIslands().get(0).getNumStudents());

        numTowersAndStudent();
        playAssistantCard();
    }


    /**
     * check the integrity of all the movement done by calculating the amount of students and towers
     */

    void numTowersAndStudent() {
        int numStud = model.bag.students.size();
        int numTowers = model.playersManager.getPreset().getTowersNumber() * model.playersManager.getPreset().getPlayersNumber();
        assert model.playersManager.getPlayers() != null;
        for (Player p : model.playersManager.getPlayers()) {
            numStud += model.playersManager.getSchoolBoard(p).getStudentsInEntrance().stream().filter(Objects::nonNull).toArray().length;
            for (StudentColor s : StudentColor.values())
                numStud += model.playersManager.getSchoolBoard().getStudentsInHall(s);
        }
        for (Cloud c : model.clouds)
            numStud += c.getStudents().stream().filter(Objects::nonNull).toArray().length;
        assertEquals(12, model.islandsManager.getNumIslandGroups());
        for (int i = 0; i < model.islandsManager.getNumIslandGroups(); i++)
            for (int j = 0; j < model.islandsManager.getIslandGroup(i).size(); j++)
                numStud += model.islandsManager.getIslandGroup(i).getIslands().get(j).getNumStudents();
        assertEquals(130, numStud);
        assertEquals(model.playersManager.getPreset().getCloudsNumber(), model.clouds.size());

        for (Player p : model.playersManager.getPlayers()) {
            numTowers = numTowers - model.playersManager.getSchoolBoard(p).getNumTowers();
        }
        for (int i = 0; i < model.islandsManager.getNumIslandGroups(); i++)
            if (model.islandsManager.getTower(i) != null) {
                numTowers = numTowers - model.islandsManager.getIslandGroup(i).size();
            }
        assertEquals(0, numTowers);
    }


    /**
     * Check the throwing of the exception when one player tries to play a card that has already been played.
     * Controls the order for the action phase
     */
    void playAssistantCard() {
        assertEquals(GamePhase.PLANNING, model.roundManager.getGamePhase());
        List<Player> players = new LinkedList<>(model.playersManager.getPlayers());
        List<Player> expectedOrder = new ArrayList<>(players.size());

        List<AssistantCard> values = List.of(AssistantCard.values());

        for (int i = 0; i < players.size(); i++) {
            expectedOrder.add(players.get(i));
            AssistantCard card = values.get(values.size() - i - 1);
            assertTrue(model.playAssistantCard(card));
            checkNotifications();
            assertFalse(model.playAssistantCard(card));
            checkEmptyNotifications();
        }

        players = model.playersManager.getPlayers();
        Collections.reverse(expectedOrder);

        for (int i = 0; i < players.size(); i++) {
            assertEquals(expectedOrder.get(i), players.get(i));
        }

    }

    /**
     * Test the moving of Students from the entrance to the hall. Tries to move from an empty slot and check the amount of students in hall.
     * After the allowed moves choose a Cloud and refill the player entrance
     */
    @Test
    void moveStudentsRefillEntrance() {
        createGameModel(GamePreset.THREE);
        Player current = model.playersManager.getCurrentPlayer();
        assertTrue(model.moveStudentToHall(0));
        checkNotifications();

        assertFalse(model.moveStudentToHall(0));
        checkEmptyNotifications();
        assertFalse(model.moveStudentToHall(model.playersManager.getPreset().getEntranceCapacity()));
        checkEmptyNotifications();
        boolean check = false;
        for (StudentColor s : StudentColor.values()) {
            if (model.playersManager.getSchoolBoard().getStudentsInHall(s) == 1) {
                if (check) {
                    fail();
                }
                check = true;
            }
        }
        assertTrue(check);
        assertTrue(model.moveStudentToHall(2));
        assertTrue(model.moveStudentToHall(4));


        assertTrue(model.moveStudentToHall(5));

        assertNotEquals(GamePhase.MOVE_STUDENTS, model.roundManager.getGamePhase());
        assertEquals(GamePhase.MOVE_MOTHER_NATURE, model.roundManager.getGamePhase());
        assertFalse(model.moveStudentToHall(6));
        assertFalse(model.moveStudentToHall(7));
        assertFalse(model.moveStudentToHall(6));
        int numOfNull = 0;
        for (StudentColor s : model.playersManager.getSchoolBoard(current).getStudentsInEntrance()) {
            if (s == null)
                numOfNull++;
        }
        assertEquals(model.playersManager.getPreset().getMaxNumMoves(), numOfNull);


        assertEquals(GamePhase.MOVE_MOTHER_NATURE, model.roundManager.getGamePhase());
        clearNotifications();
        assertFalse(model.getStudentsFromCloud(5));
        checkEmptyNotifications();

        model.roundManager.startChooseCloudPhase();
        assertTrue(model.getStudentsFromCloud(2));
        checkNotifications();

        for (StudentColor s : model.clouds.get(2).getStudents()) {
            assertNull(s);
        }
        //check the implementation of the getStudentsFromCloud
        numOfNull = 0;
        ArrayList<StudentColor> entrance = model.playersManager.getSchoolBoard(current).getStudentsInEntrance();
        for (int i = 0; i < model.playersManager.getPreset().getEntranceCapacity(); i++) {
            if (entrance.get(i) == null)
                numOfNull++;
        }
        assertEquals(0, numOfNull);
    }

    /**
     * Test the moving of Students from Entrance to Island, test limit case like removing from an empty space or playing more moves than allowed.
     * After all the moves has been played check the Game Phase and try to move mother nature
     */
    @Test
    void moveStudentsMoveMotherNature() {
        createGameModel(GamePreset.THREE);
        model.roundManager.startActionPhase();
        model.playersManager.nextPlayer();
        assertTrue(model.moveStudentToIsland(1, 4));
        checkNotifications();
        assertFalse(model.moveStudentToIsland(1, 4));
        assertFalse(model.moveStudentToIsland(1, 4));
        checkEmptyNotifications();
        assertTrue(model.moveStudentToIsland(2, 4));

        assertTrue(model.moveStudentToIsland(3, 4));

        int oldMotherNature = model.motherNatureIndex;
        clearNotifications();
        assertFalse(model.moveMotherNature(1));
        checkEmptyNotifications();
        assertEquals(oldMotherNature, model.motherNatureIndex);

        assertTrue(model.moveStudentToIsland(5, 4));

        assertEquals(GamePhase.MOVE_MOTHER_NATURE, model.roundManager.getGamePhase());

        int maxMoves = model.playersManager.getPlayedCard(model.playersManager.getCurrentPlayer()).getMoves();
        assertFalse(model.moveMotherNature(maxMoves + 1));
        assertEquals(oldMotherNature, model.motherNatureIndex);
        clearNotifications();
        assertTrue(model.moveMotherNature(maxMoves));
        checkNotifications();

        assertEquals((oldMotherNature + maxMoves) % model.islandsManager.getNumIslandGroups(), model.motherNatureIndex);
    }

    /**
     * Tests the implementation of checkProfessor and calcInfluence. Checks the first assignation of a Professor and then the relocation of
     * the professor. During the two operations calls calcInfluence and checks the validity of the method
     */
    @Test
    void checkProfessor() {
        createGameModel(GamePreset.THREE);
        ArrayList<Player> players = model.playersManager.getPlayers();
        Player curr = model.playersManager.getCurrentPlayer();
        Player next;

        assertNull(model.islandsManager.getTower(model.motherNatureIndex));
        for (Island i : model.islandsManager.getIslandGroup(model.motherNatureIndex).getIslands()) {
            assertEquals(0, i.getNumStudents());
        }

        model.islandsManager.addStudent(StudentColor.BLUE, model.motherNatureIndex);
        model.islandsManager.addStudent(StudentColor.BLUE,model.motherNatureIndex);

        for (Player p : players) {
            for (StudentColor s : StudentColor.values()) {
                model.playersManager.getSchoolBoard(p).removeFromHall(s, 12);
            }
        }

        assertTrue(model.playersManager.getSchoolBoard().addToHall(StudentColor.BLUE));

        assertEquals(1, model.playersManager.getSchoolBoard().getStudentsInHall(StudentColor.BLUE));
        for (Player p : players) {
            if (!p.equals(curr))
                assertEquals(0, model.playersManager.getSchoolBoard(p).getStudentsInHall(StudentColor.BLUE));
        }

        model.checkProfessor(StudentColor.BLUE);
        assertTrue(model.playersManager.getSchoolBoard(curr).getProfessors().contains(StudentColor.BLUE));
        for (Player p : players) {
            if (!p.equals(curr))
                assertFalse(model.playersManager.getSchoolBoard(p).getProfessors().contains(StudentColor.BLUE));
        }
        model.playersManager.nextPlayer();
        int befTow = model.playersManager.getSchoolBoard(curr).getNumTowers();
        model.checkInfluence(model.motherNatureIndex);

        assertEquals(model.playersManager.getSchoolBoard(curr).getTower(), model.islandsManager.getTower(model.motherNatureIndex));
        assertEquals(befTow - 1, model.playersManager.getSchoolBoard(curr).getNumTowers());

        next = model.playersManager.getCurrentPlayer();

        assertTrue(model.playersManager.getSchoolBoard(next).addToHall(StudentColor.BLUE));
        assertTrue(model.playersManager.getSchoolBoard(next).addToHall(StudentColor.BLUE));

        //Check the swap of the professor
        model.checkProfessor(StudentColor.BLUE);
        assertFalse(model.playersManager.getSchoolBoard(curr).getProfessors().contains(StudentColor.BLUE));
        assertTrue(model.playersManager.getSchoolBoard(next).getProfessors().contains(StudentColor.BLUE));

        //check the swap of towers
        int befNextTow = model.playersManager.getSchoolBoard(next).getNumTowers();

        model.checkInfluence(model.motherNatureIndex);
        assertEquals(model.playersManager.getSchoolBoard(next).getTower(), model.islandsManager.getTower(model.motherNatureIndex));
        assertEquals(befTow, model.playersManager.getSchoolBoard(curr).getNumTowers());
        assertEquals(befNextTow - 1,model.playersManager.getSchoolBoard(next).getNumTowers());



    }

    /**
     * Tests the implementation of mergeIsland by forcing the swap and the merge control.
     * Check the value of motherNature after the call to mergeIsland.
     */
    @Test
    void checkMergeIslandGroups() {
        createGameModel(GamePreset.THREE);
        ArrayList<Island> old;
        int oldLength;
        model.motherNatureIndex = 3;
        model.islandsManager.setTower(Tower.WHITE,0);
        model.islandsManager.setTower(Tower.BLACK,1);
        model.islandsManager.setTower(Tower.WHITE,2);
        model.islandsManager.setTower(Tower.WHITE,3);
        model.islandsManager.setTower(Tower.BLACK,4);
        old = model.islandsManager.getIslandGroup(2).getIslands();
        oldLength = model.islandsManager.getNumIslandGroups();
        model.checkMergeIslandGroups(2);
        assertTrue(model.islandsManager.getIslandGroup(2).getIslands().containsAll(old));
        assertEquals(oldLength - 1,model.islandsManager.getNumIslandGroups());
        assertEquals(2,model.motherNatureIndex);
        assertEquals(Tower.WHITE,model.islandsManager.getTower(0));
        assertEquals(Tower.BLACK,model.islandsManager.getTower(1));
        assertEquals(Tower.WHITE,model.islandsManager.getTower(2));
        assertEquals(Tower.BLACK,model.islandsManager.getTower(3));
        model.islandsManager.setTower(Tower.BLACK,2);
        old.clear();
        old.addAll(model.islandsManager.getIslandGroup(2).getIslands());
        old.addAll(model.islandsManager.getIslandGroup(3).getIslands());
        oldLength = model.islandsManager.getNumIslandGroups();
        model.checkMergeIslandGroups(2);
        assertEquals(oldLength - 2,model.islandsManager.getNumIslandGroups());
        assertEquals(1,model.motherNatureIndex);
    }

    /**
     * Checks the winner after the remove of all the tower in a player' school board.
     */
    @Test
    void checkWinByTower() {
        createGameModel(GamePreset.THREE);
        Tower test = model.playersManager.getSchoolBoard().getTower();
        int nTower = model.playersManager.getSchoolBoard().getNumTowers();
        model.islandsManager.setTower(test, 0);
        for (int i = 1; i < nTower; i++) {
            model.swapTowers(i, test);
            model.checkMergeIslandGroups(i - 1);
        }
        model.swapTowers(nTower, test);

        for(Player p : model.playersManager.getPlayers()){
            if(p.equals(model.playersManager.getCurrentPlayer()))
                assertTrue(model.roundManager.getWinners().contains(model.playersManager.getSchoolBoard(p).getTower()));
            else{
                assertFalse(model.roundManager.getWinners().contains(model.playersManager.getSchoolBoard(p).getTower()));
            }
        }

    }

    /**
     * Checks the winner after the last round when all the player Cards have been played.
     */
    @Test
    void checkWinByAssistantCards() {
        createGameModel(GamePreset.THREE);
        Player curr = model.playersManager.getCurrentPlayer();
        assertTrue(model.playersManager.getSchoolBoard().removeTowers(5));

        model.roundManager.nextRound();
        for (Player ignored : model.playersManager.getPlayers()) {
            for(AssistantCard a : AssistantCard.values()) {
                if (!a.equals(AssistantCard.ONE))
                    model.playersManager.currentPlayerPlayed(a);
            }
            clearNotifications();
            assertTrue(model.playAssistantCard(AssistantCard.ONE));
            checkNotifications();
        }


        model.nextRound();


        for(Player p : model.playersManager.getPlayers()){
            if(p.equals(curr))
                assertTrue(model.roundManager.getWinners().contains(model.playersManager.getSchoolBoard(p).getTower()));
            else{
                assertFalse(model.roundManager.getWinners().contains(model.playersManager.getSchoolBoard(p).getTower()));
            }
        }
    }

    /**
     * Checks the winner after all the Students have been used.
     */
    @Test
    void checkWinByStudents() {
        createGameModel(GamePreset.TWO);

        assertTrue(model.playersManager.getSchoolBoard().removeTowers(5));
        Player curr = model.playersManager.getCurrentPlayer();

        do{
            for(Cloud c : model.clouds){
                c.popStudents();
            }
            model.nextRound();
        }
        while(!model.bag.isEmpty());
        assertTrue(true);
        model.nextRound();
        assertFalse(model.roundManager.getWinners().isEmpty());

        for(Player p : model.playersManager.getPlayers()){
            if(p.equals(curr))
                assertTrue(model.roundManager.getWinners().contains(model.playersManager.getSchoolBoard(p).getTower()));
            else{
                assertFalse(model.roundManager.getWinners().contains(model.playersManager.getSchoolBoard(p).getTower()));
            }
        }
    }

    /**
     * Tests the method removePlayer. Try to remove a player that doesn't exist
     */
    @Test
    void removePlayer(){
        GameModel model = new GameModel(GamePreset.THREE);
        assertFalse(model.removePlayer("1"));
        assertTrue(model.addPlayer(pC.getPlayer("1", Wizard.ONE)));
        assertTrue(model.addPlayer(pC.getPlayer("2", Wizard.ONE)));
        assertTrue(model.removePlayer("1"));
        assertFalse(model.removePlayer("1"));
        assertTrue(model.addPlayer(pC.getPlayer("1", Wizard.ONE)));
        assertTrue(model.addPlayer(pC.getPlayer("3", Wizard.ONE)));
    }

}