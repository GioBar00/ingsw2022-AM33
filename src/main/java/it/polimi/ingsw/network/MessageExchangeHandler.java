package it.polimi.ingsw.network;

import it.polimi.ingsw.network.listeners.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * This class is used to handle the message exchange between the server and the client and vice versa.
 */
public class MessageExchangeHandler implements DisconnectListenerSubscriber {

    /**
     * The message handler.
     */
    private final MessageHandler messageHandler;

    /**
     * The socket used to exchange messages.
     */
    private Socket socket;

    /**
     * The reader used to read messages.
     */
    private BufferedReader reader;

    /**
     * The writer used to write messages.
     */
    private BufferedWriter writer;

    /**
     * The queue used to send messages.
     */
    private final LinkedBlockingQueue<Message> queue;

    /**
     * The executor used to handle the message exchange.
     */
    private ExecutorService executor;

    /**
     * Listener used to handle the disconnection.
     */
    private DisconnectListener disconnectListener;

    /**
     * If true, ping and error messages are sent.
     */
    private final Boolean isMaster;

    /**
     * The latch used to check if the pong has been received.
     */
    private CountDownLatch clientAcknowledged;

    /**
     * The timer used to send ping messages.
     */
    private Timer timer;

    /**
     * Constructor.
     *
     * @param messageHandler the message handler
     */
    public MessageExchangeHandler(MessageHandler messageHandler) {
        this(messageHandler, false);
    }

    /**
     * Constructor.
     *
     * @param messageHandler the message handler
     * @param isMaster if true, pings will be sent to ensure that the two parties are connected.
     */
    public MessageExchangeHandler(MessageHandler messageHandler, Boolean isMaster) {
        this.messageHandler = messageHandler;
        queue = new LinkedBlockingQueue<>();
        executor = Executors.newSingleThreadExecutor();
        executor.shutdownNow();
        this.isMaster = isMaster;
    }

    /**
     * This method is used to set the socket.
     * @param socket the socket to set
     */
    public synchronized void setSocket(Socket socket) {
        if (executor != null)
            stop();
        this.socket = socket;
        if (!socket.isClosed()) {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return if the connection is still alive.
     */
    public synchronized boolean isConnected() {
        return socket != null && !socket.isClosed() && !executor.isShutdown();
    }

    /**
     * @return if it is the master.
     */
    public boolean isMaster() {
        return isMaster;
    }

    /**
     * For tests.
     * @return the queue
     */
    public List<Message> getQueue() {
        return new ArrayList<>(queue);
    }

    /**
     * For tests.
     */
    public void clearQueue() {
        queue.clear();
    }

    /**
     * This method sends the messages in the queue.
     */
    private void handleOutput() {
        try {
            while (!Thread.interrupted()) {
                Message m = queue.take();
                writer.write(MessageBuilder.toJson(m));
                writer.newLine();
                writer.flush();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method reads the messages.
     */
    private void handleInput() {
        try {
            while (!Thread.interrupted()) {

                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();

                while (reader.ready() && line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }

                if (line == null) {
                    notifyListener(new DisconnectEvent(this));
                    stop();
                }

                builder.append(line);
                line = builder.toString();

                Message message = MessageBuilder.fromJson(line);
                if (message.isValid()) {
                    if (isMaster && !checkPong(message) || !isMaster && !checkPing(message)) {
                        messageHandler.handleMessage(message);
                    }
                } else if (isMaster)
                    sendInvalidMessage();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks if the message is a ping and sends a pong if it is.
     * @param message the message to check
     * @return if the message is a ping
     */
    private boolean checkPing(Message message) {
        if (MessageType.retrieveByMessage(message) == MessageType.COMM_MESSAGE)
            if (((CommMessage) message).getType() == CommMsgType.PING) {
                queue.add(new CommMessage(CommMsgType.PONG));
                return true;
            }
        return false;
    }

    /**
     * This method checks if the message is a pong and acknowledges if it is.
     * @param message the message to check
     * @return if the message is a pong
     */
    private boolean checkPong(Message message) {
        if (MessageType.retrieveByMessage(message) == MessageType.COMM_MESSAGE)
            if (((CommMessage) message).getType() == CommMsgType.PONG) {
                clientAcknowledged.countDown();
                return true;
            }
        return false;
    }

    /**
     * Send a Communication Message(ERROR_INVALID_MESSAGE) to the client
     */
    private void sendInvalidMessage() {
        queue.add(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE));
    }

    /**
     * This method starts the timer to check if the connection is alive.
     */
    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                clientAcknowledged = new CountDownLatch(1);
                try {
                    queue.add(new CommMessage(CommMsgType.PING));
                    if (!clientAcknowledged.await(10, TimeUnit.SECONDS)) {
                        stop();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },0,5 * 1000);
    }

    /**
     * This method starts the message exchange handler.
     */
    public synchronized void start() {
        if (socket == null)
            return;
        if (socket.isClosed())
            System.out.println("MessageExchangeHandler: Socket is closed");
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newFixedThreadPool(2);
            executor.submit(this::handleInput);
            executor.submit(this::handleOutput);
            if (isMaster)
                startTimer();
        }
    }

    /**
     * This method stops the message exchange handler.
     */
    public synchronized void stop() {
        if (!executor.isShutdown()) {
            if (isMaster)
                timer.cancel();
            executor.shutdownNow();
            try {
                reader.close();
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method adds a message to the queue.
     * @param message the message to send
     */
    public void sendMessage(Message message) {
        queue.add(message);
    }

    /**
     * Sets the disconnection listener.
     *
     * @param listener the listener to set
     */
    @Override
    public void setDisconnectListener(DisconnectListener listener) {
        disconnectListener = listener;
    }

    /**
     * Notifies the listener that a disconnection has occurred.
     *
     * @param event the event to notify
     */
    @Override
    public void notifyListener(DisconnectEvent event) {
        if (disconnectListener != null)
            disconnectListener.onDisconnect(event);
    }
}
