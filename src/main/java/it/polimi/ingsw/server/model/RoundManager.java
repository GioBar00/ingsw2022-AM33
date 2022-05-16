package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.model.enums.GamePhase;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.EnumSet;

/**
 * This class manages the game rounds and the game phases.
 */
public class RoundManager {
    /**
     * The current game phase.
     */
    private GamePhase gamePhase;
    /**
     * The current round.
     */
    private int roundNum;
    /**
     * If the game is in the last round.
     */
    private boolean lastRound = false;
    /**
     * The maximum number of moves allowed in a turn.
     */
    private final int maxNumMoves;
    /**
     * The number of moves already done in the current turn.
     */
    private int numMoves = 0;
    /**
     * The winning teams.
     */
    private EnumSet<Tower> winners;

    /**
     * Constructor.
     *
     * @param preset the game preset
     */
    RoundManager(GamePreset preset) {
        gamePhase = GamePhase.PLANNING;
        roundNum = 0;
        maxNumMoves = preset.getMaxNumMoves();
        winners = EnumSet.noneOf(Tower.class);

    }

    /**
     * Gets the current game phase.
     *
     * @return the current game phase.
     */
    public GamePhase getGamePhase() {
        return gamePhase;
    }

    /**
     * Gets the current round number.
     *
     * @return the current round number.
     */
    int getRoundNum() {
        return roundNum;
    }

    /**
     * Returns if it is the last round.
     *
     * @return if it is the last round.
     */
    boolean isLastRound() {
        return lastRound;
    }

    /**
     * Declares that it is the last round.
     */
    void setLastRound() {
        lastRound = true;
    }

    /**
     * Sets the winner.
     *
     * @param t tower of the winner
     */
    void setWinner(Tower t) {
        winners = EnumSet.of(t);
    }

    /**
     * Sets the winners.
     *
     * @param ts towers of the winners.
     */
    void setWinners(EnumSet<Tower> ts) {
        winners = ts;
    }

    /**
     * Gets the winners.
     *
     * @return the winners.
     */
    EnumSet<Tower> getWinners() {
        return EnumSet.copyOf(winners);
    }

    /**
     * If not the last round, starts the next round.
     */
    void nextRound() {
        if (!lastRound) {
            gamePhase = GamePhase.PLANNING;
            roundNum++;
        }
    }

    /**
     * Starts the action phase
     */
    public void startActionPhase() {
        gamePhase = GamePhase.MOVE_STUDENTS;
        numMoves = 0;
    }

    /**
     * Adds one move to the ones made by the current player.
     * If the maximum amount of moves were made then "move mother nature" phase starts.
     */
    void addMoves() {
        numMoves++;
        if (numMoves == maxNumMoves) gamePhase = GamePhase.MOVE_MOTHER_NATURE;
    }

    /**
     * Starts the "choose cloud" phase.
     */
    void startChooseCloudPhase() {
        gamePhase = GamePhase.CHOOSE_CLOUD;
    }

    /**
     * Returns if the current player has some moves left if it is the correct phase.
     *
     * @return if the current player can move students.
     */
    boolean canMoveStudents() {
        if (!gamePhase.equals(GamePhase.MOVE_STUDENTS)) return false;
        return numMoves < maxNumMoves;
    }
}
