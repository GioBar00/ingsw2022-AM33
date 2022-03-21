package it.polimi.ingsw.model;
import javax.naming.LimitExceededException;
import java.util.*;

class SchoolBoard {
    private final int maxNumTowers;
    private int numTowers;
    private final Tower tower;
    private final EnumMap<StudentColor, Integer> studentsHall;
    private final int entranceCapacity;
    private final StudentColor[] entrance;
    private final EnumSet<StudentColor> professors;

    SchoolBoard(int entranceCapacity, Tower tower, int numTowers){
        this.maxNumTowers = numTowers;
        this.tower = tower;
        this.numTowers = 0;
        this.entranceCapacity = entranceCapacity;
        this.entrance = new StudentColor[entranceCapacity];
        this.studentsHall = new EnumMap<>(StudentColor.class);
        this.professors = EnumSet.noneOf(StudentColor.class);
    }

    Tower getTower(){
        return tower;
    }

    void addTowers(int num) throws LimitExceededException {
        if(numTowers + num > maxNumTowers){
            throw new LimitExceededException();
        }
        else{
            numTowers = numTowers + num;
        }
    }

    int getNumTowers(){return numTowers;}

    void removeTowers(int num) throws LimitExceededException{
        if(numTowers - num < 0){
            throw new LimitExceededException();
        }

        numTowers = numTowers - num;

    }

    int getStudNumInHall(StudentColor s){
        return studentsHall.get(s);
    }


    EnumSet<StudentColor> getProfessors(){
        EnumSet<StudentColor> ret;
        ret = EnumSet.copyOf(professors);
        return ret;
    }

    void addProfessor(StudentColor p)throws Exception{
        if(!professors.contains(p))
            professors.add(p);
        else {
            throw new Exception();
        }
    }


    void removeProfessor(StudentColor p) throws Exception{
        if(!professors.contains(p))
            professors.remove(p);
        else{
            throw new Exception();
        }
    }

    void addToEntrance(StudentColor s) throws LimitExceededException{
        int counter = 0;
        for(int i = 0; i < entranceCapacity; i ++){
            if(entrance[i] != null){
                entrance[i] = s;
                break;
            }
            else{counter ++;}
        }
        if(counter == entranceCapacity)
            throw new LimitExceededException();
    }

    StudentColor removeFromEntrance(int index) throws LimitExceededException{
        StudentColor s;
        if(index < 0 && index >= entranceCapacity)
            throw new LimitExceededException();
        else {
            s = entrance[index];
            entrance[index] = null;
            return s;
        }
    }

    void moveToHall(int entranceIndex) throws LimitExceededException {
        StudentColor s = removeFromEntrance(entranceIndex);
        if(studentsHall.containsKey(s)){
            if (studentsHall.get(s) < 12)
                studentsHall.replace(s, studentsHall.get(s)+1);
            else throw new LimitExceededException();
        }
        else{ studentsHall.put(s,1);}
    }
    void removeFromHall(StudentColor s, int maxNum) {
        if (studentsHall.containsKey(s)) {
            if (studentsHall.get(s) < maxNum)
                studentsHall.replace(s, 0);
            else studentsHall.replace(s, studentsHall.get(s) - maxNum);
        }
    }
}
