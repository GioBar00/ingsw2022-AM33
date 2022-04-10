package it.polimi.ingsw.model.islands;

import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.model.enums.Tower;
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
        ig1.addStudent(StudentColor.GREEN);
        ig1.addStudent(StudentColor.BLUE);

        EnumSet<StudentColor> profs = EnumSet.of(StudentColor.BLUE, StudentColor.GREEN);

        assertEquals(3, ig1.calcInfluence(Tower.BLACK, profs));
        assertEquals(2, ig1.calcInfluence(profs));
        assertNotEquals(ig1.calcInfluence(Tower.BLACK, profs), ig1.calcInfluence(profs));
    }

    /**
     * the test checks that, whenever a student is added to an IslandGroup, it's positioned on the island that currently
     * contains the least amount of students
     */
    @Test
    void getIslandWithMinStudents(){
        IslandGroup ig1 = new IslandGroup();
        IslandGroup ig2 = new IslandGroup();
        IslandGroup ig3 = new IslandGroup();

        assertEquals(1, ig1.size());
        assertEquals(1, ig2.size());
        assertEquals(1, ig3.size());

        ig1.setTower(Tower.WHITE);
        for(StudentColor s: StudentColor.values()){
            ig1.addStudent(s);
        }

        ig2.setTower(Tower.WHITE);
        ig2.addStudent(StudentColor.BLUE);
        ig2.addStudent(StudentColor.GREEN);

        ig3.setTower(Tower.WHITE);

        ig1.mergeWith(ig2);
        ig1.mergeWith(ig3);

        assertEquals(ig1.size(), 3);

        ig1.addStudent(StudentColor.RED);

        assertEquals(1, ig1.getIslands().get(2).getNumStudents(StudentColor.RED));

        ig1.addStudent(StudentColor.RED);

        assertEquals(2, ig1.getIslands().get(2).getNumStudents(StudentColor.RED));

        ig1.addStudent(StudentColor.PINK);
        assertEquals(1, ig1.getIslands().get(1).getNumStudents(StudentColor.PINK));
    }

    /**
     * checks that the method mergeWith() works in the correct way: the size of the IslandGroup is correctly incremented
     * and the objects now on the merged Island are the union of the objects on the original Islands
     */
    @Test
    void mergeWith(){
        IslandGroup ig1 = new IslandGroup();
        IslandGroup ig2 = new IslandGroup();

        ig1.addStudent(StudentColor.BLUE);
        ig1.addStudent(StudentColor.RED);
        ig1.addStudent(StudentColor.PINK);

        ig2.addStudent(StudentColor.YELLOW);
        ig2.addStudent(StudentColor.GREEN);

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