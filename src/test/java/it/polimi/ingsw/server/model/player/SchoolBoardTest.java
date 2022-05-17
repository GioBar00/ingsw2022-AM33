package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class SchoolBoardTest {

    /**
     * the test checks that the method removeTowers works properly in the two cases of usage: removing
     * just one tower at a time and removing more than one with the same call
     */
    @Test
    void removeTowers() {
        SchoolBoard s = new SchoolBoard(300, Tower.WHITE,10);
        assertTrue(s.removeTowers(1));
        assertEquals(9,s.getNumTowers());
        assertTrue(s.removeTowers(8));
        assertFalse(s.removeTowers(2));
        assertEquals(0,s.getNumTowers());
    }

    /**
     * checks that the method addTowers works correctly, right after the initialization of the board and also
     * once some towers are removed before being re-added; the method is used in this test in two ways: to add one
     * tower at a time and add more towers at the same time
     * by definition
     */
    @Test
    void addTowers() {
        SchoolBoard s = new SchoolBoard(10,Tower.BLACK,0);
        assertFalse(s.addTowers(1));

        SchoolBoard n = new SchoolBoard(10,Tower.BLACK, 10);
        assertEquals(10, n.getNumTowers());
        assertFalse(n.removeTowers(10));
        assertTrue(n.addTowers(1));
        assertEquals(1, n.getNumTowers());
        assertTrue(n.addTowers(3));
        assertEquals(4, n.getNumTowers());
        assertFalse(n.addTowers(10));
        assertEquals(4, n.getNumTowers());


        SchoolBoard m =new SchoolBoard(300,Tower.BLACK,30);
        assertEquals(30,m.getNumTowers());
        assertFalse( m.removeTowers(30));

        for(int i = 0; i < 30; i++) {
            assertEquals(i, m.getNumTowers());
            assertTrue(m.addTowers(1));
        }
        assertEquals(30, m.getNumTowers());
        assertFalse(m.addTowers(1));
        assertEquals(30, m.getNumTowers());
    }

    /**
     * checks that the professors of a SchoolBoard are initialized in the correct way and then modified through the
     * game as per the rules
     * professor that is not present called in a remove
     */
    @Test
    void ProfessorsTest(){
        SchoolBoard s = new SchoolBoard(20,Tower.GREY,10);
        EnumSet<StudentColor> profs;
        profs = s.getProfessors();
        //Check add and get
        assertEquals(0,profs.size());
        s.addProfessor(StudentColor.BLUE);
        profs = s.getProfessors();
        assertTrue(profs.contains(StudentColor.BLUE));
        for(StudentColor t : StudentColor.values()){
            if(!t.equals(StudentColor.BLUE))
                assertFalse(profs.contains(t));
        }
        assertEquals(1, profs.size());
        assertFalse(s.addProfessor(StudentColor.BLUE));
        s.addProfessor(StudentColor.RED);
        assertFalse(s.addProfessor(StudentColor.RED));
        //Check consistency
        profs.remove(StudentColor.BLUE);
        profs = s.getProfessors();
        assertTrue(profs.contains(StudentColor.BLUE));
        //Check remove and get
        s.removeProfessor(StudentColor.BLUE);
        profs = s.getProfessors();
        assertEquals(1,profs.size());
        s.removeProfessor(StudentColor.RED);
        profs = s.getProfessors();
        assertEquals(0,profs.size());
        for(StudentColor t : StudentColor.values()){
            assertFalse(profs.contains(t));
        }
        assertFalse(s.removeProfessor(StudentColor.BLUE));


    }

    /**
     * the test checks for the correct use of two methods related to the entrance of the SchoolBoard: addToEntrance and
     * removeFromEntrance
     */
    @Test
    void EntranceTest(){
        SchoolBoard s = new SchoolBoard(20,Tower.GREY,10);
        //Check addInEntrance and getStudentsInEntrance
        for(int i =0 ; i < 3; i++){
         assertTrue(s.addToEntrance(StudentColor.BLUE));
         assertEquals(StudentColor.BLUE, s.getStudentInEntrance(i));
        }

        ArrayList<StudentColor>entrance = s.getStudentsInEntrance();
        assertTrue(entrance.contains(StudentColor.BLUE));
        assertFalse(entrance.contains(StudentColor.GREEN));
        assertFalse(entrance.contains(StudentColor.YELLOW));
        assertEquals(-1 ,entrance.indexOf(StudentColor.GREEN));
        assertEquals(20, entrance.size());
        assertTrue(s.moveToHall(2));
        assertTrue(s.addToEntrance(StudentColor.GREEN));
        entrance.remove(2);
        entrance = s.getStudentsInEntrance();
        assertEquals(20, entrance.size());
        assertEquals(StudentColor.GREEN, s.getStudentInEntrance(2));

        //Check removeFromEntrance
        SchoolBoard m = new SchoolBoard(2,Tower.GREY,10);
        assertTrue(m.addToEntrance(StudentColor.BLUE));
        assertTrue(m.addToEntrance(StudentColor.MAGENTA));

        assertTrue(m.removeFromEntrance(0));
        assertFalse(m.getStudentsInEntrance().contains(StudentColor.BLUE));
        assertTrue(m.removeFromEntrance(0));
        assertNull(m.getStudentsInEntrance().get(0));
        assertEquals( StudentColor.MAGENTA, m.getStudentsInEntrance().get(1));
        m.getStudentsInEntrance().add(StudentColor.GREEN);
        assertTrue(m.removeFromEntrance(1));
        assertFalse(m.getStudentsInEntrance().contains(StudentColor.BLUE));
        assertFalse(m.getStudentsInEntrance().contains(StudentColor.MAGENTA));

    }

    /**
     * the test checks two methods related to che Hall of the SchoolBoard: moveToHall and removeFromHall
     */
    @Test
    void HallTest(){
        SchoolBoard s = new SchoolBoard(10,Tower.BLACK,0);
        s.addToEntrance(StudentColor.BLUE);
        s.addToEntrance(StudentColor.BLUE);
        s.addToEntrance(StudentColor.GREEN);
        s.addToEntrance(StudentColor.MAGENTA);
        assertTrue(s.moveToHall(0));
        assertEquals(1,s.getStudentsInHall(StudentColor.BLUE));
        assertNull(s.getStudentsInEntrance().get(0));
        assertEquals(StudentColor.BLUE,s.getStudentsInEntrance().get(1));
        assertTrue(s.moveToHall(1));
        assertEquals(2,s.getStudentsInHall(StudentColor.BLUE));
        assertTrue(s.moveToHall(2));
        assertTrue(s.moveToHall(3));

        assertEquals(1,s.getStudentsInHall(StudentColor.GREEN));
        assertEquals(1,s.getStudentsInHall(StudentColor.MAGENTA));

        assertTrue(s.removeFromHall(StudentColor.MAGENTA,0));
        assertEquals(1,s.getStudentsInHall(StudentColor.MAGENTA));
        assertTrue(s.removeFromHall(StudentColor.MAGENTA,1));
        assertEquals(0,s.getStudentsInHall(StudentColor.MAGENTA));
        s.tryRemoveFromHall(StudentColor.GREEN,10);
        assertEquals(0,s.getStudentsInHall(StudentColor.GREEN));
        assertEquals(0,s.getStudentsInHall(StudentColor.GREEN));
        assertTrue(s.removeFromHall(StudentColor.BLUE,2));
        assertEquals(0,s.getStudentsInHall(StudentColor.BLUE));
        assertEquals(0,s.getStudentsInHall(StudentColor.RED));
        assertEquals(0,s.getStudentsInHall(StudentColor.YELLOW));

        SchoolBoard s1 = new SchoolBoard(GamePreset.FOUR.getEntranceCapacity(), Tower.BLACK,0);
        for (int i = 0; i < GamePreset.FOUR.getEntranceCapacity(); i++) {
            s1.addToEntrance(StudentColor.BLUE);
        }
        assertFalse(s1.addToEntrance(StudentColor.BLUE));

        assertFalse(s1.addToEntrance(StudentColor.BLUE, GamePreset.FOUR.getEntranceCapacity()));
        assertFalse(s1.addToEntrance(StudentColor.BLUE, 0));

        assertFalse(s1.removeFromEntrance(-1));
        assertFalse(s1.removeFromEntrance(GamePreset.FOUR.getEntranceCapacity()));

        assertFalse(s1.moveToHall(-1));
        assertFalse(s1.moveToHall(GamePreset.FOUR.getEntranceCapacity()));

        assertTrue(s1.removeFromEntrance(0));
        assertFalse(s1.moveToHall(0));
    }

}