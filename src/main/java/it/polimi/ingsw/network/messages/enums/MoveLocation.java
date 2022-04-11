package it.polimi.ingsw.network.messages.enums;

public enum MoveLocation {
    ENTRANCE(true, true),
    HALL(true, false),
    ISLAND(true, true),
    CARD(true, false);

    private final boolean doesRequireFromIndex;
    private final boolean doesRequireToIndex;

    MoveLocation(boolean doesRequireFromIndex, boolean doesRequireToIndex) {
        this.doesRequireFromIndex = doesRequireFromIndex;
        this.doesRequireToIndex = doesRequireToIndex;
    }

    public boolean requiresFromIndex() {
        return doesRequireFromIndex;
    }
    public boolean requiresToIndex() {
        return doesRequireToIndex;
    }
    public boolean requiresIndex() {
        return doesRequireFromIndex || doesRequireToIndex;
    }
}
