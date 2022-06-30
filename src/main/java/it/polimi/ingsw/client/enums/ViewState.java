package it.polimi.ingsw.client.enums;

/**
 * This enum represents the possible states of the UI.
 */
public enum ViewState {
    /**
     * The ui is being set up
     */
    SETUP,
    /**
     * The ui is showing the menu to choose between the wizards
     */
    CHOOSE_WIZARD,
    /**
     * The ui is showing the menu to choose between the teams
     */
    CHOOSE_TEAM,
    /**
     * The ui is showing the game screen
     */
    PLAYING,

    /**
     * The ui is in waiting
     */
    WAITING,
    /**
     * The ui is showing the end-game screen
     */
    END_GAME,

    /**
     * The ui is being reset
     */
    RESET

}
