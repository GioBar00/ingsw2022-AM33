package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;

public class PlayerConvertor{

    public PlayerDetails getPlayer(String nickname, Wizard wizard) {
        PlayerDetails details = new PlayerDetails(nickname);
        details.setWizard(wizard);
        return  details;
    }

    public PlayerDetails getPlayer(String nickname, Wizard wizard, Tower tower) {
        PlayerDetails details = new PlayerDetails(nickname);
        details.setWizard(wizard);
        details.setTower(tower);
        return  details;
    }
}
