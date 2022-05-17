package it.polimi.ingsw.client;

import it.polimi.ingsw.server.Server;

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
