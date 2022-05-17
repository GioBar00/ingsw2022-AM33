package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;

public class SchoolBoardView implements Serializable {
    /**
     * number of towers still on the board
     */
    private final int numTowers;
    /**
     * type of tower
     */
    private final Tower tower;
    /**
     * students in the hall
     */
    private final EnumMap<StudentColor, Integer> studentsHall;
    /**
     * students in the entrance
     */
    private final ArrayList<StudentColor> entrance;
    /**
     * professors on the board
     */
    private final EnumSet<StudentColor> professors;

    public SchoolBoardView(int numTowers, Tower tower, EnumMap<StudentColor, Integer> studentsHall, ArrayList<StudentColor> entrance, EnumSet<StudentColor> professors) {
        this.numTowers = numTowers;
        this.tower = tower;
        this.studentsHall = studentsHall;
        this.entrance = entrance;
        this.professors = professors;
    }

    /**
     * @return the number of towers
     */
    public int getNumTowers() {
        return numTowers;
    }

    /**
     * @return the type of tower
     */
    public Tower getTower() {
        return tower;
    }

    /**
     * @return the students in the hall
     */
    public EnumMap<StudentColor, Integer> getStudentsHall() {
        return studentsHall;
    }

    /**
     * @return the students in the entrance
     */
    public ArrayList<StudentColor> getEntrance() {
        return entrance;
    }

    /**
     * @return the professors
     */
    public EnumSet<StudentColor> getProfessors() {
        return professors;
    }
}
