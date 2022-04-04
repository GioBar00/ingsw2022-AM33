package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.StudentColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudTest {

    /**
     * the test checks that the cloud is created empty, adds students to the cloud, check that
     * each student is added to the right position, that the method throws the right exception, proceeds to
     * pop all students and check that finally the islands is empty
     */
    @Test
    void get_add_pop_Students(){
        Cloud testCloud = new Cloud(3);


        assertTrue(testCloud.popStudents().isEmpty());
        testCloud.addStudent(StudentColor.PINK, 0);
        testCloud.addStudent(StudentColor.RED, 1);
        testCloud.addStudent(StudentColor.YELLOW, 2);

        assertSame(testCloud.getStudents().get(0), StudentColor.PINK);
        assertSame(testCloud.getStudents().get(1), StudentColor.RED);
        assertSame(testCloud.getStudents().get(2), StudentColor.YELLOW);


        testCloud.popStudents();

        assertTrue(testCloud.popStudents().isEmpty());
    }
}