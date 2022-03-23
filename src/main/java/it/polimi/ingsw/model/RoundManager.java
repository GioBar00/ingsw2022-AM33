package it.polimi.ingsw.model;


import it.polimi.ingsw.enums.GamePhase;

class RoundManager {
    private GamePhase gamePhase;
    private int roundNum;
    private boolean lastRound = false;
    private final int maxNumMoves;
    private int numMoves = 0;

    RoundManager(Integer numPlayers) {
        gamePhase = GamePhase.PLANNING;
        roundNum = 1;
        if (numPlayers == 3)
            maxNumMoves = 4;
        else
            maxNumMoves = 3;
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


}
