package it.polimi.ingsw.model.islands;

import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.enums.Tower;

import java.util.EnumMap;

public class Island {
    private Tower tower;
    private final EnumMap<StudentColor, Integer> students;

    public Island(){
        students = new EnumMap<>(StudentColor.class);
        for (StudentColor s : StudentColor.values()) {
            students.put(s, 0);
        }
        this.tower = null;
    }

    /**
     * method to access the tower of the island
     * @return the tower
     */
    Tower getTower(){
        return tower;
    }

    /**
     * method to set the tower of the island
     * @param tower: new tower to be set
     */
    void setTower(Tower tower){
        this.tower = tower;
    }

    /**
     * adds one student of a specific type on the Island
     * @param s type of the student to be added
     */
    void addStudent(StudentColor s){
        students.replace(s, students.get(s) + 1);
    }

    /**
     * calculates the total number of students on the island, regardless of color
     * @return total number of students
     */
    public int getNumStudents(){
        return students.get(StudentColor.GREEN) + students.get(StudentColor.RED) + students.get(StudentColor.YELLOW) + students.get(StudentColor.PINK) + students.get(StudentColor.BLUE);
    }

    /**
     * calculates the number if student of a specific type present on the island
     * @param s type of the student
     * @return number of student of type s
     */
    int getNumStudents(StudentColor s){
        return students.get(s);
    }
}
