package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Move;
import it.polimi.ingsw.network.messages.enums.MoveLocation;

public class MovedStudent extends Move {

    private final MoveLocation from;
    private final Integer fromIndex;

    private final MoveLocation to;
    private final Integer toIndex;

    public MovedStudent(MoveLocation from, Integer fromIndex, MoveLocation to, Integer toIndex) {
        this.from = from;
        this.fromIndex = fromIndex;
        this.to = to;
        this.toIndex = toIndex;
    }

    public MoveLocation getFrom() {
        return from;
    }

    public Integer getFromIndex() {
        return fromIndex;
    }

    public MoveLocation getTo() {
        return to;
    }

    public Integer getToIndex() {
        return toIndex;
    }

    @Override
    public boolean isValid() {
        if(from != null && to != null) {
            if (from.requiresIndex() && fromIndex == null)
                return false;
            return !to.requiresIndex() || toIndex != null;
        }
        return false;
    }
}
