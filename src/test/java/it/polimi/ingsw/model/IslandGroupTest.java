package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class IslandGroupTest {
    @Test
    void CalcInfluenceGeneralTest(){
        IslandGroup ig1 = new IslandGroup();
        ig1.setTower(Tower.BLACK);
        ig1.addStudent(0, StudentColor.GREEN);
        ig1.addStudent(0, StudentColor.BLUE);

        EnumSet<StudentColor> profs = EnumSet.of(StudentColor.BLUE, StudentColor.GREEN);

        assertTrue(ig1.calcInfluence(Tower.BLACK, profs) == 3);
        assertTrue(ig1.calcInfluence(profs) == 2);
        assertFalse(ig1.calcInfluence(Tower.BLACK, profs) == ig1.calcInfluence(profs));
    }

    @Test
    void mergeWith(){
        IslandGroup ig1 = new IslandGroup();
        IslandGroup ig2 = new IslandGroup();

        ig1.mergeWith(ig2);

        assertTrue(ig1.size() == 2);
    }
}