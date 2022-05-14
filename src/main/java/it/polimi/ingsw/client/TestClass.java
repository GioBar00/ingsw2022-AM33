package it.polimi.ingsw.client;

import it.polimi.ingsw.client.CLI.CLI;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.IslandGroupView;
import it.polimi.ingsw.network.messages.views.IslandView;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Scanner;

public class TestClass {
    static BufferedReader stdIn;
    static GameView gameView;

    static String nickname = "0";
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        if(input.equals("s")){
            System.out.println("Starting a server");
            Server server = new Server();
            server.handleRequest();
        }
        else {
            System.out.println("Starting a client");
            UI userInterface = new CLI();
            userInterface.showStartScreen();
        }
    }

}
