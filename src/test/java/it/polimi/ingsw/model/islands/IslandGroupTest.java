package it.polimi.ingsw.model.islands;

import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.enums.Tower;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class IslandGroupTest {

    /**
     * check that the computation of the influence is done correctly
     */
    @Test
    void CalcInfluenceGeneralTest(){
        IslandGroup ig1 = new IslandGroup();
        ig1.setTower(Tower.BLACK);
        ig1.addStudent(0, StudentColor.GREEN);
        ig1.addStudent(0, StudentColor.BLUE);

        EnumSet<StudentColor> profs = EnumSet.of(StudentColor.BLUE, StudentColor.GREEN);

        assertEquals(3, ig1.calcInfluence(Tower.BLACK, profs));
        assertEquals(2, ig1.calcInfluence(profs));
        assertNotEquals(ig1.calcInfluence(Tower.BLACK, profs), ig1.calcInfluence(profs));
    }

    /**
     * checks that the method mergeWith() works in the correct way: the size of the IslandGroup is correctly incremented
     * and the objects now on the merged Island are the union of the objects on the original Islands
     */
    @Test
    void mergeWith(){
        IslandGroup ig1 = new IslandGroup();
        IslandGroup ig2 = new IslandGroup();

        ig1.addStudent(0, StudentColor.BLUE);
        ig1.addStudent(0, StudentColor.RED);
        ig1.addStudent(0, StudentColor.PINK);

        ig2.addStudent(0, StudentColor.YELLOW);
        ig2.addStudent(0, StudentColor.GREEN);

        ig1.mergeWith(ig2);

        assertEquals(2, ig1.size());
        assertEquals(1, ig1.getIslands().get(0).students.get(StudentColor.BLUE));
        assertEquals(1, ig1.getIslands().get(0).students.get(StudentColor.RED));
        assertEquals(1, ig1.getIslands().get(0).students.get(StudentColor.PINK));
        assertEquals(1, ig1.getIslands().get(1).students.get(StudentColor.YELLOW));
        assertEquals(1, ig1.getIslands().get(1).students.get(StudentColor.GREEN));
    }

    /**
     * the test checks that the attribute isBlocked can be set correctly
     */
    @Test
    void blockedTest(){
        IslandGroup ig = new IslandGroup();

        assertFalse(ig.isBlocked());
        ig.setBlocked(true);
        assertTrue(ig.isBlocked());
        ig.setBlocked(false);
        assertFalse(ig.isBlocked());
    }
}