package it.polimi.ingsw.model;


import it.polimi.ingsw.enums.GamePhase;
import it.polimi.ingsw.enums.GamePreset;
import it.polimi.ingsw.model.player.Player;

class RoundManager {
    private GamePhase gamePhase;
    private int roundNum;
    private boolean lastRound = false;
    private final int maxNumMoves;
    private final int numMoves = 0;
    private Player winner;
    private int moves;

    RoundManager(GamePreset preset) {
        gamePhase = GamePhase.PLANNING;
        roundNum = 0;
        maxNumMoves = preset.getMaxNumMoves();
        winner = null;
        moves = 0;
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
        gamePhase = GamePhase.PLANNING;
        roundNum++;
        moves = 0;
    }
    void startActionPhase(){
        gamePhase = GamePhase.MOVE_STUDENTS;
    }

    void addMoves() throws Exception {
        if(moves < maxNumMoves)
            moves++;
        else throw new Exception();
    }

    void clearMoves() {
        moves = 0;
    }

    boolean canPlay(){
        if(gamePhase.equals(GamePhase.MOVE_STUDENTS))
            return moves < maxNumMoves;
        return false;
    }
}
