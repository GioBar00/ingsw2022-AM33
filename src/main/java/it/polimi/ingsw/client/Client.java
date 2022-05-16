package it.polimi.ingsw.client;

import it.polimi.ingsw.client.CLI.CLI;
import it.polimi.ingsw.client.GUI.GUI;
import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.network.MessageHandler;
import it.polimi.ingsw.network.listeners.DisconnectEvent;
import it.polimi.ingsw.network.listeners.DisconnectListener;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.CurrentGameState;
import it.polimi.ingsw.network.messages.server.CurrentTeams;
import javafx.application.Application;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class for client-side communication
 */
public class Client implements MessageHandler, ViewListener, Runnable , DisconnectListener {

    /**
     * the hostname for the connection
     */
    private String hostname;

    /**
     * the port for the connection
     */
    private int port;

    private String nickname;

    /**
     * The message exchange handler used to send and receive messages to and from the client
     */
    private final CommunicationHandler communicationHandler;

    /**
     * The interface for the user
     */
    private final UI userInterface;

    /**
     * Queue with arrived messages
     */
    private final LinkedBlockingQueue<Message> queue;

    /**
     * The executor used to handle the message exchange.
     */
    private ExecutorService executor;

    /**
     * Constructor of Virtual Server
     */
    public Client(boolean gui) {
        executor = Executors.newSingleThreadExecutor();
        executor.shutdownNow();
        queue = new LinkedBlockingQueue<>();
        this.communicationHandler = new CommunicationHandler(this);
        if (gui) {
            Application.launch(GUI.class);
            userInterface = GUI.getInstance();
            if (userInterface == null) {
                System.out.println("FATAL ERROR: unable to instantiate GUI");
                System.exit(1);
            }
        } else
            userInterface = new CLI();
        userInterface.setClient(this);
        userInterface.setViewListener(this);
    }

    public void startClient(){
        userInterface.showStartScreen();
    }

    public boolean setServerAddress(String hostname){
        this.hostname = hostname;
        return true;
        /*
        if(hostname.toLowerCase().equals("localhost")){
            this.hostname = hostname;
            return true;
        }

        String [] add = hostname.split(".");

        for(String i: add){
            System.out.println(i);
        }

        if(add.length != 4)
            return false;
        int val;
        for(String i : add){
            if(i.matches("-?\\d+")){
                val = Integer.parseInt(i);
                if(val < 0 || val > 255)
                    return false;
            }
            else{ return false;}
        }
        this.hostname = hostname;
        return true;

         */
    }

    public boolean setServerPort(String port){
        if(port.matches("-?\\d+")){
            int portValue = Integer.parseInt(port);
            if(portValue < 1024 || portValue > 65535)
                return false;
            this.port = portValue;
            return true;
        }
        return false;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    /**
     * This method sets up the connection and starts the communicationHandler.
     */
    public void startConnection(){
        try {
            communicationHandler.setSocket(new Socket(hostname, port));
            communicationHandler.setDisconnectListener(this);
            communicationHandler.start();
            if(executor.isShutdown()) {
                executor = Executors.newSingleThreadExecutor();
                executor.submit(this);
            }
        } catch (IOException e) {
            userInterface.serverUnavailable();
            closeConnection();
        }
    }

    /**
     * This method is called when a message is received.
     * @param message the message received.
     */
    @Override
    public void handleMessage(Message message) {
        System.out.println("C: received message - " + MessageBuilder.toJson(message));
        queue.add(message);
    }

    /**
     * Method called when the user want to update the model.
     * @param message the request.
     */
    @Override
    public void onMessage(Message message) {
        System.out.println("C: sending message - " + MessageBuilder.toJson(message));
        communicationHandler.sendMessage(message);
    }

    public boolean sendLogin(){
        if(nickname != null && hostname != null && port != 0) {
            startConnection();
            onMessage(new Login(nickname));
            return true;
        }
        return false;
    }
    /**
     * Task for the Client.
     * Takes the messages from the model and apply the changes to the view.
     */
    @Override
    public void run() {
        Message message;
        while(!Thread.interrupted()){
            try {
                message = queue.take();
                System.out.println("CL : " + message.getClass().getName());
                updateView(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Method for updating the view based on the received  messages.
     * @param message a Message from the model.
     */
    public synchronized void updateView(Message message){
        System.out.println(MessageBuilder.toJson(message));

        switch (MessageType.retrieveByMessage(message)){
            case COMM_MESSAGE -> {
                switch (((CommMessage)message).getType()){
                    case CHOOSE_GAME -> userInterface.chooseGame();
                    case CAN_START -> userInterface.hostCanStart();
                    case ERROR_CANT_START -> userInterface.hostCantStart();
                    case ERROR_TIMEOUT, ERROR_SERVER_UNAVAILABLE -> userInterface.serverUnavailable();
                    default -> userInterface.showCommMessage((CommMessage)message);
                }
            }
            case AVAILABLE_WIZARDS -> {
                userInterface.setWizardView(((AvailableWizards)message).getWizardsView());
                userInterface.showWizardMenu();
            }
            case CURRENT_TEAMS -> userInterface.setTeamsView(((CurrentTeams)message).getTeamsView());
            case PLAY_ASSISTANT_CARD, MULTIPLE_POSSIBLE_MOVES, CHOOSE_CLOUD, CHOOSE_ISLAND, CHOOSE_STUDENT_COLOR, MOVE_MOTHER_NATURE, MOVE_STUDENT, SWAP_STUDENTS ->
                    userInterface.setPossibleMoves(message);

            case CURRENT_GAME_STATE -> {
                userInterface.setGameView(((CurrentGameState)message).getGameView());
                userInterface.showGameScreen();
            }
        }
    }

    /**
     * End the connection
     */
    public void closeConnection(){
        communicationHandler.stop();
    }

    /**
     * Invoked when a client disconnects from the server and vice versa.
     *
     * @param event the event object
     */
    @Override
    public void onDisconnect(DisconnectEvent event) {
        closeConnection();
        userInterface.serverUnavailable();
        executor.shutdownNow();
    }
}
