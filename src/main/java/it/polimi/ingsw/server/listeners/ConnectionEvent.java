package it.polimi.ingsw.server.listeners;

import java.util.EventObject;

/**
 * Class used to report a Connection Closed event
 */
public class ConnectionEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ConnectionEvent(Object source) {
        super(source);
    }
}
