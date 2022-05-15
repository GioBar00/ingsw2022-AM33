package it.polimi.ingsw.server.enums;

/**
 * This class represents the state of the server.
 */
public enum ServerState {
    /**
     * The server is waiting for first player connections.
     */
    EMPTY,
    /**
     * The server is handling the first player connection.
     */
    HANDLING_FIRST,
    /**
     * Normal server state.
     */
    NORMAL
}
