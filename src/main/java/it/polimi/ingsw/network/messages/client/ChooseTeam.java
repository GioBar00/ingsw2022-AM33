package it.polimi.ingsw.network.messages.client;


import it.polimi.ingsw.model.enums.Tower;
import it.polimi.ingsw.network.messages.Message;

public class ChooseTeam extends Message {
    private final Tower tower;

    public ChooseTeam(Tower tower) {
        this.tower = tower;
    }

    public Tower getTower() {
        return tower;
    }

    @Override
    public boolean isValid() {
        return tower.equals(Tower.WHITE) || tower.equals(Tower.BLACK) || tower.equals(Tower.GREY);
    }
}
