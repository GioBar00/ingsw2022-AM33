package it.polimi.ingsw.model;


import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.GamePreset;
import it.polimi.ingsw.model.player.Player;

class RoundManager {
    private GamePhase gamePhase;
    private int roundNum;
    private boolean lastRound = false;
    private final int maxNumMoves;
    private int numMoves = 0;
    private Player winner;

    RoundManager(GamePreset preset) {
        gamePhase = GamePhase.PLANNING;
        roundNum = 0;
        maxNumMoves = preset.getMaxNumMoves();
        winner = null;

    }

    GamePhase getGamePhase() {
        return gamePhase;
    }

    int getRoundNum() {
        return roundNum;
    }

    boolean isLastRound() {
        return lastRound;
    }

    void setLastRound(){
        lastRound = true;
    }

    void setWinner(Player p){
        winner = p;
    }

    Player getWinner() { return winner; }

    void nextRound() {
        if(!lastRound) {
            gamePhase = GamePhase.PLANNING;
            roundNum++;
            clearMoves();
        }
    }

    void startActionPhase(){
        gamePhase = GamePhase.MOVE_STUDENTS;
    }

    boolean addMoves() {
        if(numMoves < maxNumMoves) {
            numMoves++;
            return true;
        }
        return false;
    }

    void clearMoves() {
        numMoves = 0;
    }

    boolean canPlay(){
        if(gamePhase.equals(GamePhase.MOVE_STUDENTS)){
            if(numMoves >= maxNumMoves)
                gamePhase = GamePhase.MOVE_MOTHER_NATURE;
            return numMoves < maxNumMoves;
        }
        return false;
    }
}
