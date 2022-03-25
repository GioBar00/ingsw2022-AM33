package it.polimi.ingsw.model.player;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.enums.Tower;

import javax.naming.LimitExceededException;
import java.util.*;

public class SchoolBoard {
    private final int maxNumTowers;
    private int numTowers;
    private final Tower tower;
    private final EnumMap<StudentColor, Integer> studentsHall;
    private final ArrayList<StudentColor> entrance;
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
     * Calculates the max number of students that schoolBoard can contain
     * @return max number of students containable in the entrance
     */
    public int getEntranceCapacity() {return entrance.size();}

    public Tower getTower(){
        return tower;
    }

    /**
     * Adds towers to the SchoolBoard
     * @param num number of towers to add
     * @throws LimitExceededException  if we're trying to add towers when there's no space left
     */
    public void addTowers(int num) throws LimitExceededException {
        if(numTowers + num > maxNumTowers){
            throw new LimitExceededException();
        }
        else{
            numTowers = numTowers + num;
        }
    }

    /**
     * Calculates the current number of towers in the SchoolBoard
     * @return current number of towers
     */
    public int getNumTowers(){return numTowers;}

    /**
     * Removes n-towers from SchoolBoard
     * @param num number of towers to remove
     * @throws LimitExceededException  if we're trying to remove more tower than available
     */
    public void removeTowers(int num) throws LimitExceededException{
        if(numTowers - num < 0){
            throw new LimitExceededException();
        }
        numTowers = numTowers - num;
    }

    /**
     * Calculates the professors in SchoolBoard
     * @return a deep copy of professors contained in SchoolBoard
     */
    public EnumSet<StudentColor> getProfessors(){
        EnumSet<StudentColor> ret;
        ret = EnumSet.copyOf(professors);
        return ret;
    }

    /**
     * Adds a new professor in SchoolBoard
     * @param p is the color of the professor
     * @throws Exception if professor is already in SchoolBard
     */
    public void addProfessor(StudentColor p)throws Exception{
        if(!professors.contains(p))
            professors.add(p);
        else {
            throw new Exception();
        }
    }

    /**
     * Removes a professor in SchoolBoard
     * @param p is the color of the professor
     * @throws Exception if SchoolBard doesn't contain the professor
     */
    public void removeProfessor(StudentColor p) throws Exception{
        if(professors.contains(p))
            professors.remove(p);
        else{
            throw new Exception();
        }
    }

    /**
     * Adds a new Student to the entrance
     * @param s the type of Student to add
     * @throws LimitExceededException   if there's no space left
     */
    public void addToEntrance(StudentColor s) throws LimitExceededException{
        for(int i = 0; i < entrance.size(); i ++){
            if(entrance.get(i) == null){
                entrance.set(i,s);
                return;
            }
        }
        throw new LimitExceededException();
    }

    /**
     * Adds a Student in a specific position in the entrance
     * @param s type of Student to add
     * @param index position in the entrance
     * @throws IndexOutOfBoundsException if the index is a no-valid value
     * @throws LimitExceededException if the slot is already taken
     */
    public void addToEntrance(StudentColor s, int index) throws IndexOutOfBoundsException, LimitExceededException {
        if (index < 0 || index >= entrance.size())
            throw new IndexOutOfBoundsException();
        if (entrance.get(index) != null)
            throw new LimitExceededException();
        entrance.set(index, s);
    }

    /**
     * Removes a specific Student from entrance
     * @param index the position of the Student to remove
     * @return the type of Student removed
     * @throws NoSuchElementException if there's no Student in the selected slot
     */
    public StudentColor removeFromEntrance(int index) throws NoSuchElementException{
        StudentColor s;
        if (index < 0 || index >= entrance.size())
            throw new IndexOutOfBoundsException();
        if(entrance.get(index) == null)
            throw new NoSuchElementException();
        s = entrance.get(index);
        entrance.set(index, null);
        return s;

    }

    /**
     * Create a deep copy of the entrance
     * @return the copy of the entrance
     */
    public ArrayList<StudentColor> getStudentsInEntrance(){
        ArrayList<StudentColor> ret = new ArrayList<>();
        for(int i = 0; i < entrance.size(); i++){
            ret.add(i,entrance.get(i));
        }
        return ret;
    }

    /**
     * Calculates number of specific type of Student in the hall
     * @param s type of Student
     * @return number of specific type of Student in the hall
     */
    public int getStudentsInHall(StudentColor s){return studentsHall.get(s);}

    /**
     * Moves a specific Student from entrance to hall
     * @param entranceIndex the position of Student in the entrance
     * @throws LimitExceededException if there's no left space in hall
     */
    public StudentColor moveToHall(int entranceIndex) throws LimitExceededException {
        StudentColor s = removeFromEntrance(entranceIndex);
        if (studentsHall.get(s) < 12){
            studentsHall.replace(s, studentsHall.get(s)+1);
            return s;}
        else throw new LimitExceededException();
    }

    /**
     * Removes from Hall a specified number and type of student. If there's fewer students than the maxNum removes all of it
     * @param s type of Student to remove
     * @param maxNum max number of students to remove
     */
    public void removeFromHall(StudentColor s, int maxNum) {
            if (studentsHall.get(s) < maxNum) {
                studentsHall.replace(s, 0);
            }
            else {
                studentsHall.replace(s, studentsHall.get(s) - maxNum);
            }
    }
}
