package it.polimi.ingsw.client;

import it.polimi.ingsw.server.Server;

public class ServerThread {

    public static void main( String[] args ) {
        Server server = new Server();
        server.handleRequest();
    }
}
