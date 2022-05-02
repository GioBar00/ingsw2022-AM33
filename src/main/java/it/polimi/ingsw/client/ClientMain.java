package it.polimi.ingsw.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientMain{

    public static void main(String[] args) throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        boolean serverOn = false;
        ServerThread st;
        ClientThread ct;
        Thread t_server = null, t_client = null;

        String input = stdIn.readLine();
        
        while(!input.equals("shutdown")){
            if(input.equals("new server")){
                if (!serverOn){
                    st = new ServerThread();
                    t_server = new Thread(st);
                    t_server.start();
                    serverOn = true;
                } else
                    System.out.println("server already launched");
            } else 
                if (input.equals("new client")){
                    ct = new ClientThread();
                    t_client = new Thread(ct);
                    t_client.start();
                } else {
                    System.out.println("invalid input... retry...");
                }
            input = stdIn.readLine();
        }
        
        t_server.interrupt();
        t_client.interrupt();

    }
}
