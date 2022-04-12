package it.polimi.ingsw.model.player;
import it.polimi.ingsw.network.messages.messagesView.SchoolBoardView;
import it.polimi.ingsw.model.enums.GamePreset;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.model.enums.Tower;

import java.util.*;

public class SchoolBoard {
    /**
     * maximum number of towers in the game
     */
    private int maxNumTowers;
    /**
     * current number of towers present on the board
     */
    private int numTowers;
    /**
     * type of towers on the board
     */
    private final Tower tower;
    /**
     * enumMap to keep track of the number of players for each color present on the Hall
     */
    private final EnumMap<StudentColor, Integer> studentsHall;
    /**
     * ArrayList of student present in the entrance
     */
    private final ArrayList<StudentColor> entrance;
    /**
     * enumSet of professors currently controlled by the player that owns the schoolBoard
     */
    private final EnumSet<StudentColor> professors;

    /**
     * Constructor of SchoolBoard, creates an instance and fills with towers
     * @param entranceCapacity max number of students containable in the entrance
     * @param tower type of tower related to the SchoolBard
     * @param numTowers max number of towers
     */
    public SchoolBoard(int entranceCapacity, Tower tower, int numTowers){
        this.maxNumTowers = numTowers;
        this.tower = tower;
        this.numTowers = numTowers;
        this.entrance = new ArrayList<>();

        entrance.addAll(Collections.nCopies(entranceCapacity, null));

        this.studentsHall = new EnumMap<>(StudentColor.class);
        for(StudentColor s : StudentColor.values()){
            studentsHall.put(s,0);
        }
        this.professors = EnumSet.noneOf(StudentColor.class);
    }

    /**
     * @param maxNumTowers new maximum number of towers
     */
    public void setMaxNumTowers(int maxNumTowers){
        this.maxNumTowers = maxNumTowers;
    }

    /**
     * Calculates the max number of students that schoolBoard can contain
     * @return max number of students containable in the entrance
     */
    public int getEntranceCapacity() {
        return entrance.size();
    }

    /**
     * Returns the type of tower related to the schoolBoard
     * @return the tower type
     */
    public Tower getTower(){
        return tower;
    }

    /**
     * Adds towers to the SchoolBoard if it doesn't overflow.
     * @param num number of towers to add
     * @return if thw towers were added successfully.
     */
    public boolean addTowers(int num) {
        if (numTowers + num > maxNumTowers)
            return false;
        numTowers = numTowers + num;
        return true;
    }

    /**
     * Calculates the current number of towers in the SchoolBoard
     * @return current number of towers
     */
    public int getNumTowers() {
        return numTowers;
    }

    /**
     * Removes at most n-towers from the SchoolBoard.
     * @param num number of towers to remove
     * @return if all the towers were removed successfully.
     */
    public boolean removeTowers(int num) {
        numTowers = numTowers - num;
        if(numTowers <= 0){
            numTowers = 0;
            return false;
        }
        return true;
    }

    /**
     * Calculates the professors in SchoolBoard
     * @return a deep copy of professors contained in SchoolBoard
     */
    public EnumSet<StudentColor> getProfessors() {
        EnumSet<StudentColor> ret;
        ret = EnumSet.copyOf(professors);
        return ret;
    }

    /**
     * Adds a new professor in SchoolBoard if professor is not already in school board.
     * @param p is the color of the professor
     * @return if the add was successful.
     */
    public boolean addProfessor(StudentColor p) {
        if(!professors.contains(p)) {
            professors.add(p);
            return true;
        }
        return false;
    }

    /**
     * Removes a professor in SchoolBoard if the school board has the professor to remove.
     * @param p is the color of the professor
     * @return if the remove was successful.
     */
    public boolean removeProfessor(StudentColor p) {
        if (professors.contains(p)){
            professors.remove(p);
            return true;
        }
        return false;
    }

