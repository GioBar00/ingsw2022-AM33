package it.polimi.ingsw.network.messages.enums;

public enum MoveLocation {
    ENTRANCE(true),
    HALL(false),
    ISLAND(true),
    CARD(true);

    private final boolean doesRequiresIndexes;

    MoveLocation(boolean doesRequiresIndexes) {
        this.doesRequiresIndexes = doesRequiresIndexes;
    }

    public boolean requiresIndex() {
        return doesRequiresIndexes;
    }
}
