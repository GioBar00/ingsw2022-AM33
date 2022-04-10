package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Move;
import it.polimi.ingsw.network.messages.enums.MoveLocation;

import java.util.Set;

public class MoveStudent extends Move {

    private final MoveLocation from;
    private final Set<Integer> fromIndexesSet;

    private final MoveLocation to;
    private final Set<Integer> toIndexesSet;

    public MoveStudent() {
        from = null;
        fromIndexesSet = null;
        to = null;
        toIndexesSet = null;
    }

    public MoveStudent(MoveLocation from, Set<Integer> fromIndexesSet, MoveLocation to, Set<Integer> toIndexesSet) {
        this.from = from;
        this.fromIndexesSet = fromIndexesSet;
        this.to = to;
        this.toIndexesSet = toIndexesSet;
    }

    public MoveLocation getFrom() {
        return from;
    }

    public Set<Integer> getFromIndexesSet() {
        if (fromIndexesSet == null)
            return null;
        return Set.copyOf(fromIndexesSet);
    }

    public MoveLocation getTo() {
        return to;
    }

    public Set<Integer> getToIndexesSet() {
        if (toIndexesSet == null)
            return null;
        return Set.copyOf(toIndexesSet);
    }

    @Override
    public boolean isValid() {
        if(from != null && to != null) {
            if (from.requiresIndex() && fromIndexesSet == null)
                return false;
            return !to.requiresIndex() || toIndexesSet != null;
        }
        return false;
    }
}
