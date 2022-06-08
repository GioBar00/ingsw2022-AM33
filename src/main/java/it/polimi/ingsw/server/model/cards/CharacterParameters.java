package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.StudentColor;

/**
 * This class converts a set of StudentColor and index to a {@link CharacterParameters} object.
 */
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
     * @param studentColor color of the student.
     * @param index index of student, island or entrance.
     */
    public CharacterParameters(StudentColor studentColor, Integer index) {
        this.studentColor = studentColor;
        this.index = index;
    }

    /**
     * constructor for a CharacterParameters without the index
     * @param studentColor color of the student.
     */
    public CharacterParameters(StudentColor studentColor) {
        this.studentColor = studentColor;
        this.index = null;
    }

    /**
     * constructor a CharacterParameters without the student color
     * @param index index of student, island or entrance.
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
