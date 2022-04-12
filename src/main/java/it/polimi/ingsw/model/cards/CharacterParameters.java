package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.StudentColor;

public class CharacterParameters {

    private final StudentColor studentColor;
    private final Integer index;

    public CharacterParameters(StudentColor studentColor, Integer index) {
        this.studentColor = studentColor;
        this.index = index;
    }

    public CharacterParameters(StudentColor studentColor) {
        this.studentColor = studentColor;
        this.index = null;
    }

    public CharacterParameters(Integer index){
        this.studentColor = null;
        this.index = index;
    }

    public StudentColor getStudentColor() {
        return  studentColor;
    }
    public Integer getIndex() {
        return index;
    }
}
