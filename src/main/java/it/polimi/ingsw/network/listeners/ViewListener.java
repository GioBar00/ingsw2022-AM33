package it.polimi.ingsw.network.listeners;

import it.polimi.ingsw.network.messages.Message;

import java.util.EventListener;

/**
 * Interface for the view listener
 */
public interface ViewListener extends EventListener {

    /**
     * Method called when the user want to update the model
     * @param event the request
     */
    void onMessage(Message event);
}
