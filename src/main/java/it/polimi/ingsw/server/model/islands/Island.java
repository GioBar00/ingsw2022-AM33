package it.polimi.ingsw.server.model.islands;

import it.polimi.ingsw.network.messages.messagesView.IslandView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.EnumMap;

public class Island {
    /**
     * tower positioned on the island (while there is none is null)
     */
    private Tower tower;
    /**
     * enumMap to keep trace of the number of students of each type
     */
    final EnumMap<StudentColor, Integer> students;

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
        students.put(s, students.get(s) + 1);
    }

    /**
     * calculates the total number of students on the island, regardless of color
     * @return total number of students
     */
    public int getNumStudents() {
        int num = 0;
        for (StudentColor s: StudentColor.values())
            num += students.get(s);
        return num;
    }

    /**
     * calculates the number if student of a specific type present on the island
     * @param s type of the student
     * @return number of student of type s
     */
    public int getNumStudents(StudentColor s){
        return students.get(s);
    }

    /**
     * @return the current islandView
     */
    public IslandView getIslandView (){
        return new IslandView(tower, students);
    }
}
