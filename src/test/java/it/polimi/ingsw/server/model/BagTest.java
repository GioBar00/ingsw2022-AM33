package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.StudentColor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Bag} class.
 */
class BagTest {

    /**
     * the tests check the addictions to the bag are done correctly and proceeds to check if the
     * method popRandomStudent() works correctly (extracting the right number of students and
     * throwing the right exception
     */
    @Test
    void popRandomStudentTest() {
        Bag testBag = new Bag();
        StudentColor eliminated;
        LinkedList<StudentColor> studentToAdd;
        studentToAdd = new LinkedList<>(Arrays.asList(StudentColor.values()));

        testBag.addStudents(studentToAdd);

        assertEquals(5, testBag.students.size());

        eliminated = testBag.popRandomStudent();

        assertNotEquals(5, testBag.students.size());
        assertEquals(4, testBag.students.size());
        assertFalse(testBag.students.contains(eliminated));

        for (int i = 0; i < 4; i++) testBag.popRandomStudent();

        assertNull(testBag.popRandomStudent());
    }

    /**
     * This method checks the isEmpty method implemented by the Bag class.
     */
    @Test
    void checkEmptiness() {
        Bag testBag = new Bag();
        LinkedList<StudentColor> studentToAdd;
        studentToAdd = new LinkedList<>(Arrays.asList(StudentColor.values()));

        assertTrue(testBag.isEmpty());
        testBag.addStudents(studentToAdd);
        assertFalse(testBag.isEmpty());

        testBag.empty();
        assertTrue(testBag.isEmpty());
    }
}