package it.polimi.ingsw.server.model.enums;

/**
 * State of the current Game, from creation to ending
 */
public enum GameState {
    /**
     * Uninitialized state: the game is not yet set up
     */
    UNINITIALIZED,
    /**
     * Started state: the game has started
     */
    STARTED,
    /**
     * Ended state: the game is over
     */
    ENDED
}
