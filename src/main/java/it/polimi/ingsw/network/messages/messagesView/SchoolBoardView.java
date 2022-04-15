package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;

public class SchoolBoardView {
    private int numTowers;
    private final Tower tower;
    private final EnumMap<StudentColor, Integer> studentsHall;
    private final ArrayList<StudentColor> entrance;

    public SchoolBoardView(int entranceCapacity, Tower tower, int numTowers){
        this.tower = tower;
        this.numTowers = numTowers;
        this.entrance = new ArrayList<>();

        entrance.addAll(Collections.nCopies(entranceCapacity, null));

        this.studentsHall = new EnumMap<>(StudentColor.class);
        for(StudentColor s : StudentColor.values()){
            studentsHall.put(s,0);
        }
        EnumSet<StudentColor> professors = EnumSet.noneOf(StudentColor.class);
    }

    public boolean addToEntrance(StudentColor s, int index) {
        if (index < 0 || index >= entrance.size())
            return false;
        if (entrance.get(index) != null)
            return false;
        entrance.set(index, s);
        return true;
    }

    public boolean addToHall(StudentColor s) {
        if (studentsHall.get(s) < 10){
            studentsHall.put(s, studentsHall.get(s) + 1);
            return true;
        }
        return false;
    }

    public boolean removeTowers(int num) {
        numTowers = numTowers - num;
        if(numTowers <= 0){
            numTowers = 0;
            return false;
        }
        return true;
    }
}
