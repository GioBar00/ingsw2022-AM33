package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.io.Serializable;
import java.util.EnumMap;

/**
 * This class represents a view of the island.
 */
public class IslandView implements Serializable {
    /**
     * tower of the island (if there is none is null)
     */
    private final Tower tower;
    /**
     * students on the island
     */
    private final EnumMap<StudentColor, Integer> students;

    public IslandView(Tower tower, EnumMap<StudentColor, Integer> students) {
        this.tower = tower;
        this.students = students;
    }

    /**
     * @return the tower
     */
    public Tower getTower() {
        return tower;
    }

    /**
     * @return the students
     */
    public EnumMap<StudentColor, Integer> getStudents() {
        return students;
    }
}