    /**
     * Adds a new Student to the entrance if there is space left.
     * @param s the type of Student to add
     * @return if the student was added correctly
     */
    public boolean addToEntrance(StudentColor s) {
        for(int i = 0; i < entrance.size(); i++) {
            if(entrance.get(i) == null){
                entrance.set(i,s);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a Student in a specific position in the entrance if the index is valid and there is no other student at that index.
     * @param s type of Student to add
     * @param index position in the entrance
     * @return if the student was added correctly.
     */
    public boolean addToEntrance(StudentColor s, int index) {
        if (index < 0 || index >= entrance.size())
            return false;
        if (entrance.get(index) != null)
            return false;
        entrance.set(index, s);
        return true;
    }

    /**
     * Removes a specific Student from entrance if the index is valid.
     * @param index the position of the Student to remove
     * @return if the remove was successful.
     */
    public boolean removeFromEntrance(int index) {
        if (index < 0 || index >= entrance.size())
            return false;
        entrance.set(index, null);
        return true;

    }

    /**
     * Create a deep copy of the entrance
     * @return the copy of the entrance
     */
    public ArrayList<StudentColor> getStudentsInEntrance() {
        return new ArrayList<>(entrance);
    }

    /**
     * Gets the student color in the entrance at a specific index or null if the index is not valid.
     * @param index index to get the student from.
     * @return student color at index.
     */
    public StudentColor getStudentInEntrance(int index) {
        if (index < 0 || index >= entrance.size())
            return null;
        return entrance.get(index);
    }

    /**
     * Calculates number of specific type of Student in the hall
     * @param s type of Student
     * @return number of specific type of Student in the hall
     */
    public int getStudentsInHall(StudentColor s) {
        return studentsHall.get(s);
    }

    /**
     * Moves a specific Student from entrance to hall if the entrance index is valid and there is space in the hall.
     * @param entranceIndex the position of Student in the entrance
     * @return the student was successfully moved to the hall.
     */
    public boolean moveToHall(int entranceIndex) {
        if (entranceIndex >= entrance.size() || entranceIndex < 0)
            return false;
        StudentColor s = getStudentInEntrance(entranceIndex);
        if(s != null) {
            if (addToHall(s)) {
                removeFromEntrance(entranceIndex);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes from Hall a specified number and type of student if there are enough in the hall.
     * @param s type of Student to remove
     * @param num number of students to remove
     * @return if the remove was successful.
     */
    public boolean removeFromHall(StudentColor s, int num) {
        if (studentsHall.get(s) < num)
            return false;
        studentsHall.put(s, studentsHall.get(s) - num);
        return true;
    }

    /**
     * Removes from Hall a specified number and type of student. If there's fewer students than the maxNum removes all of it
     * @param s type of Student to remove
     * @param maxNum max number of students to remove
     */
    public void tryRemoveFromHall(StudentColor s, int maxNum) {
        if (studentsHall.get(s) < maxNum) {
            studentsHall.put(s, 0);
        }
        else {
            studentsHall.put(s, studentsHall.get(s) - maxNum);
        }
    }

    /**
     * Adds to Hall a student if there is space left in hall.
     * @param s type of Student to remove
     * @return if the student was successfully added to the hall.
     */
    public boolean addToHall(StudentColor s) {
        if (studentsHall.get(s) < 10){
            studentsHall.put(s, studentsHall.get(s) + 1);
            return true;
        }
        return false;
    }

    public SchoolBoardView getSchoolBoardView(GamePreset preset){
        SchoolBoardView schoolBoardView = new SchoolBoardView(preset.getEntranceCapacity(), getTower(), preset.getTowersNumber());
        for (int i = 0; i < getEntranceCapacity(); i++) {
            schoolBoardView.addToEntrance(getStudentInEntrance(i), i);
        }
        for (StudentColor s : StudentColor.values()) {
            for (int i = 0; i < getStudentsInHall(s); i++)
                schoolBoardView.addToHall(s);
        }
        schoolBoardView.removeTowers(preset.getTowersNumber() - getNumTowers());
        return schoolBoardView;
    }
}
