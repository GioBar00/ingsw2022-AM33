package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.StudentColor;

public class CharacterParameters {
    private final StudentColor studentColor;
    private final int index;

    public CharacterParameters(StudentColor studentColor, int index) {
        this.studentColor = studentColor;
        this.index = index;
    }

    public CharacterParameters(StudentColor studentColor) {
        this.studentColor = studentColor;
        this.index = -1;
    }

    public CharacterParameters(int index){
        this.studentColor = null;
        this.index = index;
    }

    public StudentColor getStudentColor() {
        return  studentColor;
    }
    public int getIndex() {
        return index;
    }
}
