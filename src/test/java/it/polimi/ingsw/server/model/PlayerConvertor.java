package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;

/**
 * Helper class with methods for convert data into a {@link PlayerDetails} class.
 */
public class PlayerConvertor {

    /**
     * This method returns a {@link PlayerDetails} from a nickname and a wizard.
     *
     * @param nickname the nickname of the player.
     * @param wizard   the wizard of the player.
     * @return {@link PlayerDetails} of the player.
     */
    public PlayerDetails getPlayer(String nickname, Wizard wizard) {
        PlayerDetails details = new PlayerDetails(nickname);
        details.setWizard(wizard);
        return details;
    }

    /**
     * This method returns a {@link PlayerDetails} from a nickname, a wizard and a team (a tower color).
     *
     * @param nickname the nickname of the player.
     * @param wizard   the wizard of the player.
     * @param tower    the team of the player.
     * @return {@link PlayerDetails} of the player.
     */
    public PlayerDetails getPlayer(String nickname, Wizard wizard, Tower tower) {
        PlayerDetails details = new PlayerDetails(nickname);
        details.setWizard(wizard);
        details.setTower(tower);
        return details;
    }
}
