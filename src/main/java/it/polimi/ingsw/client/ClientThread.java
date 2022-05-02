package it.polimi.ingsw.client;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.listeners.MessageEvent;
import it.polimi.ingsw.server.listeners.MessageListener;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePreset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static java.lang.Thread.sleep;

public class ClientThread implements Runnable, MessageListener{

    String hostname = "127.0.0.1";
    int portNumber = 1234;
    String clientName;
    Client client;
    Message message;

    @Override
    public void run() {

        try {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("CT: choose a nickname: ");
            clientName = stdIn.readLine();

            client = new Client(clientName, hostname, portNumber);
            client.addListener(this);

            PrintWriter out = new PrintWriter(client.getSocket().getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
            System.out.println("CT: system loaded");

            Message login = new Login(clientName);
            System.out.println(MessageBuilder.toJson(login));
            client.putInQueue(login);

            boolean commUp = true;
            while (commUp) {
                Message outMessage = null;
                if (message.isValid()) {
                    if (MessageType.retrieveByMessageClass(message).equals(MessageType.COMM_MESSAGE)) {
                        CommMsgType type = null;
                        for (CommMsgType commType : CommMsgType.values()) {
                            if (((CommMessage) message).getType().getMessage().equals(commType.getMessage()))
                                type = commType;
                        }
                        if (type.equals(CommMsgType.CHOOSE_PARTY_TYPE)) {
                            String line = stdIn.readLine();
                            boolean isSet = true;
                            char mode = 'n';
                            char num = 'n';
                            while (isSet) {
                                if (line.length() == 2) {
                                    mode = line.charAt(0);
                                    num = line.charAt(1);
                                    if (mode != 'n' && num != 'n')
                                        isSet = false;
                                    else
                                        System.out.println("CT: input written incorrectly (be sure to write the two character onw after the other, with no spaces)");
                                } else
                                    System.out.println("CT: input error, retry");
                            }
                            GamePreset preset = null;
                            if (num == '2')
                                preset = GamePreset.TWO;
                            if (num == '3')
                                preset = GamePreset.THREE;
                            if (num == '4')
                                preset = GamePreset.FOUR;
                            GameMode gameMode = null;
                            if (mode == 'a')
                                gameMode = GameMode.EASY;
                            if (mode == 'b')
                                gameMode = GameMode.EXPERT;
                            outMessage = new ChosenGame(preset, gameMode);
                        }
                    }
                    client.putInQueue(outMessage);
                    commUp = false;
                }
            }

            System.out.println("CT: answer:" + in.readLine());
            System.out.println("CT: closing");
            client.closeConnection();
        } catch (IOException e) {
            System.err.println("CT: Couldn't get I/O for the connection to " + hostname);
        }
    }

    @Override
    public void onMessage(MessageEvent event) {
        message = event.getMessage();
    }
}
