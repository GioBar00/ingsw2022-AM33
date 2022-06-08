package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.Action;

/**
 * This message signifies that the player chose a cloud.
 */
public class ChosenCloud implements Action {

    /**
     * index of the chosen cloud.
     */
    private final Integer cloudIndex;

    /**
     * Creates the message.
     *
     * @param cloudIndex index of the chosen cloud.
     */
    public ChosenCloud(Integer cloudIndex) {
        this.cloudIndex = cloudIndex;
    }

    /**
     * @return index of the chosen cloud.
     */
    public Integer getCloudIndex() {
        return cloudIndex;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return cloudIndex != null;
    }
}
