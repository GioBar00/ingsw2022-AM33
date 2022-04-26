package it.polimi.ingsw.server.listeners;

import java.util.EventListener;

/**
 * Interface for the party ender listener
 */
public interface EndPartyListener extends EventListener {

    /**
     * Method called when a connection is closed
     * @param event of the connection
     */
    void onEndPartyEvent(EndPartyEvent event);
}
