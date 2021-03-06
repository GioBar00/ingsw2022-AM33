package it.polimi.ingsw.network;

import it.polimi.ingsw.network.listeners.DisconnectEvent;
import it.polimi.ingsw.network.listeners.DisconnectListener;
import it.polimi.ingsw.network.listeners.DisconnectListenerSubscriber;
import it.polimi.ingsw.network.messages.IgnoreMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * The timer used to send ping messages.
     */
    private Timer timer;

    /**
     * If the communication handler is stopped.
     */
    private volatile boolean stopped = true;

    /**
     * if the disconnection was already notified.
     */
    private boolean notifiedDisconnect = false;

    /**
     * Lock used to notify the disconnection.
     */
    private final Object disconnectLock = new Object();

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
        this.isMaster = isMaster;
    }

    /**
     * This method is used to set the socket.
     *
     * @param socket the socket to set
     */
    public synchronized void setSocket(Socket socket) {
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
        return socket != null && !socket.isClosed() && !stopped;
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
            while (!stopped) {
                Message m = null;
                try {
                    m = queue.take();
                } catch (InterruptedException e) {
                    if (isMaster) notifyDisconnectAndStop();
                }
                if (m != null && MessageType.retrieveByMessage(m) != MessageType.IGNORE)
                    MessageExchange.sendMessage(m, writer);
                if (queue.isEmpty()) {
                    synchronized (this) {
                        notify();
                    }
                }
            }
            notifyDisconnectAndStop();
        } catch (IOException e) {
            notifyDisconnectAndStop();
        }
    }

    /**
     * This method reads the messages.
     */
    private void handleInput() {
        try {
            while (!stopped) {
                Message message = MessageExchange.receiveMessage(reader, (event) -> notifyDisconnectAndStop());
                if (message != null) {
                    if (message.isValid()) {
                        if (isMaster && !checkPong(message) || !isMaster && !checkPing(message)) {
                            if (messageHandler != null) messageHandler.handleMessage(message);
                        }
                    } else if (isMaster) sendMessage(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE));
                }
            }
            notifyDisconnectAndStop();
        } catch (IOException e) {
            notifyDisconnectAndStop();
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
                        notifyDisconnectAndStop();
                    }

                } catch (InterruptedException ignored) {
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
        if (stopped) {
            notifiedDisconnect = false;
            stopped = false;
            queue.clear();
            new Thread(this::handleInput).start();
            new Thread(this::handleOutput).start();
            startTimer();
        }
    }

    /**
     * This method stops the message exchange handler closing the socket.
     */
    public synchronized void stop() {
        if (!stopped) {
            stopped = true;
            disconnectListener = e -> {};
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            queue.add(new IgnoreMessage());
            try {
                if (socket != null) socket.close();
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends the last message to the client and stops the communication handler.
     *
     * @param message the message to send
     */
    public synchronized void sendLastMessage(Message message) {
        disconnectListener = event -> {
        };
        stopped = true;
        try {
            MessageExchange.sendMessage(message, writer);
        } catch (IOException ignored) {
        }
        stopped = false;
        stop();
    }

    /**
     * This method adds a message to the queue.
     *
     * @param message the message to send
     */
    public void sendMessage(Message message) {
        queue.add(message);
    }

    /**
     * This method notifies the disconnect listener if it was not already notified.
     */
    private void notifyDisconnectIfNotAlreadyDone() {
        synchronized (disconnectLock) {
            if (!notifiedDisconnect) {
                notifiedDisconnect = true;
                notifyDisconnectListener(new DisconnectEvent(this));
            }
        }
    }

    /**
     * Notifies the disconnect listener and stops the communication handler.
     */
    private void notifyDisconnectAndStop() {
        notifyDisconnectIfNotAlreadyDone();
        if (!stopped) stop();
    }

    /**
     * Sets the disconnection listener.
     *
     * @param listener the listener to set
     */
    @Override
    public synchronized void setDisconnectListener(DisconnectListener listener) {
        disconnectListener = listener;
    }

    /**
     * Notifies the listener that a disconnection has occurred.
     *
     * @param event the event to notify
     */
    @Override
    public void notifyDisconnectListener(DisconnectEvent event) {
        DisconnectListener listener = disconnectListener;
        if (listener != null) {
            listener.onDisconnect(event);
        }
    }
}
