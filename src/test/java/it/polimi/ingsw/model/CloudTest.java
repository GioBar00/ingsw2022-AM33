package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.StudentColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CloudTest {
    @Test
    void get_add_pop_Students(){
        Cloud testCloud = new Cloud(3);

        for (int i = 0; i < 3; i++) {
            assertNull(testCloud.popStudents().get(i));
        }

        testCloud.addStudent(StudentColor.PINK, 0);
        testCloud.addStudent(StudentColor.RED, 1);
        testCloud.addStudent(StudentColor.YELLOW, 2);

        assertSame(testCloud.getStudents().get(0), StudentColor.PINK);
        assertSame(testCloud.getStudents().get(1), StudentColor.RED);
        assertSame(testCloud.getStudents().get(2), StudentColor.YELLOW);

        assertThrows(IndexOutOfBoundsException.class, ()-> testCloud.addStudent(StudentColor.BLUE, 4));

        testCloud.popStudents();

        for (int i = 0; i < 3; i++) {
            assertNull(testCloud.popStudents().get(i));
        }
    }
}