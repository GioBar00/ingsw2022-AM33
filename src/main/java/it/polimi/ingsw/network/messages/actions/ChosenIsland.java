package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.Action;

/**
 * This message signifies that the player chose an island.
 */
public class ChosenIsland implements Action {

    /**
     * index of the chosen island.
     */
    private final Integer islandIndex;

    /**
     * Creates the message.
     * @param islandIndex index of the chosen island.
     */
    public ChosenIsland(Integer islandIndex) {
        this.islandIndex = islandIndex;
    }

    /**
     * @return index of the chosen island.
     */
    public Integer getIslandIndex() {
        return islandIndex;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return islandIndex != null;
    }
}
