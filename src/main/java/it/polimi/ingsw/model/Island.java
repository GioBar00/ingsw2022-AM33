package it.polimi.ingsw.model;

import java.util.EnumMap;

public class Island {
    private Tower tower;
    EnumMap<StudentColor, Integer> students;

    public Island(){
        students = new EnumMap<>(StudentColor.class);
        for (StudentColor s : StudentColor.values()) {
            students.put(s, 0);
        }
        this.tower = null;
    }


    Tower getTower(){
        return tower;
    }

    void setTower(Tower tower){
        this.tower = tower;
    }

    /* method that adds students on an island */
    void addStudent(StudentColor s){
        students.replace(s, students.get(s) + 1);
    }

    /* method that returns the number of all the students present on the island, regardless of type of Student*/
    int getNumStudents(){
        return students.get(StudentColor.GREEN) + students.get(StudentColor.RED) + students.get(StudentColor.YELLOW) + students.get(StudentColor.PINK) + students.get(StudentColor.BLUE);
    }

    /* method that returns the number of students of a specific type*/
    int getNumStudents(StudentColor s){
        return students.get(s);
    }
}
