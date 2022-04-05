package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.islands.Island;
import it.polimi.ingsw.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameModelTest {
    GameModel model;
    int fMotherNature = 0;

    /**
     * Test the creation of a three payers game and the setting up of the match
     */
    @BeforeEach
    void creationTest() {
        model = new GameModel(GamePreset.THREE);
        assertFalse(model.initializeGame());
        assertTrue(model.addPlayer("1"));

        assertFalse(model.initializeGame());
        assertTrue(model.addPlayer("2"));
        assertFalse(model.initializeGame());
        assertEquals(1, model.getAvailablePlayerSlots());
        assertTrue(model.addPlayer("3"));
        assertEquals(0, model.getAvailablePlayerSlots());
        assertEquals(GameState.UNINITIALIZED, model.getGameState());

        assertTrue(model.initializeGame());
        assertTrue(model.startGame());

        assertEquals(3, model.playersManager.getPlayers().size());
        assertEquals(GameState.STARTED, model.getGameState());
        assertFalse(model.initializeGame());
        fMotherNature = model.motherNatureIndex;
        assertEquals(0,model.islandsManager.getIslandGroup(fMotherNature).getIslands().get(0).getNumStudents());
        assertEquals(0,model.islandsManager.getIslandGroup((fMotherNature + 6) % 12).getIslands().get(0).getNumStudents());
        numTowersAndStudent();
        playAssistantCard();
    }

    /**
     * check the integrity of all the movement done by calculating the amount of students and towers
     */
    void numTowersAndStudent() {
        int numStud = model.bag.students.size();
        int numTowers = model.preset.getTowersNumber() * model.preset.getPlayersNumber();
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
        assertEquals(model.preset.getCloudsNumber(), model.clouds.size());

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
        Player[] expectedOrder = new Player[3];

        expectedOrder[2] = model.playersManager.getCurrentPlayer();
        assertTrue(model.playAssistantCard(AssistantCard.SEVEN));


        expectedOrder[1] = model.playersManager.getCurrentPlayer();
        assertTrue(model.playAssistantCard(AssistantCard.FIVE));

        assertFalse(model.playAssistantCard(AssistantCard.FIVE));

        expectedOrder[0] = model.playersManager.getCurrentPlayer();
        assertTrue(model.playAssistantCard(AssistantCard.FOUR));

        model.playersManager.calculatePlayerOrder();
        for (int i = 0; i < model.preset.getPlayersNumber(); i++) {
            assertEquals(expectedOrder[i], model.playersManager.getPlayers().get(i));
        }

    }

    /**
     * Test the moving of Students from the entrance to the hall. Tries to move from an empty slot and check the amount of students in hall.
     * After the allowed moves choose a Cloud and refill the player entrance
     */
    @Test
    void moveStudentsRefillEntrance() {
        Player current = model.playersManager.getCurrentPlayer();
        assertTrue(model.moveStudentToHall(0));

        assertFalse(model.moveStudentToHall(0));
        assertFalse(model.moveStudentToHall(model.preset.getEntranceCapacity()));
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
        assertEquals(model.preset.getMaxNumMoves(), numOfNull);


        assertEquals(GamePhase.MOVE_MOTHER_NATURE, model.roundManager.getGamePhase());
        assertFalse(model.getStudentsFromCloud(5));

        model.roundManager.startChooseCloudPhase();
        assertTrue(model.getStudentsFromCloud(2));

        for (StudentColor s : model.clouds.get(2).getStudents()) {
            assertNull(s);
        }
        //check the implementation of the getStudentsFromCloud
        numOfNull = 0;
        ArrayList<StudentColor> entrance = model.playersManager.getSchoolBoard(current).getStudentsInEntrance();
        for (int i = 0; i < model.preset.getEntranceCapacity(); i++) {
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
        model.roundManager.startActionPhase();
        model.playersManager.nextPlayer();
        assertTrue(model.moveStudentToIsland(1, 4));

        assertFalse(model.moveStudentToIsland(1, 4));
        assertFalse(model.moveStudentToIsland(1, 4));
        assertTrue(model.moveStudentToIsland(2, 4));

        assertTrue(model.moveStudentToIsland(3, 4));

        int oldMotherNature = model.motherNatureIndex;
        assertFalse(model.moveMotherNature(1));
        assertEquals(oldMotherNature, model.motherNatureIndex);

        assertTrue(model.moveStudentToIsland(5, 4));

        assertEquals(GamePhase.MOVE_MOTHER_NATURE, model.roundManager.getGamePhase());

        int maxMoves = model.playersManager.getPlayedCard(model.playersManager.getCurrentPlayer()).getMoves();
        assertFalse(model.moveMotherNature(maxMoves + 1));
        assertEquals(oldMotherNature, model.motherNatureIndex);
        assertTrue(model.moveMotherNature(maxMoves));

        assertEquals((oldMotherNature + maxMoves) % model.islandsManager.getNumIslandGroups(), model.motherNatureIndex);
    }


    /**
     * Tests the implementation of checkProfessor and calcInfluence. Checks the first assignation of a Professor and then the relocation of
     * the professor. During the two operations calls calcInfluence and checks the validity of the method
     */
    @Test
    void checkProfessor() {
        ArrayList<Player> players = model.playersManager.getPlayers();
        Player curr = model.playersManager.getCurrentPlayer();
        Player next;

        assertNull(model.islandsManager.getTower(fMotherNature));
        for (Island i : model.islandsManager.getIslandGroup(fMotherNature).getIslands()) {
            assertEquals(0, i.getNumStudents());
        }

        model.islandsManager.addStudent(StudentColor.BLUE, fMotherNature);
        model.islandsManager.addStudent(StudentColor.BLUE,fMotherNature);

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
        model.checkInfluence(fMotherNature);

        assertEquals(model.playersManager.getSchoolBoard(curr).getTower(), model.islandsManager.getTower(fMotherNature));
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

        model.checkInfluence(fMotherNature);
        assertEquals(model.playersManager.getSchoolBoard(next).getTower(), model.islandsManager.getTower(fMotherNature));
        assertEquals(befTow, model.playersManager.getSchoolBoard(curr).getNumTowers());
        assertEquals(befNextTow - 1,model.playersManager.getSchoolBoard(next).getNumTowers());



    }

    /**
     * Tests the implementation of mergeIsland by forcing the swap and the merge control.
     * Check the value of motherNature after the call to mergeIsland.
     */
    @Test
    void checkMergeIslandGroups() {
        GameModel m = new GameModel(GamePreset.THREE);
        ArrayList<Island> old;
        int oldLength;
        m.motherNatureIndex = 3;
        m.islandsManager.setTower(Tower.WHITE,0);
        m.islandsManager.setTower(Tower.BLACK,1);
        m.islandsManager.setTower(Tower.WHITE,2);
        m.islandsManager.setTower(Tower.WHITE,3);
        m.islandsManager.setTower(Tower.BLACK,4);
        old = m.islandsManager.getIslandGroup(2).getIslands();
        oldLength = m.islandsManager.getNumIslandGroups();
        m.checkMergeIslandGroups(2);
        assertTrue(m.islandsManager.getIslandGroup(2).getIslands().containsAll(old));
        assertEquals(oldLength - 1,m.islandsManager.getNumIslandGroups());
        assertEquals(2,m.motherNatureIndex);
        assertEquals(Tower.WHITE,m.islandsManager.getTower(0));
        assertEquals(Tower.BLACK,m.islandsManager.getTower(1));
        assertEquals(Tower.WHITE,m.islandsManager.getTower(2));
        assertEquals(Tower.BLACK,m.islandsManager.getTower(3));
        m.islandsManager.setTower(Tower.BLACK,2);
        old.clear();
        old.addAll(m.islandsManager.getIslandGroup(2).getIslands());
        old.addAll(m.islandsManager.getIslandGroup(3).getIslands());
        oldLength = m.islandsManager.getNumIslandGroups();
        m.checkMergeIslandGroups(2);
        assertEquals(oldLength - 2,m.islandsManager.getNumIslandGroups());
        assertEquals(1,m.motherNatureIndex);
    }

    /**
     * Checks the winner after the remove of all the tower in a player' school board.
     * Checks the winner after the last round when all the player Cards have been played.
     * Checks the winner after all the Students have been used.
     */
    @Test
    void checkWinner() {
        Tower test = model.playersManager.getSchoolBoard().getTower();
        int nTower = model.playersManager.getSchoolBoard().getNumTowers();
        Player curr;
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


        GameModel m1 = new GameModel(GamePreset.THREE);
        assertTrue(m1.addPlayer("1"));

        curr = m1.playersManager.getCurrentPlayer();
        assertTrue(m1.addPlayer("2"));
        assertTrue(m1.addPlayer("3"));

        assertTrue(m1.initializeGame());
        assertTrue(m1.playersManager.getSchoolBoard().removeTowers(5));

        for(AssistantCard a : AssistantCard.values()) {
            if (!a.equals(AssistantCard.TEN))
                m1.playersManager.currentPlayerPlayed(a);
        }

        assertTrue(m1.playAssistantCard(AssistantCard.TEN));

        m1.nextRound();

        for(Player p : m1.playersManager.getPlayers()){
            if(p.equals(curr))
                assertTrue(m1.roundManager.getWinners().contains(m1.playersManager.getSchoolBoard(p).getTower()));
            else{
                assertFalse(m1.roundManager.getWinners().contains(m1.playersManager.getSchoolBoard(p).getTower()));
            }
        }


        GameModel m2 = new GameModel(GamePreset.TWO);
        assertTrue(m2.addPlayer("1"));

        assertTrue(m2.addPlayer("2"));
        assertTrue(m2.initializeGame());

        assertTrue(m2.playersManager.getSchoolBoard().removeTowers(5));
        curr = m2.playersManager.getCurrentPlayer();

        do{
            for(Cloud c : m2.clouds){
                c.popStudents();
            }
            m2.nextRound();
        }
        while(!m2.bag.isEmpty());
        assertTrue(true);
        m2.nextRound();
        assertFalse(m2.roundManager.getWinners().isEmpty());

        for(Player p : m2.playersManager.getPlayers()){
            if(p.equals(curr))
                assertTrue(m2.roundManager.getWinners().contains(m2.playersManager.getSchoolBoard(p).getTower()));
            else{
                assertFalse(m2.roundManager.getWinners().contains(m2.playersManager.getSchoolBoard(p).getTower()));
            }
        }

    }
}