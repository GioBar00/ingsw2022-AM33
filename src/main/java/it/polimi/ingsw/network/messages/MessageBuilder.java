package it.polimi.ingsw.network.messages;

import com.google.gson.*;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveType;

import java.lang.reflect.Type;

/**
 * Serializes and Deserializes messages in Json.
 */
public final class MessageBuilder {

    /**
     * Serializes the message to Json.
     * @param m message to serialize.
     * @return the serialized message.
     */
    public static String toJson(Message m) {
        Gson g = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Message.class, new MessageSerializer())
                .create();
        return g.toJson(m, Message.class);
    }

    /**
     * Deserializes the serialized message.
     * @param json the serialized message
     * @return the deserialized message or an invalid message if the serialized message is invalid.
     */
    public static Message fromJson(String json) {
        Gson g = new GsonBuilder()
                .registerTypeAdapter(Message.class, new MessageDeserializer())
                .create();
        Message m = g.fromJson(json, Message.class);
        if (m != null && m.isValid())
            return m;

        return new Message();
    }

    /**
     * Custom serializer for Message class.
     */
    public static class MessageSerializer implements JsonSerializer<Message> {

        @Override
        public JsonElement serialize(Message message, Type type, JsonSerializationContext jsonSerializationContext) {
            Gson g = new GsonBuilder()
                    .registerTypeAdapter(Move.class, new MoveSerializer())
                    .create();
            JsonObject jsonObject = new JsonObject();
            MessageType t = MessageType.retrieveByMessageClass(message);
            jsonObject.add("type", g.toJsonTree(t));
            jsonObject.add("message", g.toJsonTree(message));
            return jsonObject;
        }
    }

    /**
     * Custom deserializer for Message class.
     */
    public static class MessageDeserializer implements JsonDeserializer<Message> {

        @Override
        public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson g = new GsonBuilder()
                    .registerTypeAdapter(Move.class, new MoveDeserializer())
                    .create();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.isJsonObject()) {
                if (jsonObject.has("type") && jsonObject.has("message")) {
                    try {
                        MessageType t = MessageType.valueOf(jsonObject.get("type").getAsString());
                        Message des = g.fromJson(jsonObject.get("message"), t.getMessageClass());
                        if (des.isValid()) {
                            return des;
                        }
                    } catch (IllegalArgumentException ignored) {
                        return new Message();
                    }
                }
            }
            return new Message();
        }
    }

    /**
     * Custom serializer for Move class.
     */
    public static class MoveSerializer implements JsonSerializer<Move> {

        @Override
        public JsonElement serialize(Move move, Type type, JsonSerializationContext jsonSerializationContext) {
            Gson g = new GsonBuilder()
                    .create();
            JsonObject jsonObject = new JsonObject();
            MoveType t = MoveType.retrieveByMove(move);
            jsonObject.add("type", g.toJsonTree(t));
            jsonObject.add("move", g.toJsonTree(move));
            return jsonObject;
        }
    }

    /**
     * Custom deserializer for Move class.
     */
    public static class MoveDeserializer implements JsonDeserializer<Move> {

        @Override
        public Move deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson g = new GsonBuilder()
                    .create();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.isJsonObject()) {
                if (jsonObject.has("type") && jsonObject.has("move")) {
                    try {
                        MoveType t = MoveType.valueOf(jsonObject.get("type").getAsString());
                        Move des = g.fromJson(jsonObject.get("move"), t.getMoveClass());
                        if (des.isValid()) {
                            return des;
                        }
                    } catch (IllegalArgumentException ignored) {
                        return new Move();
                    }
                }
            }
            return new Move();
        }
    }
}
