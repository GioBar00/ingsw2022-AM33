package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.CommMsgType;

/**
 * This class represents a communication message sent by the server to the client.
 */
public class CommMessage implements Message {
    /**
     * The communication message type.
     */
    private final CommMsgType type;

    /**
     * Creates a new communication message.
     * @param type the communication message type.
     */
    public CommMessage(CommMsgType type) {
        this.type = type;
    }

    /**
     * Gets the communication message type.
     * @return the communication message type.
     */
    public CommMsgType getType() {
        return type;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return type != null;
    }
}
