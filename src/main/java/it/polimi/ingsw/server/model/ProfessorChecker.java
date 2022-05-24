package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.StudentColor;

/**
 * An interface for the can check Professors;
 */
public interface ProfessorChecker {

    /**
     * the method checks that che professor of type s is assigned to the correct SchoolBoard and changes its position
     * in case that the current assignment is incorrect
     *
     * @param s: the color of the professor to be checked
     */
    void checkProfessor(StudentColor s);
}
