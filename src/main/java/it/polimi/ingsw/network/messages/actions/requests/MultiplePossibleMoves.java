package it.polimi.ingsw.network.messages.actions.requests;

import it.polimi.ingsw.network.messages.ActionRequest;
import it.polimi.ingsw.network.messages.moves.MoveActionRequest;

import java.util.LinkedList;
import java.util.List;

/**
 * This message tells the player to move or swap students.
 */
public class MultiplePossibleMoves implements ActionRequest {

    /**
     * possible moves the player can make.
     */
    private final List<MoveActionRequest> possibleMoves;

    /**
     * Creates message.
     * @param moves the player can make.
     */
    public MultiplePossibleMoves(List<MoveActionRequest> moves) {
        possibleMoves = moves;
    }

    /**
     * @return possible moves the player can make.
     */
    public List<MoveActionRequest> getPossibleMoves() {
        return new LinkedList<>(possibleMoves);
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        if (possibleMoves != null && !possibleMoves.isEmpty()) {
            for (MoveActionRequest move: possibleMoves)
                if (move == null || !move.isValid())
                    return false;
            return true;
        }
        return false;
    }
}
