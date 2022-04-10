package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.enums.MoveLocation;

public class SwappedStudent extends MovedStudent {

    public SwappedStudent(MoveLocation from, Integer fromIndex, MoveLocation to, Integer toIndex) {
        super(from, fromIndex, to, toIndex);
    }
}
