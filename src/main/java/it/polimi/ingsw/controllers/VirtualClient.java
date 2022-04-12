package it.polimi.ingsw.controllers;

import it.polimi.ingsw.model.cards.CharacterParameters;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.client.*;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class VirtualClient implements Runnable{
    private final String nickname;
    private final Socket socket;
    private final Controller controller;
    private final Scanner in;
    private final PrintWriter out;
    private final CharacterChoiceAdapter adapter;

    public VirtualClient(Controller controller,String nickname,Socket socket) throws IOException {
        this.controller = controller;
        this.nickname = nickname;
        this.socket = socket;
        in = new Scanner(this.socket.getInputStream());
        out = new PrintWriter(this.socket.getOutputStream());
        adapter = new CharacterChoiceAdapter();
    }


    @Override
    public void run() {
        messagesHandler();
    }

    private void messagesHandler() {
        CharacterParameters parameters;
        while(true){
                String line = in.nextLine();
                Message m = MessageBuilder.fromJson(line);
                int value;
                switch (MessageType.retrieveByMessageClass(m)){
                    case ACTIVATED_CHARACTER_CARD :
                        ActivatedCharacterCard activatedCharacterCard = (ActivatedCharacterCard) m;
                        if(activatedCharacterCard.isValid()){
                            value = controller.playCharacterCard(nickname,activatedCharacterCard.getCharacterCardIndex());
                            sendNegativeAnswer(value);
                            if(value == 1)
                                adapter.setActivatedCard();
                        }
                        else{ sendInvalidMessage(); }
                        break;

                    case CHOOSE_GAME:

                    case LOGIN:
                        sendInvalidMessage();
                        break;

                    case CHOOSE_TEAM:
                        assert m instanceof ChooseTeam;
                        ChooseTeam chooseTeam = (ChooseTeam) m;
                        if(chooseTeam.isValid()){
                            if(!controller.changeTeam(nickname,chooseTeam.getTower()))
                                sendNegativeAnswer(0);
                        }else{ sendInvalidMessage(); }
                        break;

                    case CHOSEN_CLOUD :
                        ChosenCloud chosenCloud = (ChosenCloud) m;
                        if(chosenCloud.isValid()){
                            value = controller.chooseCloud(nickname,chosenCloud.getCloudIndex());
                            sendNegativeAnswer(value);
                        }
                        else{ sendInvalidMessage(); }
                        break;

                    case CHOOSE_ISLAND:
                        ChosenIsland chosenIsland = (ChosenIsland) m;
                        if(chosenIsland.isValid()){
                            parameters = adapter.chooseIsland(chosenIsland.getIslandIndex());
                            if(parameters == null){
                                sendImpossibleMessage();
                            }else{
                                value = controller.applyEffect(nickname, parameters);
                                sendNegativeAnswer(value);
                            }
                        }else sendInvalidMessage();
                        break;

                    case CHOOSE_STUDENT_COLOR:
                        ChosenStudentColor chosenStudentColor = (ChosenStudentColor)m;
                        if(chosenStudentColor.isValid()) {
                            parameters = adapter.chooseColor(chosenStudentColor.getStudentColor());
                            if (parameters == null) {
                                sendImpossibleMessage();
                            } else {
                                value = controller.applyEffect(nickname, parameters);
                                sendNegativeAnswer(value);
                            }
                        }else sendInvalidMessage();
                        break;

                    case CONCLUDE_CHARACTER_CARD_EFFECT:
                        ConcludeCharacterCardEffect concludeCharacterCardEffect = (ConcludeCharacterCardEffect)m;
                        if(concludeCharacterCardEffect.isValid()) {
                            value = controller.concludeCharacterCardEffect(nickname);
                            if (value == 1)
                                adapter.resetActivatedCard();
                            sendNegativeAnswer(value);
                        }else sendInvalidMessage();
                        break;

                    case MOVED_MOTHER_NATURE :
                        MovedMotherNature movedMotherNature = (MovedMotherNature)m;
                        if(movedMotherNature.isValid()){
                            value = controller.moveMotherNature(nickname,movedMotherNature.getNumMoves());
                            sendNegativeAnswer(value);
                        }
                        else{ sendInvalidMessage(); }
                        break;

                    case MOVED_STUDENT, SWAPPED_STUDENT:
                        MovedStudent movedStudent = (MovedStudent) m;
                        if (movedStudent.isValid()) {
                            switch (movedStudent.getFrom()) {

                                case ENTRANCE:
                                    switch (movedStudent.getTo()) {
                                        case HALL -> {
                                            value = controller.moveStudentToHall(nickname, movedStudent.getFromIndex());
                                            sendNegativeAnswer(value);
                                        }
                                        case ISLAND -> {
                                            value = controller.moveStudentToIsland(nickname, movedStudent.getFromIndex(), movedStudent.getToIndex());
                                            sendNegativeAnswer(value);
                                        }
                                    }
                                    break;

                                case CARD:
                                    switch (movedStudent.getTo()) {
                                        case ISLAND , ENTRANCE-> {
                                            parameters = adapter.fromCard(movedStudent.getFromIndex(), movedStudent.getToIndex());
                                            if (parameters == null) {
                                                sendImpossibleMessage();
                                            } else {
                                                value = controller.applyEffect(nickname, parameters);
                                                sendNegativeAnswer(value);
                                            }
                                        }
                                        case HALL -> {
                                            parameters = adapter.chooseColor(StudentColor.retrieveStudentColorByOrdinal(movedStudent.getFromIndex()));
                                            if (parameters == null) {
                                                sendImpossibleMessage();
                                            } else {
                                                value = controller.applyEffect(nickname, parameters);
                                                sendNegativeAnswer(value);
                                            }
                                        }
                                    }
                                    break;

                                case HALL:
                                    parameters= adapter.fromHall(movedStudent.getFromIndex(), movedStudent.getFromIndex());
                                    if(parameters == null){
                                        sendImpossibleMessage();
                                    }else{
                                        value = controller.applyEffect(nickname, parameters);
                                        sendNegativeAnswer(value);
                                    }
                                    break;
                            }
                        }
                        break;

                    case PLAYED_ASSISTANT_CARD :
                        PlayedAssistantCard playedAssistantCard = (PlayedAssistantCard) m;
                        if (playedAssistantCard.isValid()) {
                            value = controller.playCard(nickname, playedAssistantCard.getAssistantCard());
                            sendNegativeAnswer(value);
                        } else {  sendInvalidMessage(); }
                        break;


                    case START_GAME :
                        StartGame startGame = (StartGame)m;
                        if(startGame.isValid()){
                            value = controller.startGame(nickname);
                            sendNegativeAnswer(value);
                        }else sendInvalidMessage();
                        break;


                }

        }
    }

    private void sendNegativeAnswer(int value) {
        if(value == -1){
            out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_NOT_YOUR_TURN)));
            out.flush();
        }
        if(value == 0){
            out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE)));
            out.flush();
        }
    }

    private void sendInvalidMessage() {
        out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_INVALID_MESSAGE)));
        out.flush();
    }

    private void sendImpossibleMessage() {
        out.println(MessageBuilder.toJson(new CommMessage(CommMsgType.ERROR_IMPOSSIBLE_MOVE)));
        out.flush();
    }
}

