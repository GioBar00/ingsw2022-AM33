package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class BagTest {
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

        for(int i = 0; i < 4; i++) eliminated = testBag.popRandomStudent();

        assertThrows(NoSuchElementException.class, testBag::popRandomStudent);
    }

}