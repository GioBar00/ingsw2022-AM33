package it.polimi.ingsw.network.messages;

import java.io.Serializable;

/**
 * Interface for all the messages
 */
public interface Message extends Serializable {

    /**
     * @return if the message is valid.
     */
    boolean isValid();
}
