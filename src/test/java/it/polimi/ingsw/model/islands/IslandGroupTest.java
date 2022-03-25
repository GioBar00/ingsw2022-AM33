package it.polimi.ingsw.model.islands;

import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.enums.Tower;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

//TODO
class IslandGroupTest {
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

    @Test
    void mergeWith(){
        IslandGroup ig1 = new IslandGroup();
        IslandGroup ig2 = new IslandGroup();

        ig1.mergeWith(ig2);

        assertEquals(2, ig1.size());
    }
}