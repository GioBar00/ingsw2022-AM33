package it.polimi.ingsw.model.islands;

import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.enums.Tower;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;


public class IslandsManagerTest {

    @Test
    void MergeTest(){
        IslandsManager im = new IslandsManager();

        assertEquals(12, im.getNumIslandGroups());

        im.setTower(Tower.GREY, 0);
        im.setTower(Tower.GREY, 1);
        im.setTower(Tower.BLACK, 10);
        im.setTower(Tower.GREY, 11);

        im.checkMergeNext(10);
        im.checkMergePrevious(10);
        // group 10 should not merge with anyone
        assertEquals(12, im.getNumIslandGroups());

        im.checkMergeNext(0);
        im.checkMergePrevious(0);
        // group 0 should merge with both 1 and 11
        assertEquals(3, im.getIslandGroup(0).size());
        assertEquals(10, im.getNumIslandGroups());
    }

    @Test
    void InfluenceTest(){
        IslandsManager im = new IslandsManager();
        EnumSet<StudentColor> professors = EnumSet.of(StudentColor.BLUE, StudentColor.GREEN);

        for (StudentColor s: StudentColor.values()) {
            im.getIslandGroup(0).addStudent(0, s);
        }

        im.setTower(Tower.GREY, 0);

        // on the IslandGroup there are students of all types and a grey tower, but the student only has two profs
        // and a different tower

        assertEquals(im.calcInfluence(Tower.BLACK, professors, 0), 2);
        assertEquals(im.calcInfluence(Tower.BLACK, professors, 0), im.calcInfluence(professors, 0));

        // if the tower changes to the player's color, influence should get + 1

        im.setTower(Tower.BLACK, 0);

        assertEquals(im.calcInfluence(Tower.BLACK, professors, 0), 3);
        assertEquals(im.calcInfluence(professors, 0), 2);

        // if two island get merged in 1 the IslandManager should return the influence of the whole IslandGroup
        im.setTower(Tower.BLACK, 1);
        im.checkMergeNext(0);
        im.checkMergePrevious(0);
        assertEquals(2, im.getIslandGroup(0).size());

        assertEquals(11, im.getNumIslandGroups());

        for (StudentColor s: StudentColor.values()) {
            im.getIslandGroup(0).addStudent(1, s);
        }

        assertEquals(im.calcInfluence(Tower.BLACK, professors, 0), 6);
        assertEquals(im.calcInfluence(professors, 0), 4);
    }
}
