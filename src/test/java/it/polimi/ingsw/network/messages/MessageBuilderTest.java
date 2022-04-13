package it.polimi.ingsw.network.messages;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MessageBuilderTest {
    /**
     * Serializes and deserializes a message
     * @param original message.
     * @return deserialized message.
     */
    public static Message toAndFromJson(Message original) {
        String json = MessageBuilder.toJson(original);
        return MessageBuilder.fromJson(json);
    }

    /**
     * Tests not valid message.
     */
    @Test
    void invalidMessage() {
        // not json
        String json = "{This is no json::\"/%/%\" \n [], random}";
        Message m = MessageBuilder.fromJson(json);
        assertFalse(m.isValid());

        // with invalid enum.
        json = "{\"type\":\"PLAYED_ASSISTANT_CARD\",\"message\":{\"assistantCard\":\"FIFTY\"}}";
        m = MessageBuilder.fromJson(json);
        assertFalse(m.isValid());
    }
}