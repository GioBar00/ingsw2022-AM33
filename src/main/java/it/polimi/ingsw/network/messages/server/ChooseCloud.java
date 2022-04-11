package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;

import java.util.Set;

/**
 * This message tells the player to choose a cloud.
 */
public class ChooseCloud extends Message {
    /**
     * available cloud indexes.
     */
    private final Set<Integer> availableCloudIndexes;

    /**
     * Creates message.
     * @param availableCloudIndexes available cloud indexes.
     */
    public ChooseCloud(Set<Integer> availableCloudIndexes) {
        this.availableCloudIndexes = availableCloudIndexes;
    }

    /**
     * @return available cloud indexes.
     */
    public Set<Integer> getAvailableCloudIndexes() {
        return Set.copyOf(availableCloudIndexes);
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return availableCloudIndexes != null;
    }
}
