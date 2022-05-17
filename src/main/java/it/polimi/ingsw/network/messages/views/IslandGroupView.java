package it.polimi.ingsw.network.messages.views;

import java.io.Serializable;
import java.util.List;

public class IslandGroupView implements Serializable {
    /**
     * view of the islands in the island group
     */
    private final List<IslandView> islands;
    /**
     * boolean attribute which is true if the island is blocked
     */
    private final boolean isBlocked;


    public IslandGroupView(List<IslandView> islands, boolean isBlocked) {
        this.islands = islands;
        this.isBlocked = isBlocked;
    }

    /**
     * @return the view of the islands
     */
    public List<IslandView> getIslands() {
        return islands;
    }

    /**
     * @return boolean attribute isBlocked
     */
    public boolean isBlocked() {
        return isBlocked;
    }
}
