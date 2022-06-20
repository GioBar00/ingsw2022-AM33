package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.network.listeners.MessageListenerSubscriber;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.client.ChosenTeam;
import it.polimi.ingsw.network.messages.client.ChosenWizard;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.server.VirtualClient;
import it.polimi.ingsw.server.listeners.EndGameEvent;
import it.polimi.ingsw.server.listeners.EndGameListener;
import it.polimi.ingsw.server.listeners.EndGameListenerSubscriber;
import it.polimi.ingsw.server.lobby.Lobby;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameBuilder;
import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.enums.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Controller class manages the first request for each client. If the request is valid instantiates a client handler.
 * Each client handler request to the Controller class specified methods for updating the model. Controller verifies the validity
 * of the request and calls the correct method. Provide an updated view for each client
 */
public class Controller implements Runnable, MessageListener, EndGameListenerSubscriber, MessageListenerSubscriber {

    /**
     * the instance of the game model
     */
    private Game model;

    /**
     * end game listener of the Controller
     */
    private EndGameListener endGameListener;

    /**
     * Lobby for the party
     */
    private Lobby lobby;

    /**
     * The queue used to process the message events.
     */
    private final LinkedBlockingQueue<MessageEvent> queue;

    private final List<MessageListener> messageListeners = new LinkedList<>();

    private volatile boolean stopped = true;

    /**
     * Default constructor of class Controller
     */
    public Controller() {
        model = null;
        lobby = null;
        queue = new LinkedBlockingQueue<>();
    }

    /**
     * Remove the controller listener
     */
    public void removeEndGameListener() {
        this.endGameListener = null;
    }

    /**
     * Method used to know if the model is instantiated
     *
     * @return true if the model is instantiated
     */
    public boolean isInstantiated() {
        return model != null && lobby != null;
    }

    /**
     * Setter of the model. Calls the GameBuilder
     *
     * @param preset is the number of players for the game
     * @param mode   is the mode of the game
     */
    public void setModelAndLobby(GamePreset preset, GameMode mode, Lobby lobby) {
        model = GameBuilder.getGame(preset, mode);
        this.lobby = lobby;
    }

    /**
     * Method for sett a new player inside the model
     *
     * @param nickname the nickname of the new player
     * @return True if the player has been added. False if the game is full or the nickname is already chosen
     */
    public boolean addPlayer(String nickname) {
        if (isInstantiated()) {
            return lobby.addPlayer(nickname);
        }
        return false;
    }

    /**
     * This method adds a new listener to the model
     *
     * @param listener the listener of the model
     */
    public void addModelListener(MessageListener listener) {
        model.addMessageListener(listener);
        lobby.addMessageListener(listener);
    }

    /**
     * Removes one selected listener from the model
     *
     * @param listener the removed listener
     */
    public void removeModelListener(MessageListener listener) {
        if (model != null)
            model.removeMessageListener(listener);
    }

    /**
     * Returns if the game is started
     *
     * @return a boolean true if the game is started
     */
    public boolean isGameStarted() {
        if (model == null)
            return false;
        return model.getGameState().equals(GameState.STARTED);
    }

    /**
     * Override methods from MessageListener Interface.
     * When a Virtual Client receives a valid message notify the controller that put the event in a queue that contains
     * all the request that need to be handled .
     *
     * @param event of the received message
     */
    @Override
    public void onMessage(MessageEvent event) {
        queue.add(event);
    }

    /**
     * Handles the message event. Analyze the message and apply the request to the model (if it's possible).
     * onMessage method is divided into 5 different methods; each of the methods handle a specific game phase.
     *
     * @param event of the received message
     */
    public void handleMessage(MessageEvent event) {
        VirtualClient vc = (VirtualClient) event.getSource();
        Message msg = event.getMessage();

        switch (MessageType.retrieveByMessage(msg)) {
            case CONNECTED -> model.notifyCurrentGameStateToPlayer(vc.getIdentifier());
            case DISCONNECTED -> handleDisconnect(vc);
            default -> {
                switch (model.getGameState()) {
                    case UNINITIALIZED -> handleGameSetup(vc, msg);
                    case STARTED -> {
                        if (!canPlay(vc.getIdentifier())) {
                            vc.sendMessage(new CommMessage(CommMsgType.ERROR_NOT_YOUR_TURN));
                        } else if (isInstantiated()) {
                            switch (model.getPhase()) {
                                case PLANNING -> handlePlanningPhase(vc, msg);

                                case MOVE_STUDENTS, MOVE_MOTHER_NATURE, CHOOSE_CLOUD -> handlePlayingPhase(vc, msg);

                            }
                        } else {
                            vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                        }
                    }
                }
            }
        }
        notifyMessageListeners(event);
    }

