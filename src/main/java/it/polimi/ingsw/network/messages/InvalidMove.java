package it.polimi.ingsw.network.messages;

/**
 * This class represents a move that is not valid.
 */
public class InvalidMove implements Move {
    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return false;
    }
}
