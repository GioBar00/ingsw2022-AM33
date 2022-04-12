package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

/**
 * Message for request the start of a game
 */
public class StartGame extends Message {
    /**
     * Default value of the message
     */
    boolean startGame;

    /**
     * This message is built when someone wants to start the game so the flag value is set on true
     */
    public StartGame() { startGame = true; }


    /**
     * Used for checking the validity of the message
     * @return true if the message is valid
     */
    public boolean isValid() {
        return startGame;
    }

}
