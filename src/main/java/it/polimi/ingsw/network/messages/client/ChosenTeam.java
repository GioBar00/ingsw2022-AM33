package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.network.messages.Message;

/**
 * This class represents the message sent by the client to choose the team
 */
public class ChosenTeam implements Message {
    /**
     * The chosen team
     */
    private final Tower tower;

    /**
     * Constructor
     * @param tower the chosen team
     */
    public ChosenTeam(Tower tower) {
        this.tower = tower;
    }

    /**
     * Getter
     * @return the chosen team
     */
    public Tower getTower() {
        return tower;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return tower.equals(Tower.WHITE) || tower.equals(Tower.BLACK) || tower.equals(Tower.GREY);
    }
}
