package it.polimi.ingsw.network.messages;

/**
 * This class represents an invalid message.
 */
public class InvalidMessage implements Message {
    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return false;
    }
}
