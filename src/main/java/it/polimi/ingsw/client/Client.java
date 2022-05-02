package it.polimi.ingsw.client;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.listeners.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends ConcreteMessageListenerSubscriber implements MessageListener{

    private final String id;

    private final Socket socket;

    private final String hostname;

    private final int port;

    private BufferedReader input;

    private PrintWriter output;

    private final ExecutorService executor;

    private final LinkedBlockingQueue<Message> queue;

    private final Object lock;

    public Client(String id, String hostname, int port) throws IOException {
        this.id = id;
        this.port = port;
        this.hostname = hostname;
        System.out.println("C: creating connection");
        this.socket = new Socket(hostname, port);
        System.out.println("C: socket up");
        this.executor = Executors.newFixedThreadPool(2);
        queue = new LinkedBlockingQueue<>();
        lock = new Object();
        boolean start_io = false;
        do {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream());
                start_io = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while(!start_io);
        executor.submit(this :: inHandler);
        executor.submit(this :: outHandler);
        System.out.println("C: client ready");
    }

    private void outHandler() {
        Message m;
        try {
            m = queue.take();
            output.println(MessageBuilder.toJson(m));
            output.flush();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void putInQueue(Message m){
        queue.add(m);
    }

    private void inHandler(){
        try {
            while (true) {
                StringBuilder builder = new StringBuilder();

                String line = input.readLine();

                while (input.ready() && line != null) {
                    builder.append(line);
                    line = input.readLine();
                }

                builder.append(line);
                line = builder.toString();


                Message m = MessageBuilder.fromJson(line);
                if (m.isValid()) {
                    if (MessageType.retrieveByMessageClass(m) == MessageType.COMM_MESSAGE) {
                        if (((CommMessage) m).getType() != CommMsgType.OK) {
                            System.out.println(id + " received a communication message:\n" + MessageBuilder.toJson(m));
                            System.out.println(((CommMessage) m).getType().getMessage());
                            notifyListeners(new MessageEvent(this, m));
                        } else {
                            System.out.println(id + " received ack");
                            notifyListeners(new MessageEvent(this, m));
                        }
                    } else {
                        System.out.println(id + " received a message:" + MessageBuilder.toJson(m));
                        notifyListeners(new MessageEvent(this, m));
                    }
                } else {
                    System.out.println(id + ": message error");
                    sendInvalidMessage();
                }

            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendInvalidMessage(){
        queue.add(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE));
    }

    public void closeConnection() {
        try {
            executor.shutdownNow();
            input.close();
            output.close();
            boolean isClosed = false;
            do {
                try {
                    socket.close();
                    isClosed = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (!isClosed);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void onMessage(MessageEvent event) {
        queue.add(event.getMessage());
    }
}
