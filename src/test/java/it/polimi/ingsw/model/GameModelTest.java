package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.islands.Island;
import it.polimi.ingsw.model.player.Player;
import org.junit.jupiter.api.*;

import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
        assertThrows(NoPermissionException.class, () -> model.initializeGame());
        try {
            model.addPlayer("1");
        } catch (NoPermissionException | NameAlreadyBoundException e) {
            fail();
        }
        assertThrows(NoPermissionException.class, () -> model.initializeGame());
        try {
            model.addPlayer("2");
        } catch (NoPermissionException | NameAlreadyBoundException e) {
            fail();
        }
        assertThrows(NoPermissionException.class, () -> model.initializeGame());
        assertEquals(1, model.getAvailablePlayerSlots());
        try {
            model.addPlayer("3");
        } catch (NoPermissionException | NameAlreadyBoundException e) {
            fail();
        }
        assertEquals(0, model.getAvailablePlayerSlots());
        assertEquals(GameState.UNINITIALIZED, model.getGameState());
        try {
            model.initializeGame();
        } catch (NoPermissionException e) {
            fail();
        }
        model.startGame();
        assertEquals(3, model.playersManager.getPlayers().size());
        assertEquals(GameState.STARTED, model.getGameState());
        assertThrows(NoPermissionException.class, () -> model.initializeGame());
        fMotherNature = model.motherNatureIndex;
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
        try {
            expectedOrder[2] = model.playersManager.getCurrentPlayer();
            model.playAssistantCard(AssistantCard.SEVEN);
        } catch (Exception e) {
            fail();
        }
        try {
            expectedOrder[1] = model.playersManager.getCurrentPlayer();
            model.playAssistantCard(AssistantCard.FIVE);
        } catch (Exception e) {
            fail();
        }
        assertThrows(Exception.class, () -> model.playAssistantCard(AssistantCard.FIVE));
        try {
            expectedOrder[0] = model.playersManager.getCurrentPlayer();
            model.playAssistantCard(AssistantCard.FOUR);
        } catch (Exception e) {
            fail();
        }
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
        try {
            model.moveStudentToHall(0);
        } catch (Exception e) {
            fail();
        }
        assertThrows(Exception.class, () -> model.moveStudentToHall(0));
        assertThrows(Exception.class, () -> model.moveStudentToHall(model.preset.getEntranceCapacity()));
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
        try {
            model.moveStudentToHall(2);
        } catch (Exception e) {
            fail();
        }
        try {
            model.moveStudentToHall(4);
        } catch (Exception e) {
            fail();
        }
        assertEquals(GamePhase.MOVE_STUDENTS, model.roundManager.getGamePhase());
        try {
            model.moveStudentToHall(5);
        } catch (Exception e) {
            fail();
        }
        assertThrows(Exception.class, () -> model.moveStudentToHall(6));
        int numOfNull = 0;
        for (StudentColor s : model.playersManager.getSchoolBoard(current).getStudentsInEntrance()) {
            if (s == null)
                numOfNull++;
        }
        assertEquals(model.preset.getMaxNumMoves(), numOfNull);
        try {
            model.getStudentsFromCloud(2);

            for (StudentColor s : model.clouds.get(2).getStudents()) {
                assertNull(s);
            }
        } catch (LimitExceededException e) {
            fail();
        }
        assertEquals(GamePhase.MOVE_MOTHER_NATURE, model.roundManager.getGamePhase());
        assertThrows(LimitExceededException.class, () -> model.getStudentsFromCloud(5));

        //check the implementation of the getStudentsFromCloud
        numOfNull = 0;
        ArrayList<StudentColor> entrance = model.playersManager.getSchoolBoard(current).getStudentsInEntrance();
        for (int i = 0; i < model.preset.getEntranceCapacity(); i++) {
            if (entrance.get(i) == null)
                numOfNull++;
        }
        assertEquals(0, numOfNull);
        //assertTrue(model.playersManager.getSchoolBoard(current).getStudentsInEntrance().containsAll(toAdd));


    }

    /**
     * Test the moving of Students from Entrance to Island, test limit case like removing from an empty space or playing more moves than allowed.
     * After all the moves has been played check the Game Phase and try to move mother nature
     */
    @Test
    void moveStudentsMoveMotherNature() {
        model.roundManager.startActionPhase();
        model.roundManager.clearMoves();
        model.playersManager.nextPlayer();
        try {
            model.moveStudentToIsland(1, 4, 0);
        } catch (Exception e) {
            fail();
        }
        assertThrows(Exception.class, () -> model.moveStudentToIsland(1, 4, 1));
        assertThrows(Exception.class, () -> model.moveStudentToIsland(1, 4, 0));
        try {
            model.moveStudentToIsland(2, 4, 0);
        } catch (Exception e) {
            fail();
        }
        try {
            model.moveStudentToIsland(3, 4, 0);
        } catch (Exception e) {
            fail();
        }
        int oldMotherNature = model.motherNatureIndex;
        assertThrows(LimitExceededException.class, () -> model.moveMotherNature(1));
        assertEquals(oldMotherNature, model.motherNatureIndex);

        try {
            model.moveStudentToIsland(5, 4, 0);
        } catch (Exception e) {
            fail();
        }

        assertEquals(GamePhase.MOVE_MOTHER_NATURE, model.roundManager.getGamePhase());


        int maxMoves = model.playersManager.getPlayedCard(model.playersManager.getCurrentPlayer()).getMoves();
        assertThrows(LimitExceededException.class, () -> model.moveMotherNature(maxMoves + 1));
        assertEquals(oldMotherNature, model.motherNatureIndex);
        try {
            model.moveMotherNature(maxMoves);
        } catch (LimitExceededException e) {
            fail();
        }
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

        model.islandsManager.addStudent(StudentColor.BLUE, fMotherNature, 0);
        model.islandsManager.addStudent(StudentColor.BLUE,fMotherNature,0);

        for (Player p : players) {
            for (StudentColor s : StudentColor.values()) {
                model.playersManager.getSchoolBoard(p).removeFromHall(s, 12);
            }
        }

        try {
            model.playersManager.getSchoolBoard().addToHall(StudentColor.BLUE);
        } catch (LimitExceededException e) {
            fail();
        }
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

        try {
            model.playersManager.getSchoolBoard(next).addToHall(StudentColor.BLUE);
            model.playersManager.getSchoolBoard(next).addToHall(StudentColor.BLUE);
        } catch (LimitExceededException e) {
            fail();
        }
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
        assertEquals(model.playersManager.getCurrentPlayer(), model.roundManager.getWinner());
        GameModel m1 = new GameModel(GamePreset.THREE);
        try {
            m1.addPlayer("1");
        } catch (NoPermissionException | NameAlreadyBoundException e) {
            fail();
        }
        curr = m1.playersManager.getCurrentPlayer();
        try {
            m1.addPlayer("2");
        } catch (NoPermissionException | NameAlreadyBoundException e) {
            fail();
        }
        try {
            m1.addPlayer("3");
        } catch (NoPermissionException | NameAlreadyBoundException e) {
            fail();
        }
        try {
            m1.initializeGame();
        } catch (NoPermissionException e) {
            fail();
        }

        try {
            m1.playersManager.getSchoolBoard().removeTowers(5);
        } catch (LimitExceededException e) {
            fail();
        }

        for(AssistantCard a : AssistantCard.values()) {
            if (!a.equals(AssistantCard.TEN))
                m1.playersManager.currentPlayerPlayed(a);
        }

        try {
            m1.playAssistantCard(AssistantCard.TEN);
        } catch (Exception e) {
            fail();
        }
        m1.endActionPhase();
        assertEquals(curr,m1.roundManager.getWinner());


        GameModel m2 = new GameModel(GamePreset.TWO);
        try {
            m2.addPlayer("1");
        } catch (NoPermissionException | NameAlreadyBoundException e) {
            fail();
        }
        try {
            m2.addPlayer("2");
        } catch (NoPermissionException | NameAlreadyBoundException e) {
            fail();
        }
        try {
            m2.initializeGame();
        } catch (NoPermissionException e) {
            fail();
        }

        try {
            m2.playersManager.getSchoolBoard().removeTowers(5);
            curr = m2.playersManager.getCurrentPlayer();
        } catch (LimitExceededException e) {
            fail();
        }
        do{
            for(Cloud c : m2.clouds){
                c.popStudents();
            }
            m2.endActionPhase();
        }
        while(!m2.bag.isEmpty());
        m2.endActionPhase();
        assertEquals(curr,m2.roundManager.getWinner());

    }
}