package it.polimi.ingsw.network.messages.messagesView;

import java.util.ArrayList;

public class IslandGroupView {
    private final ArrayList<IslandView> islands  = new ArrayList<>();
    private boolean isBlocked;

    public IslandGroupView() {
        islands.add(new IslandView());
        isBlocked = false;
    }

    public ArrayList<IslandView> getIslands() {
        return islands;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
