package it.polimi.ingsw.server.listeners;

/**
 * Interface for the EndGameListenerSubscriber
 */
public interface EndGameListenerSubscriber {

    /**
     * Method to subscribe a EndGameListener
     * @param listener the EndGameListener to subscribe
     */
    void setEndGameListener(EndGameListener listener);

    /**
     * Method to notify the EndGameListener
     */
    void notifyEndGame();
}
