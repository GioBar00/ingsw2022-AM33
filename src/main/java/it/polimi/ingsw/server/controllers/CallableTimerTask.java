package it.polimi.ingsw.server.controllers;

/**
 * Interface for methods exposed to TimerTask
 */
public interface CallableTimerTask {
    /**
     * Used for notify the end of time for a connection
     */
    void setNotAlive();

    /**
     * Used for notify the end of a time period
     */
    void endOfTime();
}
