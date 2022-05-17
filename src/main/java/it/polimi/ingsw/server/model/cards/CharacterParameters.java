package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.StudentColor;

public class CharacterParameters {

    /**
     * color of the student on the card
     */
    private final StudentColor studentColor;
    /**
     * index of the student on the card
     */
    private final Integer index;

    /**
     * constructor for a CharacterParameters with both index and studentColor
     * @param studentColor
     * @param index
     */
    public CharacterParameters(StudentColor studentColor, Integer index) {
        this.studentColor = studentColor;
        this.index = index;
    }

    /**
     * constructor for a CharacterParameters without the index
     * @param studentColor
     */
    public CharacterParameters(StudentColor studentColor) {
        this.studentColor = studentColor;
        this.index = null;
    }

    /**
     * constructor a CharacterParameters without the student color
     * @param index
     */
    public CharacterParameters(Integer index){
        this.studentColor = null;
        this.index = index;
    }

    /**
     * @return the student color
     */
    public StudentColor getStudentColor() {
        return  studentColor;
    }

    /**
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }
}
