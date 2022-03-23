package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;

class Cloud {
    private final ArrayList<StudentColor> students;

    Cloud(int capacity) {
        this.students = new ArrayList<>(capacity);
        students.addAll(Collections.nCopies(capacity, null));
    }

    /**
     * method to eliminate all the students on the cloud
     */
    private void clearStudents() {
        for (int i = 0; i < students.size(); i++) {
            students.set(i, null);
        }
    }

    /**
     * method to access all the students on the cloud
     * @return the ArrayList of students
     */
    ArrayList<StudentColor> getStudents() {
        return students;
    }

    /**
     * method to add some students on the cloud
     * @param s type of student to be added
     * @param index number of student of type s to add to the cloud
     * @throws IndexOutOfBoundsException if the method tries to add more students than allowed
     */
    void addStudent(StudentColor s, int index) throws IndexOutOfBoundsException {
        students.set(index, s);
    }

    /**
     * method to remove students from the cloud
     * @return the ArrayList of students just removed form the cloud
     */
    ArrayList<StudentColor> popStudents(){
        ArrayList<StudentColor> tmp = new ArrayList<>();
        for(int i = 0; i < students.size(); i++){
            tmp.add(i, students.get(i));
        }
        clearStudents();
        return tmp;
    }
}
