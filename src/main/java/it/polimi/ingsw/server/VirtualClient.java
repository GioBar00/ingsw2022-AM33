package it.polimi.ingsw.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.SkipTurn;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.listeners.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class for handle connection between a specified player and the server. Virtual Client forward valid request to the
 * controller
 */
public class VirtualClient extends ConcreteMessageListenerSubscriber implements MessageListener {

    /**
     * Nickname of the player who interfaces this VirtualClient
     */
    private final String identifier;

    /**
     * Socket of the connection
     */
    private Socket socket;

    /**
     * input scanner for incoming messages
     */
    private Scanner in;

    /**
     * output printer for outgoing messages
     */
    private PrintWriter out;

    /**
     * Thread executor used for
     */
    private final ExecutorService executor;

    /**
     * A queue for the messages to send
     */
    private final LinkedBlockingQueue<Message> queue;

    /**
     * Timer for checking if the connection is still alive
     */
    private final Timer timer;

    /**
     * Value for checking the time validity of a reply
     */
    private Boolean notAlive = false;

    /**
     * Used for know if the client is connected
     */
    private Boolean isActive;

    /**
     * lock associated at the boolean notAlive
     */
    private final Object lock;

    /**
     * lock associated at the boolean notAlive
     */
    private final Object lock2;


    /**
     * Constructor of VirtualClient
     * @param identifier the nickname of the player that interfaces with this VirtualClient
     */
    public VirtualClient(String identifier) {
        this.identifier = identifier;
        queue = new LinkedBlockingQueue<>();
        timer = new Timer();
        this.executor = Executors.newFixedThreadPool(2);
        isActive = false;
        lock = new Object();
        lock2 = new Object();
    }

    /**
     * Method used for know if the client is still connected
     * @return true if is connected, false in other case
     */
    public boolean getStatus() {
        synchronized (lock2) {
            return isActive;
        }
    }

    /**
     * Adding a socket to the virtualClient. Used in case of creation or reconnection
     * @param socket the socket of the communication
     */
    public void addSocket(Socket socket) {
        this.socket = socket;
        boolean open = false;
        do {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream());
                open = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while(!open);
        synchronized (lock2){
            isActive = true;
        }
    }

    /**
     * Method for start the in and the out communications
     */
    public void startVirtualClient() {
        executor.submit(this :: messagesHandler);
        executor.submit(this :: startOutput);
        startTimer();
        isActive = true;
    }


    /**
     * Private method for setting up the output communication
     */
    private void startOutput() {
        Message m;
        try {
            m = queue.take();
            out.println(MessageBuilder.toJson(m));
            out.flush();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Private method used for checking if the connection is alive
     */
    private void startTimer(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (lock) {
                    notAlive = true;
                }
                try {
                    queue.put(new CommMessage(CommMsgType.CONNECTION_ALIVE));
                    wait(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock) {
                    if (notAlive) {
                        closeConnection();
                    }
                }
            }
        },0,10* 1000);
    }


    /**
     * Identifier getter
     * @return the nickname
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Handles the incoming messages. Checks if the message is valid. If the message is valid notify the listener
     */
    private void messagesHandler() {
        String line;
        while(true){
            line = in.nextLine();
            synchronized (lock){
                notAlive = false;
            }
            Message m = MessageBuilder.fromJson(line);
            if(m.isValid()){
                if (MessageType.retrieveByMessageClass(m) == MessageType.COMM_MESSAGE) {
                    if (((CommMessage) m).getType() != CommMsgType.OK) {
                        notifyListeners(new MessageEvent(this, m));
                    }
                } else {
                    notifyListeners(new MessageEvent(this, m));
                }
            }
            else sendInvalidMessage();
        }

    }

    /**
     * Send a Communication Message(ERROR_NOT_YOUR_TURN) to the client
     */
    public void sendNotYourTurnMessage(){
        queue.add(new CommMessage(CommMsgType.ERROR_NOT_YOUR_TURN));
    }

    /**
     * Send a Communication Message(ERROR_INVALID_MESSAGE) to the client
     */
    private void sendInvalidMessage() {
            queue.add(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE));
    }

    /**
     * Send a Communication Message(ERROR_IMPOSSIBLE_MOVE) to the client
     */
    public void sendImpossibleMessage() {
            queue.add(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
    }

    /**
     * Close the connection socket and send notify to Server
     */
    void closeConnection() {
        synchronized (lock2) {
            isActive = false;
        }
        timer.cancel();
        executor.shutdown();
        in.close();
        out.close();
        boolean isClosed = false;
        do {
            try {
                socket.close();
                isClosed = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while(!isClosed);
    }

    /**
     * Methods from MessageListener Interface for notify to the VirtualClient changes in the model
     * @param event of the received message
     */
    @Override
    public void onMessage(MessageEvent event) {
        if(!isActive)
            notifyListeners(new MessageEvent(this, new SkipTurn()));
        else{queue.add(event.getMessage());}
    }

}

