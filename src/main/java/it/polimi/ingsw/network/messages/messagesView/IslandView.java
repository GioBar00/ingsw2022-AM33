package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.EnumMap;

public class IslandView {
    private final Tower tower;
    private final EnumMap<StudentColor, Integer> students;

    public IslandView(Tower tower, EnumMap<StudentColor, Integer> students) {
        this.tower = tower;
        this.students = students;
    }

    public Tower getTower() {
        return tower;
    }

    public EnumMap<StudentColor, Integer> getStudents() {
        return students;
    }
}
