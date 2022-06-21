package it.polimi.ingsw.network.listeners;

import it.polimi.ingsw.network.messages.Message;

import java.util.EventObject;

/**
 * This class represents a message event.
 */
public class MessageEvent extends EventObject {
    /**
     * The message.
     */
    private final Message message;

    /**
     * Constructor.
     *
     * @param source  the source of the event
     * @param message the message
     */
    public MessageEvent(Object source, Message message) {
        super(source);
        this.message = message;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public Message getMessage() {
        return message;
    }
}
