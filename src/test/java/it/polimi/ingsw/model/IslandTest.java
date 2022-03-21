package it.polimi.ingsw.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IslandTest {
    @Test
    @DisplayName("addStudent test")
    void addStudentTest() {
        Island i_test = new Island();
        for (StudentColor s : StudentColor.values()) {
            assertTrue(i_test.students.get(s) == 0);
        }

        for (StudentColor s : StudentColor.values()) {
            i_test.addStudent(s);
        }

        for (StudentColor s : StudentColor.values()) {
            assertFalse(i_test.students.get(s) == 0);
            assertTrue(i_test.students.get(s) == 1);
        }
    }
}