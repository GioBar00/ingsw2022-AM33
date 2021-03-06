package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.StudentColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that represents a cloud of the game
 */
public class Cloud {

    /**
     * students present on the cloud
     */
    private final ArrayList<StudentColor> students;

    /**
     * Public constructor of a Cloud
     *
     * @param capacity the number of the students that the cloud can hold
     */
    Cloud(int capacity) {
        this.students = new ArrayList<>(capacity);
        students.addAll(Collections.nCopies(capacity, null));
    }

    /**
     * method to eliminate all the students on the cloud
     */
    private void clearStudents() {
        Collections.fill(students, null);
    }

    /**
     * method to access all the students on the cloud
     *
     * @return the List of students
     */
    List<StudentColor> getStudents() {
        LinkedList<StudentColor> tmp = new LinkedList<>();
        for (StudentColor s : students) {
            if (s != null) tmp.add(s);
        }
        return tmp;
    }

    /**
     * method to add some students on the cloud
     *
     * @param s     type of student to be added
     * @param index number of student of type s to add to the cloud
     */
    void addStudent(StudentColor s, int index) {
        students.set(index, s);
    }

    /**
     * method to remove students from the cloud
     *
     * @return the List of students just removed form the cloud
     */
    List<StudentColor> popStudents() {
        List<StudentColor> tmp = getStudents();
        clearStudents();
        return tmp;
    }

    /**
     * @return the students to create the view
     */
    public ArrayList<StudentColor> getStudentsForView() {
        return students;
    }
}
