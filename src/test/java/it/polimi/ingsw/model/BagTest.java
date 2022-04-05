package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.StudentColor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class BagTest {

    /**
     * the tests check the addictions to the bag are done correctly and proceeds to check if the
     * method popRandomStudent() works correctly (extracting the right number of students and
     * throwing the right exception
     */
    @Test
    void popRandomStudentTest(){
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

        for(int i = 0; i < 4; i++)  testBag.popRandomStudent();

        assertNull(testBag.popRandomStudent());
    }

}