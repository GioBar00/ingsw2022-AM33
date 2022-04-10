package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

public class ChosenCloud extends Message {
    private final Integer cloudIndex;

    public ChosenCloud(Integer cloudIndex) {
        this.cloudIndex = cloudIndex;
    }

    public Integer getCloudIndex() {
        return cloudIndex;
    }

    @Override
    public boolean isValid() {
        return cloudIndex != null;
    }
}
