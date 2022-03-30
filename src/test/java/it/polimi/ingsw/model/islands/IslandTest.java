package it.polimi.ingsw.model.islands;

import it.polimi.ingsw.enums.StudentColor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//TODO JavaDOC
class IslandTest {
    @Test
    @DisplayName("addStudent test")
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
    }
}