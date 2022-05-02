package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;
import it.polimi.ingsw.server.model.player.Player;

public class PlayerDetails {

    private final String nickname;

    private Wizard wizard;

    private Tower tower = null;

    public PlayerDetails(String nickname) {
        this.nickname = nickname;
    }

    public void setTower(Tower tower) {
        this.tower = tower;
    }

    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    public String getNickname() {
        return nickname;
    }

    public Wizard getWizard() {
        return wizard;
    }

    public Tower getTower() {
        return tower;
    }
}
