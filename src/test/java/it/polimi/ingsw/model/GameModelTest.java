package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.naming.LimitExceededException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameModelTest {
    GameModel model = new GameModel(GamePreset.THREE);
    /**
     *  Test the creation of a three payers game and the setting up of the match
     */
    @BeforeAll
    void creationTest() {
        assertThrows(NoPermissionException.class,()-> model.initializeGame());
        try {
            model.addPlayer("1");
        }
        catch (NoPermissionException | NameAlreadyBoundException e){
            fail();
        }
        assertThrows(NoPermissionException.class,()-> model.initializeGame());
        try {
            model.addPlayer("2");
        }
        catch (NoPermissionException | NameAlreadyBoundException e){
            fail();
        }
        assertEquals(1, model.getAvailablePlayerSlots());
        assertThrows(NoPermissionException.class, ()-> model.initializeGame());
        assertEquals(1, model.getAvailablePlayerSlots());
        try {
            model.addPlayer("3");
        }
        catch (NoPermissionException | NameAlreadyBoundException e){
            fail();
        }
        assertEquals(0, model.getAvailablePlayerSlots());
        model.startGame();
        assertEquals(GameState.UNINITIALIZED,model.getGameState());
        try {
            model.initializeGame();
        }
        catch(NoPermissionException e){
            fail();
        }
        model.startGame();
        assertEquals(3, model.playersManager.getPlayers().size());
        assertEquals(GameState.STARTED, model.getGameState());
        assertThrows(NoPermissionException.class, ()-> model.initializeGame());
        numTowersAndStudent();
        playAssistantCard();
    }

    /**
     * check the integrity of all the movement done by calculating the amount of students and towers
     */
    void numTowersAndStudent(){
        int numStud = model.bag.students.size();
        int numTowers = model.preset.getTowersNumber() * model.preset.getPlayersNumber();
        assert model.playersManager.getPlayers() != null;
        for (Player p: model.playersManager.getPlayers()) {
            numStud += model.playersManager.getSchoolBoard(p).getStudentsInEntrance().stream().filter(Objects::nonNull).toArray().length;
            for (StudentColor s: StudentColor.values())
                numStud += model.playersManager.getSchoolBoard().getStudentsInHall(s);
        }
        for (Cloud c: model.clouds)
            numStud += c.getStudents().stream().filter(Objects::nonNull).toArray().length;
        assertEquals(12, model.islandsManager.getNumIslandGroups());
        for (int i = 0; i < model.islandsManager.getNumIslandGroups(); i++)
            for (int j = 0; j < model.islandsManager.getIslandGroup(i).size(); j ++)
                numStud += model.islandsManager.getIslandGroup(i).getIslands().get(j).getNumStudents();
        assertEquals(130, numStud);
        assertEquals(model.preset.getCloudsNumber(),model.clouds.size());

        for(Player p: model.playersManager.getPlayers()){
            numTowers = numTowers - model.playersManager.getSchoolBoard(p).getNumTowers();
        }
        for(int i = 0; i < model.islandsManager.getNumIslandGroups(); i++)
            if(model.islandsManager.getTower(i) != null){
                numTowers = numTowers - model.islandsManager.getIslandGroup(i).size();
            }
        assertEquals(0, numTowers);
    }

    /**
     * Check the throwing of the exception when one player tries to play a card that has already been played.
     */
    void playAssistantCard() {
        assertEquals(GamePhase.PLANNING, model.roundManager.getGamePhase());
        try{model.playAssistantCard(AssistantCard.SEVEN);}
        catch (Exception e){
            fail();
        }
        try{model.playAssistantCard(AssistantCard.FIVE);}
        catch (Exception e){
            fail();
        }
        assertThrows(Exception.class,()->model.playAssistantCard(AssistantCard.FIVE));
        try{model.playAssistantCard(AssistantCard.FOUR);}
        catch (Exception e){
            fail();
        }
    }

    /**
     * Test the moving of Students from the entrance to the hall. Tries to move from a empty slot and check the amount of students in hall.
     * After the allowed moves choose a Cloud and refill the player entrance
     */
    @Test
    void moveStudentsRefillEntrance() {
        Player current = model.playersManager.getCurrentPlayer();
        try {
            model.moveStudentToHall(0);
        }catch (Exception e){
            fail();
        }
        assertThrows(Exception.class,()->model.moveStudentToHall(0));
        assertThrows(Exception.class,()->model.moveStudentToHall(model.preset.getEntranceCapacity()));
        boolean check = false;
        for(StudentColor s : StudentColor.values()){
            if(model.playersManager.getSchoolBoard().getStudentsInHall(s) == 1)
            {
                if(check){
                    fail();
                }
                check = true;
            }
        }
        assertTrue(check);
        try {
            model.moveStudentToHall(2);
        }catch (Exception e){
            fail();
        }
        try {
            model.moveStudentToHall(4);
        }catch (Exception e){
            fail();
        }
        assertEquals(GamePhase.MOVE_STUDENTS, model.roundManager.getGamePhase());
        try {
            model.moveStudentToHall(5);
        }catch (Exception e){
            fail();
        }
        assertThrows(Exception.class,()-> model.moveStudentToHall(6));
        int numOfNull = 0;
        for(StudentColor s : model.playersManager.getSchoolBoard(current).getStudentsInEntrance()){
            if(s == null)
                numOfNull++;
        }
        assertEquals(model.preset.getMaxNumMoves(),numOfNull);
        try {
            model.getStudentsFromCloud(2);
        }
        catch (LimitExceededException e){
            fail();
        }
        assertEquals(GamePhase.MOVE_MOTHER_NATURE,model.roundManager.getGamePhase());
        assertThrows(LimitExceededException.class, ()->model.getStudentsFromCloud(5));

        //check the implementation of the getStudentsFromCloud
        numOfNull = 0;
        ArrayList<StudentColor> entrance =  model.playersManager.getSchoolBoard(current).getStudentsInEntrance();
        for(int i = 0; i < model.preset.getEntranceCapacity(); i++){
            if(entrance.get(i) == null)
                numOfNull++;
        }
        assertEquals(0,numOfNull);
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
        try{    model.moveStudentToIsland(1,4,0);}
        catch(Exception e ){
            fail();
        }
        assertThrows(Exception.class,()->model.moveStudentToIsland(1,4,1));
        assertThrows(Exception.class, ()->model.moveStudentToIsland(1,4,0));
        try{    model.moveStudentToIsland(2,4,0);}
        catch(Exception e ){
            fail();
        }
        try{    model.moveStudentToIsland(3,4,0);}
        catch(Exception e ) {
            fail();
        }
        int oldMotherNature = model.motherNatureIndex;
        assertThrows(LimitExceededException.class, ()->model.moveMotherNature(1));
        assertEquals(oldMotherNature,model.motherNatureIndex);

        try{    model.moveStudentToIsland(5,4,0);}
        catch(Exception e ) {
            fail();
        }

        assertEquals(GamePhase.MOVE_MOTHER_NATURE,model.roundManager.getGamePhase());


        int maxMoves = model.playersManager.getPlayedCard(model.playersManager.getCurrentPlayer()).getMoves();
        assertThrows(LimitExceededException.class, ()->model.moveMotherNature(maxMoves + 1 ));
        assertEquals(oldMotherNature,model.motherNatureIndex);
        try{ model.moveMotherNature(maxMoves);}
        catch(LimitExceededException e){fail();}
        assertEquals((oldMotherNature + maxMoves) % model.islandsManager.getNumIslandGroups(), model.motherNatureIndex);
    }


    @Test
    void checkProfessor() {
    }

    @Test
    void checkInfluence() {
    }

    @Test
    void testCheckInfluence() {
    }

    @Test
    void swapTowers() {
    }

    @Test
    void checkMergeIslandGroups() {
    }


    @Test
    void nextRound() {
    }

    @Test
    void endActionPhase() {
    }

    @Test
    void checkWinner() {
    }
}