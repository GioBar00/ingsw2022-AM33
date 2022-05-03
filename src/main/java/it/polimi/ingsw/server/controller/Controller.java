package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.client.*;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.server.VirtualClient;
import it.polimi.ingsw.server.listeners.EndPartyEvent;
import it.polimi.ingsw.server.listeners.EndPartyListener;
import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameBuilder;
import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.enums.*;

/**
 * Controller class manages the first request for each client. If the request is valid instantiates a client handler.
 * Each client handler request to the Controller class specified methods for updating the model. Controller verifies the validity
 * of the request and calls the correct method. Provide an updated view for each client
 */
public class Controller implements MessageListener {

    /**
     * the instance of the game model
     */
    private Game model;

    /**
     * listener of the Controller
     */
    private EndPartyListener listener;

    private Lobby lobby;

    /**
     * Default constructor of class Controller
     * @param listener of the controller
     */
    public Controller(EndPartyListener listener) {
        model = null;
        this.listener = listener;
        lobby = null;
    }

    /**
     * Remove the controller listener
     */
    public void removeListener(){ this.listener = null;}

    /**
     * Method used for know if the model is instantiated
     * @return true if the model is instantiated
     */
    public boolean isInstantiated() {
        return model != null && lobby != null;
    }

    /**
     * Setter of the model. Calls the GameBuilder
     * @param preset is the number of players for the game
     * @param mode is the mode of the game
     */
    public void setModelAndLobby(GamePreset preset, GameMode mode, Lobby lobby) {
        model = GameBuilder.getGame(preset, mode);
        this.lobby = lobby;
    }

    /**
     * Method for sett a new player inside the model
     * @param nickname the nickname of the new player
     * @return True if the player has been added. False if the game is full or the nickname is already chosen
     */
    public boolean addPlayer(String nickname) {
        if(isInstantiated()) {
            return lobby.addPlayer(nickname);
        }
        return false;
    }

    /**
     * This method adds a new listener to the model
     * @param listener the listener of the model
     */
    public void addModelListener(MessageListener listener) {
        model.addMessageListener(listener);
        lobby.addMessageListener(listener);
    }

    /**
     * Removes one selected listener from the model
     * @param listener the removed listener
     */
    public void removeListener(MessageListener listener) {
        model.removeMessageListener(listener);
    }

    /**
     * Override methods from MessageListener Interface.
     * When a Virtual Client receives a valid message notify the controller that analyze the message and apply the
     * request to the model (if it's possible).
     * onMessage method is divided into 5 different methods; each of the methods handle a specific game phase.
     * @param event of the received message
     */
    @Override
    public synchronized void onMessage(MessageEvent event) {
        VirtualClient vc = (VirtualClient)event.getSource();
        Message msg = event.getMessage();

        if(MessageType.retrieveByMessage(msg) == MessageType.SKIP_TURN){
            String identifier = ((VirtualClient) event.getSource()).getIdentifier();
            if(model.getGameState() == GameState.UNINITIALIZED){
                {
                    if(lobby.removePlayer(identifier)){
                        lobby.removePlayer(identifier);
                        lobby.removeMessageListener(vc);
                    }
                    else {
                        lobby.removeAllMessageListener();
                        listener.onEndPartyEvent(new EndPartyEvent(this));
                        model = null;
                    }
                }
            }
            else{
                if(identifier.equals(model.getCurrentPlayer())){
                    model.skipCurrentPlayerTurn();
                }
            }
        }
        else {
            switch (model.getGameState()) {
                case UNINITIALIZED:
                    handleGameSetup(vc, msg);
                    break;
                case STARTED:
                    if (canPlay(vc.getIdentifier())) {
                        vc.sendNotYourTurnMessage();
                    } else {
                        switch (model.getPhase()) {
                            case PLANNING -> handlePlanningPhase(vc, msg);

                            case MOVE_STUDENTS -> handleMoveStudentPhase(vc, msg);

                            case MOVE_MOTHER_NATURE -> handleMoveMotherNaturePhase(vc, msg);

                            case CHOOSE_CLOUD -> handleChooseCloudPhase(vc, msg);
                        }
                    }
                    break;
                case ENDED:
                    vc.sendImpossibleMessage();
                    break;
            }
        }


    }

    /**
     * Handle the messages during the setup phase
     * @param vc is the Virtual Client bonded with the request
     * @param msg is the Message
     */
    private void handleGameSetup(VirtualClient vc, Message msg) {

        switch (MessageType.retrieveByMessage(msg)) {
            case CHOSEN_TEAM -> {
                ChosenTeam chosenTeam = (ChosenTeam)msg;
                this.changeTeam(vc.getIdentifier(),chosenTeam.getTower());

            }
            case START_GAME -> {
                if (!this.startGame(vc.getIdentifier()))
                        vc.sendImpossibleMessage();
            }

            case CHOSEN_WIZARD -> {
                ChosenWizard chosenWizard = (ChosenWizard)msg;
                if(!lobby.setWizard(chosenWizard.getWizard(),vc.getIdentifier()))
                    vc.sendImpossibleMessage();
            }
            default -> vc.sendImpossibleMessage();
        }
    }

    /**
     * Send the current wizard situation to a specified listener
     * @param messageListener the client who is going to receive
     */
    public void sendAvailableWizard(MessageListener messageListener){
        lobby.notifyListener(messageListener.getIdentifier(),new MessageEvent(this, new AvailableWizards(lobby.getWizardsView())));
    }

