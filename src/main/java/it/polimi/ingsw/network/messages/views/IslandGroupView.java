package it.polimi.ingsw.network.messages.views;

import java.io.Serializable;
import java.util.List;

public class IslandGroupView implements Serializable {
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
