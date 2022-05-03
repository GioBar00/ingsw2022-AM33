package it.polimi.ingsw.client;

import it.polimi.ingsw.network.MessageExchangeHandler;
import it.polimi.ingsw.network.MessageHandler;
import it.polimi.ingsw.network.listeners.DisconnectEvent;
import it.polimi.ingsw.network.listeners.DisconnectListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;

import java.io.*;
import java.net.Socket;

public class Client implements MessageHandler, DisconnectListener {

    private final String nickname;

    private final String hostname;

    private final int port;

    private final MessageExchangeHandler messageExchangeHandler;

    public Client(String nickname, String hostname, int port) {
        this.nickname = nickname;
        this.port = port;
        this.hostname = hostname;
        messageExchangeHandler = new MessageExchangeHandler(this);
        messageExchangeHandler.setDisconnectListener(this);
    }

    public String getNickname() {
        return nickname;
    }

    public void connect() {
        try {
            messageExchangeHandler.setSocket(new Socket(hostname, port));
            messageExchangeHandler.start();
            messageExchangeHandler.sendMessage(new Login(nickname));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        messageExchangeHandler.sendMessage(message);
    }

    private void handleGameCreation(Message m) {
        switch (MessageType.retrieveByMessage(m)) {
            case COMM_MESSAGE -> {
                if (((CommMessage) m).getType() == CommMsgType.CHOOSE_GAME) {
                    //FIXME: view chose game
                    System.out.println(nickname +": choose game");
                } else
                    System.out.println("ERROR: " + nickname + ": " + ((CommMessage) m).getType().getMessage());
            }
            case CURRENT_TEAMS -> {

            }
            case CURRENT_GAME_STATE -> {

            }
        }
    }

    private void handleCommMessage(Message m) {

    }


    /**
     * Invoked when a client disconnects from the server and vice versa.
     *
     * @param event the event object
     */
    @Override
    public void onDisconnect(DisconnectEvent event) {

    }

    /**
     * This method is called when a message is received.
     *
     * @param message the message received
     */
    @Override
    public void handleMessage(Message message) {
        System.out.println(nickname + ": received message ");
        System.out.println(MessageBuilder.toJson(message));
        switch (MessageType.retrieveByMessage(message)) {
            case COMM_MESSAGE -> handleCommMessage(message);

        }
    }
}