package it.polimi.ingsw.network.messages;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link MessageBuilder} class.
 */
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

        // with invalid enum
        json = "{\"type\":\"PLAYED_ASSISTANT_CARD\",\"message\":{\"assistantCard\":\"FIFTY\"}}";
        m = MessageBuilder.fromJson(json);
        assertFalse(m.isValid());

        // invalid message type
        json = "{\"type\":\"INVALID_MESSAGE\",\"message\":{\"assistantCard\":\"ONE\"}}";
        m = MessageBuilder.fromJson(json);
        assertFalse(m.isValid());

        // invalid move type
        json = "{\"type\":\"MULTIPLE_POSSIBLE_MOVES\",\"message\":{\"possibleMoves\":[{\"type\":\"INVALID_MOVE\",\"move\":{\"from\":\"ENTRANCE\",\"fromIndexesSet\":[1,5],\"to\":\"HALL\"}}]}}";
        m = MessageBuilder.fromJson(json);
        assertFalse(m.isValid());

        // invalid move type
        json = "{\"type\":\"MULTIPLE_POSSIBLE_MOVES\",\"message\":{\"possibleMoves\":[{ \"invalid\":\"invalid\" }]}}";
        m = MessageBuilder.fromJson(json);
        assertFalse(m.isValid());

    }
}