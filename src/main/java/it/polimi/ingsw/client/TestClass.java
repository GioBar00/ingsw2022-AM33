package it.polimi.ingsw.client;

import it.polimi.ingsw.client.CLI.CLI;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.actions.requests.SwapStudents;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.model.enums.Tower;

import java.io.BufferedReader;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TestClass {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        String input = in.nextLine();

        if(input.equals("s")){
            System.out.println("Starting a server");
            Server server = new Server();
            server.handleRequests();
        }
        else {
            System.out.println("Starting a client");
            Client client = new Client(false);
            client.startClient();
        }

    }

}
