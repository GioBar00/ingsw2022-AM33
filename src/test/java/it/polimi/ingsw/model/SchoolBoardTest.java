package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import javax.naming.LimitExceededException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchoolBoardTest {


    @Test
    void addTowers() {
        final SchoolBoard s = new SchoolBoard(10,Tower.BLACK,0);
        assertThrows(LimitExceededException.class,() -> s.addTowers(1) );
        SchoolBoard n = new SchoolBoard(10,Tower.BLACK, 10);
        assertEquals(0, n.getNumTowers());
        try {n.addTowers(1);
            assertEquals(1, n.getNumTowers());}
        catch(LimitExceededException ignored){}
        try {n.addTowers(3);
            assertEquals(4, n.getNumTowers());}
        catch(LimitExceededException ignored){}
        try {n.addTowers(10);
            assertEquals(14, n.getNumTowers());}
        catch(LimitExceededException e){
            assertEquals(4,n.getNumTowers());
        }
        SchoolBoard m =new SchoolBoard(300,Tower.BLACK,30);
        assertEquals(0,m.getNumTowers());
        for(int i = 0; i < 30; i++) {
            assertEquals(i, m.getNumTowers());
            try {
                m.addTowers(1);
            }
            catch (LimitExceededException e){System.out.println( "Problem" );}
        }
        assertEquals(30, m.getNumTowers());
        assertThrows(LimitExceededException.class,() -> m.addTowers(1) );
        assertEquals(30, m.getNumTowers());
    }

    @Test
    void removeTowers() {
        SchoolBoard s = new SchoolBoard(300,Tower.WHITE,10);
        try{
            s.addTowers(10);
            try{s.removeTowers(1);
                assertEquals(9,s.getNumTowers());
                try{s.removeTowers(8);
                    assertThrows(LimitExceededException.class,() -> s.removeTowers(2));
                    assertEquals(1,s.getNumTowers());
                }
                catch (LimitExceededException ignored){}

            }
            catch (LimitExceededException ignored){}
        }
        catch (LimitExceededException ignored){}
    }



    @Test
    void getProfessors() {
    }

    @Test
    void addProfessor() {

    }

    @Test
    void removeProfessor() {
    }

    @Test
    void addToEntrance() {
    }

    @Test
    void removeFromEntrance() {
    }

    @Test
    void moveToHall() {
    }

    @Test
    void removeFromHall() {
    }
}