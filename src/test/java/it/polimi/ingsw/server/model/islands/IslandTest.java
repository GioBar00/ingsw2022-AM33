package it.polimi.ingsw.server.model.islands;

import it.polimi.ingsw.server.model.enums.StudentColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Island} class.
 */
class IslandTest {
    /**
     * checks that when the island is created the number of student present is 0 for every color, then adds
     * students to the island and checks that they are added in the correct amount
     */
    @Test
    void addStudentTest() {
        Island i_test = new Island();
        for (StudentColor s : StudentColor.values()) {
            assertEquals(0, i_test.getNumStudents(s));
        }

        for (StudentColor s : StudentColor.values()) {
            i_test.addStudent(s);
        }

        for (StudentColor s : StudentColor.values()) {
            assertNotEquals(0, i_test.getNumStudents(s));
            assertEquals(1, i_test.getNumStudents(s));
        }

        assertEquals(5, i_test.getNumStudents());
    }
}