package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.network.messages.IgnoreMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.client.ChosenTeam;
import it.polimi.ingsw.network.messages.client.ChosenWizard;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.server.ClientManager;
import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.server.VirtualClient;
import it.polimi.ingsw.server.lobby.Lobby;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameBuilder;
import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.enums.*;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Controller class manages the first request for each client. If the request is valid instantiates a client handler.
 * Each client handler request to the Controller class specified methods for updating the model. Controller verifies the validity
 * of the request and calls the correct method. Provide an updated view for each client
 */
public class Controller implements Runnable, MessageListener {

    /**
     * the instance of the game model
     */
    private Game model;

    /**
     * Lobby for the party
     */
    private Lobby lobby;

    /**
     * The ClientManager associated to the Controller
     */
    private final ClientManager clientManager;

    /**
     * The queue used to process the message events.
     */
    private final LinkedBlockingQueue<MessageEvent> queue;

    /**
     * If the controller is stopped
     */
    private volatile boolean stopped = true;

    /**
     * If the controller should wait and do nothing
     */
    private volatile boolean waiting = false;

    /**
     * Default constructor of class Controller
     *
     * @param clientManager the client manager associated with this controller
     */
    public Controller(ClientManager clientManager) {
        this.clientManager = clientManager;
        model = null;
        lobby = null;
        queue = new LinkedBlockingQueue<>();
    }

    /**
     * This method sets the status to waiting. It is used when there's only one player left in the party.
     *
     * @param waiting true if the party is waiting for the other player to join.
     */
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
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
     * @param lobby lobby of the game
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
    public synchronized void handleMessage(MessageEvent event) {
        VirtualClient vc = (VirtualClient) event.getSource();
        Message msg = event.getMessage();
        if (MessageType.retrieveByMessage(msg) == MessageType.DISCONNECTED) {
            handleDisconnect(vc);
        } else {
            if (!waiting) {
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
            } else
                vc.sendMessage(new CommMessage(CommMsgType.WAITING));
        }
    }

    /**
     * Handles the skip turn request.
     *
     * @param vc the virtual client that sent the request
     */
    public synchronized void handleDisconnect(VirtualClient vc) {
        if (waiting || !clientManager.isClientInGame(vc))
            return;
        String identifier = vc.getIdentifier();
        switch (model.getGameState()) {
            case UNINITIALIZED -> {
                if (lobby.containsPlayer(identifier)) {
                    if (lobby.getMaster().equals(identifier)) {
                        lobby.removeAllMessageListeners();
                        clientManager.gameEnded();
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
                        System.out.println("S: " + identifier + "'s turn skipped");
                    }
                }
            }
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
                    lobby.sendStart();
                }
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
        if (model.getGameState() == GameState.ENDED)
            clientManager.gameEnded();
    }

    /**
     * Method used by onMessage for starting the game.
     *
     * @param nickname who try to call this method
     * @return true if the game is started. False if the game can't start or who request this method doesn't have
     * the rights
     */
    private boolean startGame(String nickname) {
        if (nickname.equals(lobby.getMaster()) && lobby.canStart()) {
            synchronized (clientManager) {
                synchronized (this) {
                    for (PlayerDetails p : lobby.getPlayers()) {
                        model.addPlayer(p);
                    }
                    boolean started = model.startGame();
                    if (started) {
                        clientManager.gameStarted();
                        return true;
                    }
                    return false;
                }
            }
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
        lobby.sendStart();
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
                    MessageEvent m = queue.take();
                    if (MessageType.retrieveByMessage(m.getMessage()) != MessageType.IGNORE)
                        handleMessage(m);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Method used to stop the thread.
     */
    public void stop() {
        stopped = true;
        queue.add(new MessageEvent(this, new IgnoreMessage()));
    }

    /**
     * Returns the team of the player.
     *
     * @param identifier the nickname of the player.
     * @return the team of the player.
     */
    public Tower getPlayerTeam(String identifier) {
        return model.getPlayerTeam(identifier);
    }


    /**
     * Notifies a specific player sending him the game view.
     *
     * @param identifier the nickname of the player.
     */
    public synchronized void notifyCurrentGameStateToPlayer(String identifier) {
        if (isInstantiated())
            model.notifyCurrentGameStateToPlayer(identifier);
    }
}
