package it.polimi.ingsw.network.listeners;

import java.util.EventObject;

/**
 * This class represents the event of a disconnection.
 */
public class DisconnectEvent extends EventObject {
    /**
     * Constructs a Disconnect Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public DisconnectEvent(Object source) {
        super(source);
    }
}
