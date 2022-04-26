package it.polimi.ingsw.server.listeners;

import java.util.EventObject;

/**
 * Class used to report when the party needs to be closed event
 */
public class EndPartyEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public EndPartyEvent(Object source) {
        super(source);
    }
}
