package it.polimi.ingsw.server.model.enums;

/**
 * Phases of when the game has started.
 */
public enum GamePhase {
    /**
     * Planning phase
     */
    PLANNING,
    /**
     * Phase for moving the students to the hall or to the islands
     */
    MOVE_STUDENTS,
    /**
     * Phase for moving mother nature
     */
    MOVE_MOTHER_NATURE,
    /**
     * Phase for choosing a cloud
     */
    CHOOSE_CLOUD
}
