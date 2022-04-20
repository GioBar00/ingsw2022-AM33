package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;

public class SchoolBoardView {
    private final int numTowers;
    private final Tower tower;
    private final EnumMap<StudentColor, Integer> studentsHall;
    private final ArrayList<StudentColor> entrance;
    private final EnumSet<StudentColor> professors;

    public SchoolBoardView(int numTowers, Tower tower, EnumMap<StudentColor, Integer> studentsHall, ArrayList<StudentColor> entrance, EnumSet<StudentColor> professors) {
        this.numTowers = numTowers;
        this.tower = tower;
        this.studentsHall = studentsHall;
        this.entrance = entrance;
        this.professors = professors;
    }

    public int getNumTowers() {
        return numTowers;
    }

    public Tower getTower() {
        return tower;
    }

    public EnumMap<StudentColor, Integer> getStudentsHall() {
        return studentsHall;
    }

    public ArrayList<StudentColor> getEntrance() {
        return entrance;
    }

    public EnumSet<StudentColor> getProfessors() {
        return professors;
    }
}
