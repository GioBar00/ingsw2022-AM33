package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.enums.MoveLocation;

import java.util.Set;

public class SwapStudents extends MoveStudent {
    public SwapStudents(MoveLocation from, Set<Integer> fromIndexes, MoveLocation to, Set<Integer> toIndexes) {
        super(from, fromIndexes, to, toIndexes);
    }
}
