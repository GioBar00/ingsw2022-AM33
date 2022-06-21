package it.polimi.ingsw.server;

import it.polimi.ingsw.network.CommunicationHandler;
import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.Winners;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.enums.ServerState;
import it.polimi.ingsw.server.listeners.EndGameEvent;
import it.polimi.ingsw.server.listeners.EndGameListener;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientManager implements EndGameListener, MessageListener {

    private final Server server;

    /**
     * Game Controller
     */
    private Controller controller;

    /**
     * virtual clients by nickname
     */
    private final ConcurrentMap<String, VirtualClient> virtualClients = new ConcurrentHashMap<>();

    /**
     * connected players by team
     */
    private final EnumMap<Tower, List<VirtualClient>> connectedPlayersByTeam = new EnumMap<>(Tower.class);

    /**
     * latch to force the end of the game
     */
    private CountDownLatch forceEndGameLatch;

    public ClientManager(Server server) {
        this.server = server;
    }

    /**
     * Getter of the controller
     *
     * @return the controller
     */
    public Controller getController() {
        return controller;
    }

    /**
     * @param nickname the nickname of the player
     * @return the virtual client associated to the nickname, null if not found
     */
    public VirtualClient getVirtualClient(String nickname) {
        if (virtualClients.containsKey(nickname)) {
            return virtualClients.get(nickname);
        }
        return null;
    }

    /**
     * Resetting the controller
     */
    public void resetGame() {
        controller = new Controller();
        controller.setEndGameListener(this);
        controller.addMessageListener(this);
        startController();
        server.resetGame();
        connectedPlayersByTeam.clear();
    }

    /**
     * Creates the virtual client for the player
     *
     * @param communicationHandler the communication handler of the player
     * @param nickname             the nickname of the player
     * @return true if the player was added, false otherwise
     */
    public boolean addPlayer(CommunicationHandler communicationHandler, String nickname) {
        if (controller.addPlayer(nickname)) {
            System.out.println("S: added player " + nickname);
            VirtualClient vc = new VirtualClient(nickname);
            connectToVirtualClient(vc, communicationHandler);
            virtualClients.put(nickname, vc);
            controller.sendInitialStats(vc);
            return true;
        }
        return false;
    }

    /**
     * Starts a virtual client and adds it to the model listeners. Adds the controller to the VirtualClient listeners
     *
     * @param vc                   the VirtualClient
     * @param communicationHandler handler for connection
     */
    public void connectToVirtualClient(VirtualClient vc, CommunicationHandler communicationHandler) {
        vc.setCommunicationHandler(communicationHandler);
        communicationHandler.setDisconnectListener(vc);
        communicationHandler.setMessageHandler(vc);
        controller.addModelListener(vc);
        vc.addMessageListener(controller);
    }

    /**
     * Starts the controller
     */
    private void startController() {
        new Thread(controller, "Controller Thread").start();
    }

    /**
     * Sets a player as connected
     *
     * @param vc the virtual client
     */
    private synchronized void playerConnected(VirtualClient vc) {
        if (controller.isGameStarted()) {
            Tower team = controller.getPlayerTeam(vc.getIdentifier());
            if (team != null) {
                List<VirtualClient> connectedPlayers = connectedPlayersByTeam.get(team);
                if (forceEndGameLatch != null && connectedPlayers.size() == 0) {
                    forceEndGameLatch.countDown();
                }
                connectedPlayers.add(vc);
            }
        }
    }

    /**
     * Sets a player as disconnected
     *
     * @param vc the virtual client
     */
    private synchronized void playerDisconnected(VirtualClient vc) {
        if (controller.isGameStarted()) {
            Tower team = controller.getPlayerTeam(vc.getIdentifier());
            if (team != null) {
                connectedPlayersByTeam.get(team).remove(vc);
                int numOfTeamsWithPlayers = 0;
                for (Tower teamWithPlayers : connectedPlayersByTeam.keySet()) {
                    if (!connectedPlayersByTeam.get(teamWithPlayers).isEmpty()) {
                        numOfTeamsWithPlayers++;
                    }
                }
                if (numOfTeamsWithPlayers <= 1) {
                    forceEndGameLatch = new CountDownLatch(1);
                    for (Tower t : connectedPlayersByTeam.keySet()) {
                        for (VirtualClient player : connectedPlayersByTeam.get(t)) {
                            player.sendMessage(new CommMessage(CommMsgType.WAITING));
                        }
                    }
                    controller.setWaiting(true);
                    new Thread(() -> {
                        try {
                            System.out.println("S: waiting for other players");
                            boolean cameBack = forceEndGameLatch.await(60, TimeUnit.SECONDS);
                            synchronized (this) {
                                if (server.getState() == ServerState.NORMAL) {
                                    if (cameBack) {
                                        for (Tower t : connectedPlayersByTeam.keySet()) {
                                            for (VirtualClient player : connectedPlayersByTeam.get(t)) {
                                                controller.notifyCurrentGameStateToPlayer(player.getIdentifier());
                                            }
                                        }
                                        controller.setWaiting(false);
                                        System.out.println("S: continue game");
                                    } else {
                                        for (Tower t : connectedPlayersByTeam.keySet()) {
                                            for (VirtualClient player : connectedPlayersByTeam.get(t)) {
                                                player.sendMessage(new Winners(EnumSet.of(t)));
                                            }
                                        }
                                        onEndGameEvent(null);
                                    }
                                    forceEndGameLatch = null;
                                }
                            }
                        } catch (InterruptedException ignored) {
                            controller.setWaiting(false);
                            forceEndGameLatch = null;
                        }
                    }).start();
                }
            }
        }
    }

    /**
     * Method called by Virtual Client if the connection is lost. Server removes the thread from execution and remove the
     * virtual Client bonded with the nickname of disconnected player.
     * Calls method skip turn on controller.
     *
     * @param event of the connection
     */
    @Override
    public synchronized void onEndGameEvent(EndGameEvent event) {
        controller.stop();
        controller.removeEndGameListener();
        System.out.println("S: ending game");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        for (VirtualClient vc : virtualClients.values()) {
            controller.removeModelListener(vc);
            vc.removeAllMessageListeners();
            vc.stop();
        }
        virtualClients.clear();
        System.out.println("S: disconnected all players");
        resetGame();
        if (server.getState() == ServerState.NORMAL && forceEndGameLatch != null) {
            forceEndGameLatch.countDown();
        }
    }

    /**
     * Disconnects and removes the virtual client from the server if the game has not started.
     *
     * @param vc virtual client
     */
    public synchronized void onDisconnect(VirtualClient vc) {
        vc.stop();
        if (!controller.isGameStarted()) {
            controller.removeModelListener(vc);
            vc.removeAllMessageListeners();
            virtualClients.remove(vc.getIdentifier());
            for (Tower team : connectedPlayersByTeam.keySet()) {
                connectedPlayersByTeam.get(team).remove(vc);
            }
        } else {
            Tower team = controller.getPlayerTeam(vc.getIdentifier());
            if (connectedPlayersByTeam.get(team).contains(vc))
                playerDisconnected(vc);
        }
        System.out.println("S: disconnected player " + vc.getIdentifier());
    }

    /**
     * Method called when a message is received
     *
     * @param event of the received message
     */
    @Override
    public synchronized void onMessage(MessageEvent event) {
        switch (MessageType.retrieveByMessage(event.getMessage())) {
            case CONNECTED -> playerConnected((VirtualClient) event.getSource());
            case DISCONNECTED -> onDisconnect((VirtualClient) event.getSource());
            case START_GAME -> {
                for (Tower t: Tower.values())
                    connectedPlayersByTeam.computeIfAbsent(t, k -> new LinkedList<>());
                for (VirtualClient vc : virtualClients.values()) {
                    Tower t = controller.getPlayerTeam(vc.getIdentifier());
                    if (t != null) {
                        connectedPlayersByTeam.get(t).add(vc);
                    }
                }
            }
        }
    }
}
