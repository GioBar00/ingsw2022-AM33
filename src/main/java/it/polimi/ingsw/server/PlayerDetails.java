package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;

/**
 * This class contains the details of a player
 */
public class PlayerDetails {

    /**
     * The player's nickname
     */
    private final String nickname;
    /**
     * The player's chosen wizard
     */
    private Wizard wizard;
    /**
     * The player's chosen tower
     */
    private Tower tower = null;

    /**
     * Constructor
     *
     * @param nickname the player's nickname
     */
    public PlayerDetails(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets the player's nickname
     *
     * @return the player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Gets the player's chosen wizard
     *
     * @return the player's chosen wizard
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     * Sets the player's chosen wizard
     */
    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    /**
     * Gets the player's chosen tower
     *
     * @return the player's chosen tower
     */
    public Tower getTower() {
        return tower;
    }

    /**
     * Constructor
     *
     * @param tower the player's chosen tower
     */
    public void setTower(Tower tower) {
        this.tower = tower;
    }
}
