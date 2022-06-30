package it.polimi.ingsw.network.messages.enums;

/**
 * Enum for the possible locations of a move
 */
public enum MoveLocation {
    /**
     * Entrance of the Schoolboard
     */
    ENTRANCE(true, true),
    /**
     * Hall of the Schoolboard
     */
    HALL(true, false),
    /**
     * Island
     */
    ISLAND(true, true),
    /**
     * Character card
     */
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

    /**
     * Convert a string into the proper MoveLocation
     *
     * @param input String
     * @return null if the value in input is not valid or the MoveLocation if it's valid
     */
    public static MoveLocation getFromString(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "entrance" -> ENTRANCE;
            case "hall" -> HALL;
            case "island" -> ISLAND;
            case "card" -> CARD;
            default -> null;
        };
    }
}
