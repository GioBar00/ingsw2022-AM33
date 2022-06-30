package it.polimi.ingsw.network.messages.enums;

import it.polimi.ingsw.network.messages.Move;
import it.polimi.ingsw.network.messages.actions.MovedStudent;
import it.polimi.ingsw.network.messages.actions.SwappedStudents;
import it.polimi.ingsw.network.messages.actions.requests.MoveStudent;
import it.polimi.ingsw.network.messages.actions.requests.SwapStudents;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum that represents the type of move
 */
public enum MoveType {
    /**
     * Invalid Move
     */
    INVALID(null),
    /**
     * Message that tells the player they can move a student from a Location to another
     */
    MOVE(MoveStudent.class),
    /**
     * Message that tells the player they can swap two students between each other
     */
    SWAP(SwapStudents.class),

    /**
     * Message that tells the server that a player has moved a student
     */
    MOVED(MovedStudent.class),
    /**
     * Message that tells the server that two students have been swapped
     */
    SWAPPED(SwappedStudents.class);

    /**
     * A map that contains the move type and the class of the move.
     */
    private static final Map<Class<? extends Move>, MoveType> LOOKUP_MAP;


    static {
        LOOKUP_MAP = new HashMap<>();
        for (MoveType m : MoveType.values())
            LOOKUP_MAP.put(m.getMoveClass(), m);
    }

    /**
     * The class of the move.
     */
    private final Class<? extends Move> moveClass;

    /**
     * Constructor.
     *
     * @param moveClass the class of the move.
     */
    MoveType(Class<? extends Move> moveClass) {
        this.moveClass = moveClass;
    }

    /**
     * @return the class of the move
     */
    public Class<? extends Move> getMoveClass() {
        return moveClass;
    }

    /**
     * @param m the move
     * @return the move type
     */
    public static MoveType retrieveByMove(Move m) {
        if (LOOKUP_MAP.containsKey(m.getClass()))
            return LOOKUP_MAP.get(m.getClass());
        return INVALID;
    }
}
