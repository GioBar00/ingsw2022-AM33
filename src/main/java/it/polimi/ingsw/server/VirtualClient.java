package it.polimi.ingsw.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.controller.CallableTimerTask;
import it.polimi.ingsw.server.listeners.*;
import it.polimi.ingsw.server.timer.MyTimer;
import it.polimi.ingsw.server.timer.VirtualClientStopConnection;
import it.polimi.ingsw.server.timer.VirtualClientTimeout;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class for handle connection between a specified player and the server. Virtual Client forward valid request to the
 * controller
 */
public class VirtualClient extends ConcreteMessageListenerSubscriber implements Runnable, MessageListener, CallableTimerTask {

    /**
     * Nickname of the player who interfaces this VirtualClient
     */
    private final String identifier;

    /**
     * Socket of the connection
     */
    private final Socket socket;

    /**
     * input scanner for incoming messages
     */
    private final Scanner in;

    /**
     * output printer for outgoing messages
     */
    private final PrintWriter out;

    /**
     * Value for checking the time validity of a reply
     */
    private boolean notAlive = false;

    /**
     * Listener used in case of lost connection
     */
    private ConnectionListener connectionListener;


    /**
     * Constructor of VirtualClient
     * @param identifier the nickname of the player that interfaces with this VirtualClient
     * @param socket socket of the connection
     * @param connectionListener the listener of ConnectionEvent
     */
    public VirtualClient(String identifier, Socket socket, ConnectionListener connectionListener) throws IOException {
        this.identifier = identifier;
        this.socket = socket;

        in = new Scanner(this.socket.getInputStream());
        out = new PrintWriter(this.socket.getOutputStream());


        this.connectionListener = connectionListener;
    }

    /**
     * Removes the ConnectionListener bonded to this class
     */
    public void removeListener(){
        this.connectionListener = null;
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
     * Set if the connection time is no more valid
     */
    public void setNotAlive() {
        notAlive = true;
    }

    /**
     * Override Method from runnable interface
     */
    @Override
    public void run() {
        messagesHandler();
    }

    /**
     * Handles the incoming messages. Checks if the message is valid. If the message is valid notify the listener
     */
    private void messagesHandler() {
        MyTimer timer = new MyTimer(new VirtualClientTimeout(this, 60));
        String line;
        while(true){
            timer.startTimer();
            synchronized (in){
                line = in.nextLine();
            }
            timer.stopTask();
            Message m = MessageBuilder.fromJson(line);
            if(m.isValid()){
                notifyListeners(new MessageEvent(this, m));
            }
            else sendInvalidMessage();
        }

    }

    /**
     * Send a Communication Message(ERROR_NOT_YOUR_TURN) to the client
     */
    public void sendNotYourTurnMessage(){
        out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_NOT_YOUR_TURN)));
        out.flush();
    }

    /**
     * Send a Communication Message(ERROR_INVALID_MESSAGE) to the client
     */
    private void sendInvalidMessage() {
        synchronized (out) {
            out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE)));
            out.flush();
        }
    }

    /**
     * Send a Communication Message(ERROR_IMPOSSIBLE_MOVE) to the client
     */
    public void sendImpossibleMessage() {
        synchronized (out) {
            out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE)));
            out.flush();
        }
    }

    /**
     * Called by the TimeTask for checking if the client is still alive.
     * Send a request message to the client and wait for a reply. If the reply didn't arrive the VirtualClient close the
     * connection and notify the Server
     */
    public void endOfTime() {
        MyTimer timer = new MyTimer(new VirtualClientStopConnection(this, 60));
        String line;
        synchronized (in){
            synchronized (out) {
                out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.CONNECTION_ALIVE)));
                out.flush();

                timer.startTimer();

                do {
                    line = in.nextLine();
                    if (line != null) {
                        timer.stopTask();
                        timer.killTimer();
                        return;
                    }
                } while (!notAlive);

                timer.killTimer();


                in.close();
                out.close();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                connectionListener.onConnectionEvent(new ConnectionEvent(this));
            }
        }

    }

    //TODO
    @Override
    public void onMessage(MessageEvent event) {

    }


}

