package it.polimi.ingsw.server.model.islands;

import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.islands.IslandsManager;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

public class IslandsManagerTest {

    /**
     * the test checks that the merge works correctly. in particular the features that are here tested are:
     * - correct update of the size of the ArrayList of island groups
     * - correct functioning of both checkMergeNext and checkMergePrevious
     * - in the particular case that a merge has already happened (hence the size of the ArrayList IslandGroups
     * is reduced), whether the merge of the first and last IslandGroups still work correctly
     */
    @Test
    void MergeTest(){
        IslandsManager im = new IslandsManager();

        assertEquals(12, im.getNumIslandGroups());

        im.setTower(Tower.GREY, 0);
        im.setTower(Tower.GREY, 1);
        im.setTower(Tower.BLACK, 10);
        im.setTower(Tower.GREY, 11);

        assertFalse(im.checkMergeNext(10));
        assertFalse(im.checkMergePrevious(10));
        // group 10 should not merge with anyone
        assertEquals(12, im.getNumIslandGroups());

        assertTrue(im.checkMergeNext(0));
        assertTrue(im.checkMergePrevious(0));
        // group 0 should merge with both 1 and 11
        assertEquals(3, im.getIslandGroup(im.getNumIslandGroups() - 1).size());
        assertEquals(10, im.getNumIslandGroups());

        // tries the case of a merge between the last and first islandGroups when the num of groups changes
        im.setTower(Tower.WHITE, 0);
        im.setTower(Tower.WHITE, im.getNumIslandGroups() - 1);

        assertTrue(im.checkMergeNext(im.getNumIslandGroups() - 1));
    }

    /**
     * the test check that the method calcInfluence works correctly. the particular cases here tested are:
     * - calcInfluence doesn't consider Students/Towers that the Player doesn't control
     * - if the tower is swapped with another, that the Player controls the result changes
     * - whether the method returns the right value of influence after a merge of islands has happened
     */
    @Test
    void InfluenceTest(){
        IslandsManager im = new IslandsManager();
        EnumSet<StudentColor> professors = EnumSet.of(StudentColor.BLUE, StudentColor.GREEN);

        for (StudentColor s: StudentColor.values()) {
            im.getIslandGroup(0).addStudent(s);
        }

        im.setTower(Tower.GREY, 0);
        assertEquals(Tower.GREY, im.getTower(0));

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
        assertTrue(im.checkMergeNext(0));
        assertFalse(im.checkMergePrevious(0));
        assertEquals(2, im.getIslandGroup(0).size());

        assertEquals(11, im.getNumIslandGroups());

        for (StudentColor s: StudentColor.values()) {
            im.getIslandGroup(0).addStudent(s);
        }

        assertEquals(im.calcInfluence(Tower.BLACK, professors, 0), 6);
        assertEquals(im.calcInfluence(professors, 0), 4);
    }
}
