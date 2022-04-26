package it.polimi.ingsw.network.messages.enums;

/**
 * Enum for the possible locations of a move
 */
public enum MoveLocation {
    ENTRANCE(true, true),
    HALL(true, false),
    ISLAND(true, true),
    CARD(true, false);

    /**
     * True if the "from location" requires an index, false otherwise
     */
    private final boolean doesRequireFromIndex;
    /**
     * True if the "to location" requires an index, false otherwise
     */
    private final boolean doesRequireToIndex;

    /**
     * Constructor
     *
     * @param doesRequireFromIndex True if the "from location" requires an index, false otherwise
     * @param doesRequireToIndex   True if the "to location" requires an index, false otherwise
     */
    MoveLocation(boolean doesRequireFromIndex, boolean doesRequireToIndex) {
        this.doesRequireFromIndex = doesRequireFromIndex;
        this.doesRequireToIndex = doesRequireToIndex;
    }

    /**
     * @return True if the "from location" requires an index, false otherwise
     */
    public boolean requiresFromIndex() {
        return doesRequireFromIndex;
    }
    /**
     * @return True if the "to location" requires an index, false otherwise
     */
    public boolean requiresToIndex() {
        return doesRequireToIndex;
    }
    /**
     * @return True if the location requires an index, false otherwise
     */
    public boolean requiresIndex() {
        return doesRequireFromIndex || doesRequireToIndex;
    }
}
