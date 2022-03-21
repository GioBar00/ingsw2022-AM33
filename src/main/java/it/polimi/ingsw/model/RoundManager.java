package it.polimi.ingsw.model;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class RoundManager {
    private GamePhase gamePhase;
    private int roundNum;
    private boolean lastRound = false;
    private final int maxNumMoves;
    private int numMoves = 0;
    Integer currentPlayerOrderIndex = 0;
    Integer[] playerOrderIndexes;

    RoundManager(Integer numPlayers) {
        gamePhase = GamePhase.PLANNING;
        roundNum = 1;
        if (numPlayers == 3)
            maxNumMoves = 4;
        else
            maxNumMoves = 3;
        playerOrderIndexes = new Integer[numPlayers];
        playerOrderIndexes[0] = ThreadLocalRandom.current().nextInt(0, numPlayers);
        calculateClockwiseOrder();
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

    /**
     * Calculate clockwise order starting from playerOrderIndex[0].
     * Ex: 3 2 0 1 --> 3 0 1 2
     */
    private void calculateClockwiseOrder() {
        for(int i = 1; i < playerOrderIndexes.length; i++) {
            playerOrderIndexes[i] = (playerOrderIndexes[i - 1] + 1) % playerOrderIndexes.length;
        }
    }

    /**
     * Calculates players' order based on the assistant card they played.
     * If they played the same assistant card, goes first the one who played it.
     * @param players array of players in the game.
     */
    void calculatePlayerOrder(Player[] players) {
        List<Integer> ordered = Arrays.asList(playerOrderIndexes);
        ordered.sort((i1, i2) -> {
            int r = players[i1].getAssistantCard().getValue().compareTo(players[i2].getAssistantCard().getValue());
            if(r == 0)
                return Integer.compare(ordered.indexOf(i1), ordered.indexOf(i1));
            return r;
        });
        ordered.toArray(playerOrderIndexes);
    }

}
