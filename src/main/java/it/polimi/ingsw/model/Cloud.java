package it.polimi.ingsw.model;

public class Cloud {
    private final StudentColor[] students;

    public Cloud(int capacity) {
        this.students = new StudentColor[capacity];
    }


    StudentColor[] getStudents() {
        return students;
    }

    void addStudent(int index, StudentColor s) throws IndexOutOfBoundsException {
        students[index] = s;
    }

    StudentColor[] popStudents(){
        StudentColor[] temp = new StudentColor[students.length];

        for(int i = 0; i < students.length; i++){
            temp[i] = students[i];
            students[i] = null;
        }

        return temp;
    }
}
