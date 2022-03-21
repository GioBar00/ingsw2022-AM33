package it.polimi.ingsw.model;
import javax.naming.LimitExceededException;
import java.util.*;

class SchoolBoard {
    private final int maxNumTowers;
    private int numTowers;
    private final Tower tower;
    private final EnumMap<StudentColor, Integer> studentsHall;
    private final ArrayList<StudentColor> entrance;
    private final EnumSet<StudentColor> professors;

    SchoolBoard(int entranceCapacity, Tower tower, int numTowers){
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

    int getEntranceCapacity() {return entrance.size();}

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
        if(professors.contains(p))
            professors.remove(p);
        else{
            throw new Exception();
        }
    }

    void addToEntrance(StudentColor s) throws LimitExceededException{
        for(int i = 0; i < entrance.size(); i ++){
            if(entrance.get(i) == null){
                entrance.set(i,s);
                return;
            }
        }
        throw new LimitExceededException();
    }

    void addToEntrance(StudentColor s, int index) throws IndexOutOfBoundsException, LimitExceededException {
        if (index < 0 || index >= entrance.size())
            throw new IndexOutOfBoundsException();
        if (entrance.get(index) != null)
            throw new LimitExceededException();
        entrance.set(index, s);
    }

    StudentColor removeFromEntrance(int index) throws NoSuchElementException{
        StudentColor s;
        if(entrance.get(index) == null)
            throw new NoSuchElementException();
        s = entrance.get(index);
        entrance.set(index, null);
        return s;

    }
    ArrayList<StudentColor> getStudentsInEntrance(){
        ArrayList<StudentColor> ret = new ArrayList<>();
        for(int i = 0; i < entrance.size(); i++){
            ret.add(i,entrance.get(i));
        }
        return ret;
    }

    int getStudentsInHall(StudentColor s){return studentsHall.get(s);}

    void moveToHall(int entranceIndex) throws LimitExceededException {
        StudentColor s = removeFromEntrance(entranceIndex);
        if (studentsHall.get(s) < 12)
            studentsHall.replace(s, studentsHall.get(s)+1);
        else throw new LimitExceededException();

    }
    void removeFromHall(StudentColor s, int maxNum) {
        if (studentsHall.containsKey(s)) {
            if (studentsHall.get(s) < maxNum)
                studentsHall.replace(s, 0);
            else studentsHall.replace(s, studentsHall.get(s) - maxNum);
        }
    }
}
