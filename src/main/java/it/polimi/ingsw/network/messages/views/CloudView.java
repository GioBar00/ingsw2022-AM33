package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.enums.StudentColor;

import java.util.ArrayList;

public class CloudView {

    /**
     * students on the cloud
     */
    private final ArrayList<StudentColor> students;

    /**
     * constructor
     * @param students in the cloud
     */
    public CloudView(ArrayList<StudentColor> students) {
        this.students = students;
    }

    /**
     * @return students on the cloud
     */
    public ArrayList<StudentColor> getStudents() {
        return students;
    }
}