    /**
     * Handles the skip turn request.
     *
     * @param vc the virtual client that sent the request
     */
    private void handleDisconnect(VirtualClient vc) {
        String identifier = vc.getIdentifier();
        System.out.println("CONTR: Skip turn from " + identifier);
        switch (model.getGameState()) {
            case UNINITIALIZED -> {
                if (lobby.containsPlayer(identifier)) {
                    if (lobby.getMaster().equals(identifier)) {
                        lobby.removeAllMessageListeners();
                        notifyEndGame();
                        model = null;
                    } else {
                        lobby.removePlayer(identifier);
                        lobby.removeMessageListener(vc);
                        lobby.sendStart();
                    }
                }
            }
            case STARTED -> {
                if (isInstantiated()) {
                    if (identifier.equals(model.getCurrentPlayer())) {
                        model.skipCurrentPlayerTurn();
                    }
                }
            }
            case ENDED -> notifyEndGame();
        }
    }

    /**
     * Handle the messages during the setup phase
     *
     * @param vc  is the Virtual Client bonded with the request
     * @param msg is the Message
     */
    private void handleGameSetup(VirtualClient vc, Message msg) {
        switch (MessageType.retrieveByMessage(msg)) {
            case CHOSEN_TEAM -> {
                ChosenTeam chosenTeam = (ChosenTeam) msg;
                changeTeam(vc.getIdentifier(), chosenTeam.getTower());
            }
            case START_GAME -> {
                if (!lobby.getMaster().equals(vc.getIdentifier()))
                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_NOT_MASTER));
                else if (!startGame(vc.getIdentifier()))
                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_CANT_START));
            }

            case CHOSEN_WIZARD -> {
                ChosenWizard chosenWizard = (ChosenWizard) msg;
                if (!lobby.setWizard(chosenWizard.getWizard(), vc.getIdentifier())) {
                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                    vc.sendMessage(new AvailableWizards(lobby.getWizardsView()));
                } else {
                    vc.sendMessage(new CommMessage(CommMsgType.OK));
                    lobby.notifyAvailableWizards();
                    lobby.notifyTeams(vc.getIdentifier());
                }
                lobby.sendStart();
            }
            default -> vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
        }
    }

    /**
     * Send the current wizard situation to a specified listener
     *
     * @param messageListener the client who is going to receive
     */
    public synchronized void sendInitialStats(MessageListener messageListener) {
        lobby.notifyAvailableWizards(messageListener.getIdentifier());
    }

    /**
     * Handle the messages during the planning phase
     *
     * @param vc  is the Virtual Client bonded with the request
     * @param msg is the Message
     */
    private void handlePlanningPhase(VirtualClient vc, Message msg) {
        if (MessageType.retrieveByMessage(msg) == MessageType.PLAYED_ASSISTANT_CARD) {
            PlayedAssistantCard playedAssistantCard = (PlayedAssistantCard) msg;
            if (!model.playAssistantCard(playedAssistantCard.getAssistantCard()))
                vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
        } else {
            vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
        }
    }

    /**
     * Handle the messages during the playing phase
     *
     * @param vc  is the Virtual Client bonded with the request
     * @param msg is the Message
     */
    private void handlePlayingPhase(VirtualClient vc, Message msg) {
        switch (MessageType.retrieveByMessage(msg)) {
            case MOVED_STUDENT -> {
                MovedStudent movedStudent = (MovedStudent) msg;
                CharacterParameters parameters;

                switch (movedStudent.getFrom()) {

                    case ENTRANCE:
                        switch (movedStudent.getTo()) {
                            case HALL -> {
                                if (!model.moveStudentToHall(movedStudent.getFromIndex()))
                                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                            }
                            case ISLAND -> {
                                if (!model.moveStudentToIsland(movedStudent.getFromIndex(), movedStudent.getToIndex()))
                                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                            }
                            default -> vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                        }
                        break;

                    case CARD:
                        switch (movedStudent.getTo()) {
                            case ISLAND, ENTRANCE, HALL -> {
                                parameters = CharacterChoiceAdapter.convert(movedStudent);
                                if (!model.applyEffect(parameters))
                                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                            }
                        }
                        break;

                    case HALL:
                        parameters = CharacterChoiceAdapter.convert(movedStudent);
                        if (!model.applyEffect(parameters))
                            vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                        break;

                    default:
                        vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                }
            }
            case SWAPPED_STUDENTS -> {
                SwappedStudents movedStudent = (SwappedStudents) msg;
                CharacterParameters parameters;

                switch (movedStudent.getFrom()) {
                    case ENTRANCE -> {
                        if (movedStudent.getTo() == MoveLocation.HALL) {
                            parameters = CharacterChoiceAdapter.convert(movedStudent);
                            if (!model.applyEffect(parameters))
                                vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                        }
                    }
                    case CARD -> {
                        if (movedStudent.getTo() == MoveLocation.ENTRANCE) {
                            parameters = new CharacterParameters(StudentColor.retrieveStudentColorByOrdinal(movedStudent.getFromIndex()), movedStudent.getToIndex());
                            if (!model.applyEffect(parameters))
                                vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                        }
                    }
                    default -> vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                }

            }
            case ACTIVATED_CHARACTER_CARD -> {
                ActivatedCharacterCard activatedCharacterCard = (ActivatedCharacterCard) msg;
                if (!model.activateCharacterCard(activatedCharacterCard.getCharacterCardIndex()))
                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
            }

            case CHOSEN_ISLAND -> {
                ChosenIsland chosenIsland = (ChosenIsland) msg;
                CharacterParameters parameters = CharacterChoiceAdapter.convert(chosenIsland);
                if (!model.applyEffect(parameters))
                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
            }

            case CHOSEN_STUDENT_COLOR -> {
                ChosenStudentColor chosenStudentColor = (ChosenStudentColor) msg;
                CharacterParameters parameters = CharacterChoiceAdapter.convert(chosenStudentColor);
                if (!model.applyEffect(parameters))
                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
            }

            case CONCLUDE_CHARACTER_CARD_EFFECT -> {
                if (!model.endEffect())
                    vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
            }
            case MOVED_MOTHER_NATURE -> {
                if (model.getPhase().equals(GamePhase.MOVE_MOTHER_NATURE)) {
                    MovedMotherNature movedMotherNature = (MovedMotherNature) msg;
                    if (!model.moveMotherNature(movedMotherNature.getNumMoves()))
                        vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                } else vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
            }

            case CHOSEN_CLOUD -> {
                if (model.getPhase().equals(GamePhase.CHOOSE_CLOUD)) {
                    ChosenCloud chosenCloud = (ChosenCloud) msg;
                    if (!model.getStudentsFromCloud(chosenCloud.getCloudIndex()))
                        vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
                } else vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
            }

            default -> vc.sendMessage(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE));
        }
    }

    /**
     * Method used by onMessage for starting the game.
     *
     * @param nickname who try to call this method
     * @return true if the game is started. False if the game can't start or who request this method doesn't have
     * the rights
     */
    private synchronized boolean startGame(String nickname) {
        if (nickname.equals(lobby.getMaster()) && lobby.canStart()) {
            for (PlayerDetails p : lobby.getPlayers()) {
                model.addPlayer(p);
            }
            return model.startGame();
        }
        return false;
    }

    /**
     * Tries to change the team of one player
     *
     * @param nickname of the player who wants to change team
     * @param tower    the team he wants to join
     */
    private synchronized void changeTeam(String nickname, Tower tower) {
        lobby.changeTeam(nickname, tower);
    }


    /**
     * Used to know if is a player turn
     *
     * @param nickname the player who send a message
     * @return true if the player is the current player
     */
    private boolean canPlay(String nickname) {
        return nickname.equals(model.getCurrentPlayer());
    }

    /**
     * Return the nickname of the current Player
     *
     * @return null if the model is not instantiated or the nickname of the current player
     */
    public String getCurrentPlayer() {
        if (isInstantiated())
            return model.getCurrentPlayer();
        return null;
    }

    /**
     * This method take the request from the queue and try to apply the changes.
     */
    public void run() {
        if (stopped) {
            stopped = false;
            queue.clear();
            try {
                while (!stopped) {
                    handleMessage(queue.take());
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void stop() {
        stopped = true;
    }

    /**
     * Method to subscribe a EndGameListener
     *
     * @param listener the EndGameListener to subscribe
     */
    @Override
    public void setEndGameListener(EndGameListener listener) {
        endGameListener = listener;
    }

    /**
     * Method to notify the EndGameListener
     */
    @Override
    public void notifyEndGame() {
        if (endGameListener != null) {
            System.out.println("CONTR: Notify end game");
            endGameListener.onEndGameEvent(new EndGameEvent(this));
        }
    }

    public Tower getPlayerTeam(String identifier) {
        return model.getPlayerTeam(identifier);
    }

    public void notifyCurrentGameStateToPlayer(String identifier) {
        if (isInstantiated())
            model.notifyCurrentGameStateToPlayer(identifier);
    }

    /**
     * Adds a message listener.
     *
     * @param listener the listener to add
     */
    @Override
    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    /**
     * Removes a message listener.
     *
     * @param listener the listener to remove
     */
    @Override
    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    /**
     * Notifies all listeners.
     *
     * @param event of the message to notify
     */
    @Override
    public void notifyMessageListeners(MessageEvent event) {
        for (MessageListener listener : messageListeners) {
            listener.onMessage(event);
        }
    }

    /**
     * Notifies a specific listener.
     *
     * @param identifier of the listener to notify
     * @param event      of the message to notify
     */
    @Override
    public void notifyMessageListener(String identifier, MessageEvent event) {
        messageListeners.stream().filter(l -> l.getIdentifier().equals(identifier)).forEach(l -> l.onMessage(event));
    }
}
