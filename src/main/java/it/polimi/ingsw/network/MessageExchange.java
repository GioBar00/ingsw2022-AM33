package it.polimi.ingsw.network;

import it.polimi.ingsw.network.listeners.DisconnectListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * This class is used to exchange messages.
 */
public abstract class MessageExchange {

    /**
     * This method is used to receive a message.
     *
     * @param reader             the reader used to read the message.
     * @param disconnectListener the listener used to handle disconnection.
     * @return the message received.
     * @throws IOException if an I/O error occurs.
     */
    public static Message receiveMessage(BufferedReader reader, DisconnectListener disconnectListener) throws IOException {
        String line = reader.readLine();

        while (reader.ready() && line == null) {
            line = reader.readLine();
        }

        if (line == null) {
            System.out.println("MexEx: null line -> disconnect");
            disconnectListener.onDisconnect(null);
            return null;
        }

        if (line.equals("") || line.equals("null")) return null;

        Message message = MessageBuilder.fromJson(line);
        if (!(MessageType.retrieveByMessage(message) == MessageType.COMM_MESSAGE && (((CommMessage) message).getType() == CommMsgType.PONG || ((CommMessage) message).getType() == CommMsgType.PING)))
            System.out.println("Real message received - " + line);
        return message;
    }

    /**
     * This method is used to receive a message.
     *
     * @param reader the reader used to read the message.
     * @return the message received.
     * @throws IOException if an I/O error occurs.
     */
    public static Message receiveMessage(BufferedReader reader) throws IOException {
        return receiveMessage(reader, (event -> {
        }));
    }

    /**
     * This method is used to send a message.
     *
     * @param message the message to send.
     * @param writer the writer used to write the message.
     * @throws IOException if an I/O error occurs.
     */
    public static void sendMessage(Message message, BufferedWriter writer) throws IOException {
        if (message != null && message.isValid()) {
            writer.write(MessageBuilder.toJson(message));
            writer.write("\n");
            writer.flush();
            if (!(MessageType.retrieveByMessage(message) == MessageType.COMM_MESSAGE && (((CommMessage) message).getType() == CommMsgType.PONG || ((CommMessage) message).getType() == CommMsgType.PING)))
                System.out.println("Real message sent - " + MessageBuilder.toJson(message));
        }
    }

}
