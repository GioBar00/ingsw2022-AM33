package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.EnumSet;

/**
 * This class is a message used for communicate the winners of the game.
 */
public class Winners implements Message {

    /**
     * The list of winners.
     */
    private final EnumSet<Tower> winners;

    /**
     * Constructor.
     *
     * @param winners the list of winners.
     */
    public Winners(EnumSet<Tower> winners) {
        this.winners = winners;
    }

    /**
     * This method returns the list of winners.
     * @return the list of winners.
     */
    public EnumSet<Tower> getWinners(){
        return winners;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return true;
    }
}
