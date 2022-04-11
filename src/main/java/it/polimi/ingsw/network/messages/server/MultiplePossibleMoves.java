package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.Move;

import java.util.LinkedList;
import java.util.List;

/**
 * This message tells the player to move or swap students.
 */
public class MultiplePossibleMoves extends Message {

    /**
     * possible moves the player can make.
     */
    private final List<Move> possibleMoves;

    /**
     * Creates message.
     * @param moves the player can make.
     */
    public MultiplePossibleMoves(List<Move> moves) {
        possibleMoves = moves;
    }

    /**
     * @return possible moves the player can make.
     */
    public List<Move> getPossibleMoves() {
        return new LinkedList<>(possibleMoves);
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        if (possibleMoves != null) {
            for (Move move: possibleMoves)
                if (move == null || !move.isValid())
                    return false;
            return true;
        }
        return false;
    }
}
