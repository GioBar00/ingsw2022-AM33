package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;

import java.util.List;

public class ChooseCloud extends Message {

    private final List<Integer> availableCloudIndexes;

    public ChooseCloud(List<Integer> availableCloudIndexes) {
        this.availableCloudIndexes = availableCloudIndexes;
    }

    public List<Integer> getAvailableCloudIndexes() {
        return availableCloudIndexes;
    }

    @Override
    public boolean isValid() {
        return availableCloudIndexes != null;
    }
}
