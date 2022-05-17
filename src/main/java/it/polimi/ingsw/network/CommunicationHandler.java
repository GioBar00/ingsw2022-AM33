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
public class CommunicationHandler implements DisconnectListenerSubscriber {

    /**
     * The message handler.
     */
    private MessageHandler messageHandler;

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
    private CountDownLatch latch;

    /**
     * The timer used to send ping messages.
     */
    private Timer timer;

    /**
     * Constructor.
     *
     * @param isMaster if true, pings will be sent to ensure that the two parties are connected.
     */
    public CommunicationHandler(boolean isMaster) {
        this((Message m) -> {
        }, isMaster);
    }

    /**
     * Constructor.
     *
     * @param messageHandler the message handler
     */
    public CommunicationHandler(MessageHandler messageHandler) {
        this(messageHandler, false);
    }

    /**
     * Constructor.
     *
     * @param messageHandler the message handler
     * @param isMaster       if true, pings will be sent to ensure that the two parties are connected.
     */
    public CommunicationHandler(MessageHandler messageHandler, Boolean isMaster) {
        this.messageHandler = messageHandler;
        queue = new LinkedBlockingQueue<>();
        executor = Executors.newSingleThreadExecutor();
        executor.shutdownNow();
        this.isMaster = isMaster;
    }

    /**
     * This method is used to set the socket.
     *
     * @param socket the socket to set
     */
    public synchronized void setSocket(Socket socket) {
        if (executor != null) {
            System.out.println("CH : setSocket closing old socket");
            stop();
        }
        this.socket = socket;
        if (!socket.isClosed()) {
            System.out.println("CH : setSocket set new socket");
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Getter
     *
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * This method is used to set the message handler.
     *
     * @param messageHandler the message handler to set
     */
    public synchronized void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * @return if the connection is still alive.
     */
    public synchronized boolean isConnected() {
        return socket != null && !socket.isClosed() && !executor.isShutdown();
    }

    /**
     * For tests.
     *
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
                Message m = null;
                try {
                    m = queue.take();
                } catch (InterruptedException e) {
                    if (isMaster) System.out.println("CH : output interrupted");
                }
                MessageExchange.sendMessage(m, writer);
            }
            System.out.println("CH : output stop");
        } catch (IOException e) {
            System.out.println("CH : handleOutput IOException");
        }
    }

    /**
     * This method reads the messages.
     */
    private void handleInput() {
        try {
            while (!Thread.interrupted()) {
                Message message = MessageExchange.receiveMessage(reader, (event) -> {
                    notifyDisconnectListener(new DisconnectEvent(this));
                    System.out.println("CH : handleInput stop");
                    stop();
                });
                if (message != null) {
                    if (message.isValid()) {
                        if (isMaster && !checkPong(message) || !isMaster && !checkPing(message)) {
                            synchronized (this) {
                                if (messageHandler != null) messageHandler.handleMessage(message);
                            }
                        }
                    } else if (isMaster) sendMessage(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE));
                }
            }
        } catch (IOException e) {
            System.out.println("CH : handleInput IOException");
        }
    }

    /**
     * This method checks if the message is a ping and sends a pong if it is.
     *
     * @param message the message to check
     * @return if the message is a ping
     */
    private boolean checkPing(Message message) {
        if (MessageType.retrieveByMessage(message) == MessageType.COMM_MESSAGE)
            if (((CommMessage) message).getType() == CommMsgType.PING) {
                sendMessage(new CommMessage(CommMsgType.PONG));
                latch.countDown();
                return true;
            }
        return false;
    }

    /**
     * This method checks if the message is a pong and acknowledges if it is.
     *
     * @param message the message to check
     * @return if the message is a pong
     */
    private boolean checkPong(Message message) {
        if (MessageType.retrieveByMessage(message) == MessageType.COMM_MESSAGE)
            if (((CommMessage) message).getType() == CommMsgType.PONG) {
                latch.countDown();
                return true;
            }
        return false;
    }

    /**
     * This method starts the timer to check if the connection is alive.
     */
    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                latch = new CountDownLatch(1);
                long seconds = isMaster ? 10 : 20;
                try {
                    if (isMaster) sendMessage(new CommMessage(CommMsgType.PING));
                    if (!latch.await(seconds, TimeUnit.SECONDS)) {
                        System.out.println("CH : timer Stop");
                        stop();
                        notifyDisconnectListener(new DisconnectEvent(this));
                    }

                } catch (InterruptedException ignored) {
                    System.out.println("CH : timer InterruptedException");
                }
            }
        }, 0, 5 * 1000);
    }

    /**
     * This method starts the message exchange handler.
     */
    public synchronized void start() {
        if (socket == null) return;
        if (socket.isClosed()) System.out.println("MessageExchangeHandler: Socket is closed");
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newFixedThreadPool(2);
            executor.submit(this::handleInput);
            executor.submit(this::handleOutput);
            startTimer();
            System.out.println("CH : start");
        }
    }

    /**
     * This method stops the message exchange handler closing the socket.
     */
    public synchronized void stop() {
        stop(true);
    }

    /**
     * This method stops the message exchange handler.
     *
     * @param closeSocket if the socket should be closed
     */
    public synchronized void stop(boolean closeSocket) {
        if (!executor.isShutdown()) {
            System.out.println("CH : stop");
            timer.cancel();
            executor.shutdownNow();
        }
        if (closeSocket) {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method adds a message to the queue.
     *
     * @param message the message to send
     */
    public void sendMessage(Message message) {
        queue.add(message);
        if (!(MessageType.retrieveByMessage(message) == MessageType.COMM_MESSAGE && (((CommMessage) message).getType() == CommMsgType.PONG || ((CommMessage) message).getType() == CommMsgType.PING)))
            System.out.println("CH: Added to queue - " + MessageBuilder.toJson(message));
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
    public void notifyDisconnectListener(DisconnectEvent event) {
        queue.clear();
        if (disconnectListener != null) {
            System.out.println("CH : notifyDisconnectListener");
            disconnectListener.onDisconnect(event);
        }

    }
}
