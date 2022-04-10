package it.polimi.ingsw.network.messages.enums;

import it.polimi.ingsw.network.messages.Move;
import it.polimi.ingsw.network.messages.client.MovedStudent;
import it.polimi.ingsw.network.messages.client.SwappedStudent;
import it.polimi.ingsw.network.messages.server.MoveStudent;
import it.polimi.ingsw.network.messages.server.SwapStudents;

import java.util.HashMap;
import java.util.Map;

public enum MoveType {
    MOVE(MoveStudent.class),
    SWAP(SwapStudents.class),

    MOVED(MovedStudent.class),
    SWAPPED(SwappedStudent.class);

    private static final Map<Class<? extends Move>, MoveType> LOOKUP_MAP;

    static {
        LOOKUP_MAP = new HashMap<>();
        for (MoveType m: MoveType.values())
            LOOKUP_MAP.put(m.getMoveClass(), m);
    }

    private final Class<? extends Move> moveClass;

    MoveType(Class<? extends Move> moveClass) {
        this.moveClass = moveClass;
    }

    public Class<? extends Move> getMoveClass() {
        return moveClass;
    }

    public static MoveType retrieveByMove(Move m) {
        return LOOKUP_MAP.get(m.getClass());
    }
}
