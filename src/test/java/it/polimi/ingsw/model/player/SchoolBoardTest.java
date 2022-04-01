package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.enums.Tower;
import org.junit.jupiter.api.Test;

import javax.naming.LimitExceededException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SchoolBoardTest {

    /**
     * the test checks that the method removeTowers works properly in the two cases of usage: removing
     * just one tower at a time and removing more than one with the same call
     */
    @Test
    void removeTowers() {
        SchoolBoard s = new SchoolBoard(300, Tower.WHITE,10);
        try{s.removeTowers(1);
            assertEquals(9,s.getNumTowers());
            try{s.removeTowers(8);
                assertThrows(LimitExceededException.class,() -> s.removeTowers(2));
                assertEquals(0,s.getNumTowers());
            }
            catch (LimitExceededException e){
                fail();
            }

        }
        catch (LimitExceededException e){
            fail();
        }
    }

    /**
     * checks that the method addTowers works correctly, right after the initialization of the board and also
     * once some towers are removed before being re-added; the method is used in this test in two ways: to add one
     * tower at a time and add more towers at the same time
     * @throws LimitExceededException when the caller tries to add to the SchoolBoard more towers than allowed
     * by definition
     */
    @Test
    void addTowers() {
        final SchoolBoard s = new SchoolBoard(10,Tower.BLACK,0);
        assertThrows(LimitExceededException.class,() -> s.addTowers(1) );

        SchoolBoard n = new SchoolBoard(10,Tower.BLACK, 10);
        assertEquals(10, n.getNumTowers());
        assertThrows(LimitExceededException.class, ()->n.removeTowers(10));
        try {
            n.addTowers(1);
            assertEquals(1, n.getNumTowers());
        } catch (LimitExceededException ignored) {}
        try {
            n.addTowers(3);
            assertEquals(4, n.getNumTowers());
        } catch (LimitExceededException e){
            fail();
        }
        try {
            n.addTowers(10);
            assertEquals(14, n.getNumTowers());
        } catch (LimitExceededException e) {
            assertEquals(4, n.getNumTowers());

        }

        SchoolBoard m =new SchoolBoard(300,Tower.BLACK,30);
        assertEquals(30,m.getNumTowers());
        try {
            m.removeTowers(30);
        } catch (LimitExceededException ignored){}

        for(int i = 0; i < 30; i++) {
            assertEquals(i, m.getNumTowers());
            try {
                m.addTowers(1);
            }
            catch (LimitExceededException e){
                fail();
            }
        }
        assertEquals(30, m.getNumTowers());
        assertThrows(LimitExceededException.class,() -> m.addTowers(1) );
        assertEquals(30, m.getNumTowers());
    }

    /**
     * checks that the professors of a SchoolBoard are initialized in the correct way and then modified through the
     * game as per the rules
     * @throws Exception in two cases: if a professor that is already present in the enum is added again and if a
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
     * @throws Exception when a removeFromEntrance is called on an Entrance slot that is already empty
     */
    @Test
    void EntranceTest(){
        SchoolBoard s = new SchoolBoard(20,Tower.GREY,10);
        //Check addInEntrance and getStudentsInEntrance
        try {   s.addToEntrance(StudentColor.BLUE); }
        catch (Exception e ){ fail(); }
        try {   s.addToEntrance(StudentColor.BLUE); }
        catch (Exception e ){ fail(); }
        try {   s.addToEntrance(StudentColor.GREEN); }
        catch (Exception e ){ fail(); }
        ArrayList<StudentColor>entrance = s.getStudentsInEntrance();
        assertTrue(entrance.contains(StudentColor.BLUE));
        assertTrue(entrance.contains(StudentColor.GREEN));
        assertFalse(entrance.contains(StudentColor.YELLOW));
        assertEquals(StudentColor.GREEN, entrance.get(entrance.indexOf(StudentColor.GREEN)));
        assertEquals(20, entrance.size());
        entrance.remove(2);
        entrance = s.getStudentsInEntrance();
        assertEquals(20, entrance.size());
        assertEquals(StudentColor.GREEN, entrance.get(2));

        //Check removeFromEntrance
        SchoolBoard m = new SchoolBoard(2,Tower.GREY,10);
        try {
            m.addToEntrance(StudentColor.BLUE);
        }
        catch (Exception e){ fail(); }
        try {
            m.addToEntrance(StudentColor.PINK);
        }
        catch (Exception e){ fail(); }
        m.removeFromEntrance(0);
        assertFalse(m.getStudentsInEntrance().contains(StudentColor.BLUE));
        assertThrows(NoSuchElementException.class, () -> m.removeFromEntrance(0));
        assertNull(m.getStudentsInEntrance().get(0));
        assertEquals( StudentColor.PINK, m.getStudentsInEntrance().get(1));
        m.getStudentsInEntrance().add(StudentColor.GREEN);
        m.removeFromEntrance(1);
        assertFalse(m.getStudentsInEntrance().contains(StudentColor.BLUE));
        assertFalse(m.getStudentsInEntrance().contains(StudentColor.PINK));

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
        s.addToEntrance(StudentColor.PINK);
        try {
            s.moveToHall(0);

            assertEquals(1,s.getStudentsInHall(StudentColor.BLUE));
            assertNull(s.getStudentsInEntrance().get(0));
            assertEquals(StudentColor.BLUE,s.getStudentsInEntrance().get(1));
            s.moveToHall(1);
            assertEquals(2,s.getStudentsInHall(StudentColor.BLUE));
            s.moveToHall(2);
            s.moveToHall(3);
        } catch (LimitExceededException e) { fail();}
        assertEquals(1,s.getStudentsInHall(StudentColor.GREEN));
        assertEquals(1,s.getStudentsInHall(StudentColor.PINK));

        s.removeFromHall(StudentColor.PINK,0);
        assertEquals(1,s.getStudentsInHall(StudentColor.PINK));
        s.removeFromHall(StudentColor.PINK,1);
        assertEquals(0,s.getStudentsInHall(StudentColor.PINK));
        s.removeFromHall(StudentColor.GREEN,10);
        assertEquals(0,s.getStudentsInHall(StudentColor.GREEN));
        s.removeFromHall(StudentColor.BLUE,2);
        assertEquals(0,s.getStudentsInHall(StudentColor.BLUE));
        assertEquals(0,s.getStudentsInHall(StudentColor.RED));
        assertEquals(0,s.getStudentsInHall(StudentColor.YELLOW));

    }

}