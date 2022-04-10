package it.polimi.ingsw.model;


import it.polimi.ingsw.model.enums.GamePhase;
import it.polimi.ingsw.model.enums.GamePreset;
import it.polimi.ingsw.model.enums.Tower;

import java.util.EnumSet;

class RoundManager {
    private GamePhase gamePhase;
    private int roundNum;
    private boolean lastRound = false;
    private final int maxNumMoves;
    private int numMoves = 0;
    private EnumSet<Tower> winners;

    RoundManager(GamePreset preset) {
        gamePhase = GamePhase.PLANNING;
        roundNum = 0;
        maxNumMoves = preset.getMaxNumMoves();
        winners = EnumSet.noneOf(Tower.class);

    }

    /**
     * Gets the current game phase.
     * @return the current game phase.
     */
    GamePhase getGamePhase() {
        return gamePhase;
    }

    /**
     * Gets the current round number.
     * @return the current round number.
     */
    int getRoundNum() {
        return roundNum;
    }

    /**
     * Returns if it is the last round.
     * @return if it is the last round.
     */
    boolean isLastRound() {
        return lastRound;
    }

    /**
     * Declares that it is the last round.
     */
    void setLastRound(){
        lastRound = true;
    }

    /**
     * Sets the winner.
     * @param t tower of the winner
     */
    void setWinner(Tower t) {
        winners = EnumSet.of(t);
    }

    /**
     * Sets the winners.
     * @param ts towers of the winners.
     */
    void setWinners(EnumSet<Tower> ts) {
        winners = ts;
    }

    /**
     * Gets the winners.
     * @return the winners.
     */
    EnumSet<Tower> getWinners() {
        return EnumSet.copyOf(winners);
    }

    /**
     * If not the last round, starts the next round.
     */
    void nextRound() {
        if(!lastRound) {
            gamePhase = GamePhase.PLANNING;
            roundNum++;
        }
    }

    /**
     * Starts the action phase
     */
    void startActionPhase(){
        gamePhase = GamePhase.MOVE_STUDENTS;
        numMoves = 0;
    }

    /**
     * Adds one move to the ones made by the current player.
     * If the maximum amount of moves were made then "move mother nature" phase starts.
     */
    void addMoves() {
        numMoves++;
        if(numMoves == maxNumMoves)
            gamePhase = GamePhase.MOVE_MOTHER_NATURE;
    }

    /**
     * Starts the "choose cloud" phase.
     */
    void startChooseCloudPhase() {
        gamePhase = GamePhase.CHOOSE_CLOUD;
    }

    /**
     * Returns if the current player has some moves left if it is the correct phase.
     * @return if the current player can move students.
     */
    boolean canMoveStudents(){
        if(!gamePhase.equals(GamePhase.MOVE_STUDENTS))
            return false;
        return numMoves < maxNumMoves;
    }
}
