package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.EnumMap;

public class IslandView {
    private Tower tower;
    private final EnumMap<StudentColor, Integer> students;

    public IslandView() {
        students = new EnumMap<>(StudentColor.class);
        for (StudentColor s : StudentColor.values()) {
            students.put(s, 0);
        }
        this.tower = null;
    }

    public Tower getTower() {
        return tower;
    }

    public void setTower(Tower tower){
        this.tower = tower;
    }

    public void addStudent(StudentColor s){
        students.put(s, students.get(s) + 1);
    }
}
