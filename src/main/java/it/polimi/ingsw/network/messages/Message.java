package it.polimi.ingsw.network.messages;

import java.io.Serializable;

/**
 * Generic message
 */
public class Message implements Serializable {

    /**
     * @return if the message is valid.
     */
    public boolean isValid() {
        return false;
    }
}