    /**
     * Handle the messages during the planning phase
     * @param vc is the Virtual Client bonded with the request
     * @param msg is the Message
     */
    private void handlePlanningPhase(VirtualClient vc, Message msg) {
        if (MessageType.retrieveByMessage(msg) == MessageType.PLAYED_ASSISTANT_CARD) {
            PlayedAssistantCard playedAssistantCard = (PlayedAssistantCard) msg;
            if(!model.playAssistantCard(playedAssistantCard.getAssistantCard()))
                vc.sendImpossibleMessage();
        } else {
            vc.sendImpossibleMessage();
        }
    }

    /**
     * Handle the messages during the "move student" phase
     * @param vc is the Virtual Client bonded with the request
     * @param msg is the Message
     */
    private void handleMoveStudentPhase(VirtualClient vc, Message msg) {
        switch (MessageType.retrieveByMessage(msg)){
            case MOVED_STUDENT,SWAPPED_STUDENTS -> {
                MovedStudent movedStudent = (MovedStudent) msg;
                CharacterParameters parameters;

                switch (movedStudent.getFrom()) {

                    case ENTRANCE:
                        switch (movedStudent.getTo()) {
                            case HALL -> {
                                if(!model.moveStudentToHall(movedStudent.getFromIndex()))
                                    vc.sendImpossibleMessage();
                            }
                            case ISLAND -> {
                                if(!model.moveStudentToIsland(movedStudent.getFromIndex(), movedStudent.getToIndex()))
                                    vc.sendImpossibleMessage();
                            }
                            default -> vc.sendImpossibleMessage();
                        }
                        break;

                    case CARD:
                        switch (movedStudent.getTo()) {
                            case ISLAND, ENTRANCE, HALL -> {
                                parameters = CharacterChoiceAdapter.convert(movedStudent);
                                if(!model.applyEffect(parameters))
                                    vc.sendImpossibleMessage();
                            }
                        }
                        break;

                    case HALL:
                        parameters = CharacterChoiceAdapter.convert(movedStudent);
                        if(!model.applyEffect(parameters))
                            vc.sendImpossibleMessage();
                        break;

                    default: vc.sendImpossibleMessage();
                }
            }

            case ACTIVATED_CHARACTER_CARD -> {
                ActivatedCharacterCard activatedCharacterCard = (ActivatedCharacterCard) msg;
                if(!model.activateCharacterCard(activatedCharacterCard.getCharacterCardIndex()))
                    vc.sendImpossibleMessage();
            }

            case CHOSEN_ISLAND ->{
                ChosenIsland chosenIsland = (ChosenIsland) msg;
                CharacterParameters parameters = CharacterChoiceAdapter.convert(chosenIsland);
                if(!model.applyEffect(parameters))
                    vc.sendImpossibleMessage();
            }

            case CHOSEN_STUDENT_COLOR -> {
                ChosenStudentColor chosenStudentColor = (ChosenStudentColor)msg;
                CharacterParameters parameters = CharacterChoiceAdapter.convert(chosenStudentColor);
                if(!model.applyEffect(parameters))
                    vc.sendImpossibleMessage();
            }

            case CONCLUDE_CHARACTER_CARD_EFFECT -> {
                if(!model.endEffect())
                    vc.sendImpossibleMessage();
            }

            default -> vc.sendImpossibleMessage();
        }
    }

    /**
     * Handle the messages during the "move mother nature" phase
     * @param vc is the Virtual Client bonded with the request
     * @param msg is the Message
     */
    private void handleMoveMotherNaturePhase(VirtualClient vc, Message msg) {
        if(MessageType.retrieveByMessage(msg) == MessageType.MOVED_MOTHER_NATURE){
            MovedMotherNature movedMotherNature = (MovedMotherNature)msg;
            if (!model.moveMotherNature(movedMotherNature.getNumMoves()))
                vc.sendImpossibleMessage();
        }
        else vc.sendImpossibleMessage();
    }

    /**
     * Handle the messages during the "choose cloud" phase
     * @param vc is the Virtual Client bonded with the request
     * @param msg is the Message
     */
    private void handleChooseCloudPhase(VirtualClient vc, Message msg) {
        if(MessageType.retrieveByMessage(msg) == MessageType.CHOSEN_CLOUD){
            ChosenCloud chosenCloud = (ChosenCloud) msg;
            if(!model.getStudentsFromCloud(chosenCloud.getCloudIndex()))
                vc.sendImpossibleMessage();
        }
        else vc.sendImpossibleMessage();
    }

    /**
     * Method used by onMessage for starting the game
     * @param nickname who try to call this method
     * @return true if the game is started. False if the game can't start or who request this method doesn't have
     * the rights
     */
    private boolean startGame(String nickname) {
        if (nickname.equals(lobby.getMaster()) && lobby.canStart()) {
           for(PlayerDetails p : lobby.getPlayers()){
               model.addPlayer(p);
           }
           return model.startGame();
        }
        return false;
    }

    /**
     * Tries to change the team of one player
     * @param nickname of the player who wants to change team
     * @param tower the team he wants to join
     */
     private synchronized void changeTeam(String nickname, Tower tower) {
         lobby.changeTeam(nickname, tower);
     }


    /**
     * Used for know if is a player turn
     * @param nickname the player who send a message
     * @return true if the player is the current player
     */
    private boolean canPlay(String nickname) {
        return !nickname.equals(model.getCurrentPlayer());
    }

    /**
     * Return the nickname of the current Player
     * @return null if the model is not instantiated or the nickname of the current player
     */
    public String getCurrentPlayer(){
        if(isInstantiated())
            return model.getCurrentPlayer();
        return null;
    }
}
