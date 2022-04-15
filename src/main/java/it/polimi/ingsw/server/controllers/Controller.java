package it.polimi.ingsw.server.controllers;

import it.polimi.ingsw.server.listeners.MessageEvent;
import it.polimi.ingsw.server.listeners.MessageListener;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameBuilder;
import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.GameState;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller class manages the first request for each client. If the request is valid instantiates a client handler.
 * Each client handler request to the Controller class specified methods for updating the model. Controller verifies the validity
 * of the request and calls the correct method. Provide an updated view for each client
 */
public class Controller implements MessageListener {

    /**
     * the instance of the game model
     */
    private Game model;

    /**
     * a collection of player nickname and their request handler
     */
    private final HashMap<String, VirtualClient> virtualClients;

    /**
     * tcp port of the controller
     */
    private final int port;

    /**
     * the first player who has the rights to start the match
     */
    private String master;

    /**
     * Default constructor of class Controller
     */
    public Controller(){
        model = null;
        virtualClients = new HashMap<>();
        port = 0;
        master = null;
    }

    public void handleRequest() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Server ready");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                String nickname = handleFirstConnection(socket);
                if(nickname != null) {
                    boolean openVirtualClient = false;
                    do {
                        try {
                            VirtualClient vc = new VirtualClient(this, nickname, socket);
                            virtualClients.put(nickname, vc);
                            openVirtualClient = true;
                            executor.submit(vc);
                        } catch (IOException ignored) {
                        }
                    } while (!openVirtualClient);
                }
            } catch(IOException e) {
                break;
            }

        }
        executor.shutdown();
    }

    private String handleFirstConnection(Socket socket) {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String line = in.nextLine();

            Message message = MessageBuilder.fromJson(line);
            if (MessageType.retrieveByMessageClass(message).equals(MessageType.LOGIN)) {
                Login mex = (Login) message;

                String nickname = mex.getNickname();
                //nickname not null
                if (mex.isValid()) {
                    out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_NULL_NICKNAME)));
                    out.flush();
                    socket.close();
                    return null;
                }
                //model didn't exist
                if (model == null) {
                    out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.CHOOSE_NUM_PLAYERS)));
                    out.flush();

                    line = in.nextLine();
                    message = MessageBuilder.fromJson(line);
                    if (MessageType.retrieveByMessageClass(message).equals(MessageType.CHOSEN_TEAM)) {
                        ChosenGame choice = (ChosenGame) message;
                        if (choice.isValid()) {
                            do {
                                model = GameBuilder.getGame(choice.getPreset(), choice.getMode());
                            }
                            while (!model.addPlayer(nickname));
                            master = nickname;
                            return nickname;
                        }

                    }
                    out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE)));
                    out.flush();
                    socket.close();
                    return null;
                }

                //model exists
                if (model.addPlayer(nickname)) {
                    return nickname;
                } else {
                    out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_NICKNAME_UNAVAILABLE)));
                    out.flush();
                    socket.close();
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    int playCard(String nickname, AssistantCard a) {
        if(canPlay(nickname))
            return -1;
        synchronized (this){
            if (model.playAssistantCard(a)) {
                updateView();
                return 1;
            }
            return 0;
        }

    }

    int moveStudentToHall(String nickname, int entranceIndex) {
        if(canPlay(nickname))
            return -1;
        synchronized (this){
            if (model.moveStudentToHall(entranceIndex)) {
                updateView();
                return 1;
            }
            return 0;
        }
    }

    int moveStudentToIsland(String nickname, int entranceIndex, int islandIndex) {
        if(canPlay(nickname))
            return -1;
        synchronized (this){
            if (model.moveStudentToIsland(entranceIndex, islandIndex)) {
                updateView();
                return 1;
            }
            return 0;
        }
    }

    int moveMotherNature(String nickname, int nMoves) {
        if(canPlay(nickname))
            return -1;
        synchronized (this){
            if (model.moveMotherNature(nMoves)) {
                updateView();
                return 1;
            }
            return 0;
        }
    }

    int chooseCloud(String nickname, int index) {
        if(canPlay(nickname))
            return -1;
        synchronized (this){
            if(model.getStudentsFromCloud(index)){
                updateView();
                return 1;
            }
            return 0;
        }
    }


    int startGame(String nickname) {
        if(nickname.equals(master)){
            if(model.startGame())
                return 1;
        }
        return -1;
    }

    synchronized boolean changeTeam(String nickname, Tower tower) {
        return  model.changeTeam(nickname,tower);
    }

    int playCharacterCard(String nickname, int cardIndex) {
        if(canPlay(nickname))
            return -1;
        synchronized (this){
            if (model.activateCharacterCard(cardIndex)) {
                updateView();
                return 1;
            }
            return 0;
        }
    }

    int applyEffect(String nickname, CharacterParameters parameters) {
        if(canPlay(nickname))
            return -1;
        synchronized (this){
            if (model.applyEffect(parameters)) {
                updateView();
                return 1;
            }
            return 0;
        }
    }

    int concludeCharacterCardEffect(String nickname) {
        if(canPlay(nickname))
            return -1;
        synchronized (this){
            if(model.endEffect())
                return 1;
            else return 0;
        }
    }

    //TODO add methods model and virtualClient
    void updateView() {
        //model.get
        for(VirtualClient vc : virtualClients.values()){
            //todo
        }
        //vc.sendView();
    }

    private boolean canPlay(String nickname) {
        return !nickname.equals(model.getCurrentPlayer());
    }


    @Override
    public void onMessage(MessageEvent event) {
        VirtualClient c = (VirtualClient) event.getSource();
        Message msg = event.getMessage();

        switch (model.getGameState()) {
            case UNINITIALIZED:
                handleGameSetup(event);
            case STARTED:
//                switch (model.getPhase()) {
//
//                }
        }


    }

    void handleGameSetup(MessageEvent event) {
        Message msg = event.getMessage();
        switch (MessageType.retrieveByMessageClass(msg)) {
            case CHOSEN_TEAM -> {
                //TODO
                break;
            }
            // default:
        }
    }
}
