package it.polimi.ingsw.client;

import it.polimi.ingsw.server.Server;

public class ServerThread implements Runnable{

    @Override
    public void run() {
        Server server = new Server();
        server.handleRequest();
    }
}
