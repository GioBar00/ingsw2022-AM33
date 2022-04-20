package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.Tower;

import java.util.ArrayList;
import java.util.List;

public class IslandGroupView {
    private final List<IslandView> islands;
    private final boolean isBlocked;

    public IslandGroupView(List<IslandView> islands, boolean isBlocked) {
        this.islands = islands;
        this.isBlocked = isBlocked;
    }

    public List<IslandView> getIslands() {
        return islands;
    }

    public boolean isBlocked() {
        return isBlocked;
    }
}
