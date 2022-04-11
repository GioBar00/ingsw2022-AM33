package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;

import java.util.Set;

/**
 * This message tells the player to choose an island.
 */
public class ChooseIsland extends Message {

    /**
     * available island indexes.
     */
    private final Set<Integer> availableIslandIndexes;

    /**
     * Creates message.
     * @param availableIslandIndexes available island indexes.
     */
    public ChooseIsland(Set<Integer> availableIslandIndexes) {
        this.availableIslandIndexes = availableIslandIndexes;
    }

    /**
     * @return available island indexes.
     */
    public Set<Integer> getAvailableIslandIndexes() {
        return Set.copyOf(availableIslandIndexes);
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return availableIslandIndexes != null;
    }
}
