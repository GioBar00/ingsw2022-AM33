package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.Move;

import java.util.List;

public class MultipleMoves extends Message {

    private final List<Move> possibleMoves;

    public MultipleMoves(List<Move> moves) {
        possibleMoves = moves;
    }

    public List<Move> getPossibleMoves() {
        return possibleMoves;
    }

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
