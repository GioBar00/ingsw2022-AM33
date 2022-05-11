package it.polimi.ingsw.client;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePreset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

public class ClientMain {

    static BufferedReader stdIn;
    static List<TestClient> clients = new LinkedList<>();
    public static void main(String[] args) {

        stdIn = new BufferedReader(new InputStreamReader(System.in));
        boolean serverOn = false;
        ServerThread st;
        Thread t_server;
        try {
            String input = stdIn.readLine();

            while (!input.equals("shutdown")) {
                switch (input) {
                    case "new server" -> {
                        if (!serverOn) {
                            st = new ServerThread();
                            t_server = new Thread(st);
                            t_server.start();
                            serverOn = true;
                        } else
                            System.out.println("server already launched");
                    }
                    case "new client" -> createNewClient();
                    case "new message" -> sendNewMessage();
                    default -> System.out.println("invalid input... retry...");
                }
                input = stdIn.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(-1);
    }

    private static void sendNewMessage() {
        try {
            int index;
            do {
                System.out.println("Choose a client to send a message from: ");
                for (int i = 0; i < clients.size(); i++)
                    System.out.println(i + ": " + clients.get(i).getNickname());
                index = Integer.parseInt(stdIn.readLine());
                if (index < 0 || index >= clients.size())
                    System.out.println("Invalid index");
            } while (index < 0 || index >= clients.size());

            Message m;
            do {
                System.out.println("Write a message: ");
                String json = stdIn.readLine();
                m = MessageBuilder.fromJson(json);
                if (!m.isValid())
                    System.out.println("Invalid message");
            } while (!m.isValid());
            clients.get(index).sendMessage(m);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createNewClient() {
        try {
            System.out.println("CT: choose a nickname: ");
            String clientName = stdIn.readLine();
            TestClient client = new TestClient(clientName, "127.0.0.1", 1234);
            clients.add(client);
            client.connect();
            if (clients.size() == 1)
                client.sendMessage(new ChosenGame(GamePreset.TWO, GameMode.EXPERT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
