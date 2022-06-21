package it.polimi.ingsw.network.messages;

/**
 * Valid message used to unblock threads waiting for new messages in queues.
 */
public class IgnoreMessage implements Message{
    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return true;
    }
}
