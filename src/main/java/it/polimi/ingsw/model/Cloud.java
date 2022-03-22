package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;

class Cloud {
    private final ArrayList<StudentColor> students;

    Cloud(int capacity) {
        this.students = new ArrayList<>(capacity);
        students.addAll(Collections.nCopies(capacity, null));
    }

    private void clearStudents() {
        for (int i = 0; i < students.size(); i++) {
            students.set(i, null);
        }
    }

    ArrayList<StudentColor> getStudents() {
        return students;
    }

    void addStudent(int index, StudentColor s) throws IndexOutOfBoundsException {
        students.set(index, s);
    }

    ArrayList<StudentColor> popStudents(){
        ArrayList<StudentColor> tmp = new ArrayList<>();
        for(int i = 0; i < students.size(); i++){
            tmp.add(i, students.get(i));
        }
        clearStudents();
        return tmp;
    }
}
