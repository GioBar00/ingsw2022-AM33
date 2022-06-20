package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.enums.Color;
import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.network.messages.client.ChosenTeam;
import it.polimi.ingsw.network.messages.client.ChosenWizard;
import it.polimi.ingsw.network.messages.client.StartGame;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.server.model.enums.*;

import java.util.*;

/**
 * This class has the task to parse the input from the user.
 */
public class InputParser {

    /**
     * A link to the CLI.
     */
    private final CLI cli;

    /**
     * Boolean that indicates if the player is the host
     */
    private boolean isHost;

    /**
     * Boolean that indicates if the player can choose a wizard.
     */
    private boolean canChoseWizard;

    /**
     * Boolean that indicates if the player can send a {@link StartGame} message.
     */
    private boolean canStart;

    /**
     * Boolean that indicates if the player can type something.
     */
    private boolean canWrite;

    /**
     * Boolean that indicates if the server is available.
     */
    private boolean serverStatus;

    /**
     * {@link Message} that contains the last request received from the server.
     */
    private Message lastRequest;

    /**
     * {@link WizardsView} that contains the available wizards.
     */
    private WizardsView wizardsView;

    /**
     * Boolean that indicates if the player can send a {@link ConcludeCharacterCardEffect} message.
     */
    private boolean canEndEffect;


    private final String RESET = Color.RESET.getName();
    private final String RED = Color.RED.getName();


    /**
     * Constructor.
     *
     * @param cli the CLI.
     */
    public InputParser(CLI cli) {
        this.cli = cli;
        isHost = false;
    }

    /**
     * This method is used for setting if the player is the host.
     */
    synchronized void setHost() {
        isHost = true;
    }

    /**
     * Getter of the boolean that indicates if the player is the host.
     *
     * @return true if the player is the host, false otherwise.
     */
    synchronized boolean isHost() {
        return isHost;
    }

    /**
     * This method is used for setting if the player can choose a wizard.
     *
     * @param value true if the player can choose a wizard, false otherwise.
     */

    synchronized void setCanChoseWizard(boolean value) {
        canChoseWizard = value;
    }

    /**
     * This method is used for setting if the player can write.
     */
    synchronized void canWrite() {
        canWrite = true;
    }

    /**
     * This method is used for setting if the player can't write.
     */
    synchronized void cantWrite() {
        canWrite = false;
    }

    /**
     * This method is used for setting if the player can start the game.
     */
    synchronized void setCanStart(boolean value) {
        canStart = value;
    }

    /**
     * This method is used for setting the status of the server.
     */
    synchronized void setServerStatus(boolean value) {
        serverStatus = value;
    }

    /**
     * This method is used for setting the last request received from the server.
     */
    synchronized void setLastRequest(Message request) {
        lastRequest = request;
    }

    /**
     * This method is used for setting the available wizards.
     */
    synchronized void setWizardsView(WizardsView wizardsView) {
        this.wizardsView = wizardsView;
    }

    /**
     * This method is used for resetting the input parser.
     */
    void resetParser(){
        wizardsView = null;
        lastRequest = null;
    }
    /**
     * This method parse the string that the user typed.
     *
     * @param input a String
     */
    synchronized void parse(String input) {

        if(cli.lastState == ViewState.WAITING){
            System.out.println(cli.colors.get(RED) + "Waiting for other players..." + cli.colors.get(RESET));
            return;
        }

        try {
            String[] in = input.split(" ");
            if (!serverStatus) {
                switch (in[0].toUpperCase()) {
                    case "C" -> cli.close();
                    case "R" -> cli.sendLogin();
                    default -> printInvalidMessage();
                }
                return;
            }
            if (canWrite) {
                if (in.length >= 1) {
                    switch (in[0].toUpperCase()) {
                        case "NEWGAME" -> parseNewGame(in);
                        case "WIZARD" -> parseWizard(in);
                        case "TEAM" -> parseTeam(in);
                        case "START" -> parseStart(in);
                        case "ASSISTANT" -> parseAssistantCard(in);
                        case "MOVE" -> parseMoveChoice(in);
                        case "SWAP" -> parseSwapChoice(in);
                        case "CONCLUDE" -> parseConcludeChoice();
                        case "ACTIVATE" -> parseCharacterCard(in);
                        case "CLOUD" -> parseCloudChoice(in);
                        case "ISLAND" -> parseIslandChoice(in);
                        case "COLOR" -> parseColor(in);
                        case "MOTHERNATURE" -> parseMotherNature(in);
                        default -> printInvalidMessage();
                    }
                } else {
                    printInvalidMessage();
                }
            } else {
                System.out.println(cli.colors.get(RED) + "Not your turn" + cli.colors.get(RESET));
            }
        } catch (IndexOutOfBoundsException e) {
            printInvalidMessage();
        }
    }

    /**
     * This method check if the input is valid according to the phase of the game.
     *
     * @param messageType the type of the message the player wants to send.
     * @return true if the input is valid, false otherwise.
     */
    private boolean checkRightMoment(MessageType messageType) {
        return MessageType.retrieveByMessage(lastRequest).equals(messageType);
    }

    /**
     * Shows an error to the user when the input is invalid.
     */
    private void printInvalidMessage() {
        System.out.println(cli.colors.get(RED) + "Error invalid message" + cli.colors.get(RESET));
    }

    //TODO: javaDoc from here.
    private void parseNewGame(String[] in) {
        if (isHost && wizardsView == null && lastRequest == null) {
            if (in.length == 3) {
                Set<Integer> set = new HashSet<>();
                set.add(2);
                set.add(3);
                set.add(4);
                if (inIntegerSet(set, in[1]))
                    if (in[2].equalsIgnoreCase("n") || in[2].equalsIgnoreCase("e")) {
                        GameMode mode;
                        if (in[2].equalsIgnoreCase("n"))
                            mode = GameMode.EASY;
                        else mode = GameMode.EXPERT;
                        cli.notifyViewListener(new ChosenGame(GamePreset.getFromNumber(Integer.parseInt(in[1])), mode));
                        return;
                    }
            }
        }
        printInvalidMessage();
    }

    private void parseWizard(String[] in) {
        if (canChoseWizard) {
            if (in.length == 2) {
                Wizard w = Wizard.getWizardFromString(in[1]);
                if (w != null && wizardsView != null)
                    if (wizardsView.getAvailableWizards().contains(w)) {
                        cli.notifyViewListener(new ChosenWizard(w));
                        System.out.println("Waiting for other players");
                        return;
                    }
            }
        }
        printInvalidMessage();
    }

    private void parseTeam(String[] in) {
        if (in.length == 2) {
            String i = in[1].toUpperCase();
            if (i.equals(Tower.BLACK.toString())) {
                cli.notifyViewListener(new ChosenTeam(Tower.BLACK));
                return;
            } else if (i.equals(Tower.WHITE.toString())) {
                cli.notifyViewListener(new ChosenTeam(Tower.WHITE));
                return;
            }
        }
        printInvalidMessage();
    }

    private void parseStart(String[] in) {
        System.out.println(canStart);
        if (canStart) {
            if (in[0].equalsIgnoreCase("START")) {
                cli.notifyViewListener(new StartGame());
                return;
            }
        }
        printInvalidMessage();
    }

    private void parseAssistantCard(String[] in) {
        if (checkRightMoment(MessageType.PLAY_ASSISTANT_CARD)) {
            EnumSet<AssistantCard> playableAssistantCard = ((PlayAssistantCard) lastRequest).getPlayableAssistantCards();
            if (in.length == 2) {
                if (in[1].matches("-?\\d+")) {
                    AssistantCard as = AssistantCard.getFromInt(Integer.parseInt(in[1]));
                    if (playableAssistantCard.contains(as)) {
                        cli.notifyViewListener(new PlayedAssistantCard(as));
                        cantWrite();
                        return;
                    }
                }
            }
            printInvalidMessage();
        }
    }

    private void parseCharacterCard(String[] in) {
        Map<String, Integer> cards = cli.playableCharacterCards();
        if (cards != null)
            if (in.length == 2) {
                in[1] = in[1].toUpperCase();
                if (cards.containsKey(in[1])) {
                    cli.notifyViewListener(new ActivatedCharacterCard(cards.get(in[1])));
                    return;
                }
            }
        printInvalidMessage();
    }

    private boolean inIntegerSet(Set<Integer> available, String in) {
        if (in.matches("-?\\d+")) {
            Integer i = Integer.parseInt(in);
            return available.contains(i);
        }
        return false;
    }

    private void parseIslandChoice(String[] in) {
        if (checkRightMoment(MessageType.CHOOSE_ISLAND) || !cli.playableCharacterCards().isEmpty()) {
            if (in.length == 2) {
                if (inIntegerSet(((ChooseIsland) lastRequest).getAvailableIslandIndexes(), in[1])) {
                    cli.notifyViewListener(new ChosenIsland(Integer.parseInt(in[1])));
                    return;
                }
            }
        }
        printInvalidMessage();
    }

    private void parseCloudChoice(String[] in) {
        if (checkRightMoment(MessageType.CHOOSE_CLOUD)) {
            if (in.length == 2) {
                if (inIntegerSet(((ChooseCloud) lastRequest).getAvailableCloudIndexes(), in[1])) {
                    cli.notifyViewListener(new ChosenCloud(Integer.parseInt(in[1])));
                    return;
                }
            }
        }
        printInvalidMessage();
    }

    private void parseColor(String[] in) {
        if (checkRightMoment(MessageType.CHOOSE_STUDENT_COLOR))
            if (in.length == 2) {
                StudentColor st = StudentColor.getColorFromString(in[1]);
                if (st != null) {
                    if (((ChooseStudentColor) lastRequest).getAvailableStudentColors().contains(st)) {
                        cli.notifyViewListener(new ChosenStudentColor(st));
                        return;
                    }
                }
            }
        printInvalidMessage();
    }

    private void parseMotherNature(String[] in) {
        if (checkRightMoment(MessageType.MOVE_MOTHER_NATURE))
            if (in.length == 2) {
                if (in[1].matches("-?\\d+")) {
                    int i = Integer.parseInt(in[1]);
                    if (i <= ((MoveMotherNature) lastRequest).getMaxNumMoves() && i > 0) {
                        cli.notifyViewListener(new MovedMotherNature(i));
                        return;
                    }
                }
            }
        printInvalidMessage();
    }

    private void parseMoveChoice(String[] in) {
        MovedStudent message = checkMoveChoice(in);
        if (message != null) {
            cli.notifyViewListener(message);
            return;
        }
        printInvalidMessage();
    }

    private void parseSwapChoice(String[] in) {
        if (checkRightMoment(MessageType.SWAP_STUDENTS)) {

            SwapStudents req = ((SwapStudents) lastRequest);
            MoveLocation from = req.getFrom();
            MoveLocation to = req.getTo();
            Integer fromIndex;

            if (in.length == 3 || in.length == 2) {
                fromIndex = getSwapInt(from, in[1]);
                if (fromIndex != null) {
                    if (!to.requiresToIndex() && !to.equals(MoveLocation.HALL)) {
                        if (in.length == 2) {
                            if (req.getFromIndexesSet().contains(fromIndex)) {
                                cli.notifyViewListener(new SwappedStudents(from, fromIndex, to, null));
                                return;
                            }
                        }
                    } else {
                        if (in.length == 3) {
                            Integer toIndex = getSwapInt(to, in[2]);
                            if (toIndex != null) {
                                if (!req.getToIndexesSet().isEmpty()) {
                                    if (req.getToIndexesSet().contains(toIndex)) {
                                        cli.notifyViewListener(new SwappedStudents(from, fromIndex, to, toIndex));
                                        return;
                                    }
                                } else {
                                    System.out.println("Type CONCLUDE ");
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        printInvalidMessage();
    }

    private Integer getSwapInt(MoveLocation location, String in) {
        Integer index = null;
        if (location.equals(MoveLocation.ENTRANCE)) {
            if (in.matches("-?\\d+"))
                index = Integer.parseInt(in);
        } else {
            StudentColor st = StudentColor.getColorFromString(in);
            if (st != null) {
                index = st.ordinal();
            }
        }
        return index;
    }

    private void parseConcludeChoice() {
        if (checkRightMoment(MessageType.SWAP_STUDENTS) && canEndEffect) {
            cli.notifyViewListener(new ConcludeCharacterCardEffect());
        } else printInvalidMessage();
    }


    private boolean checkMove(MoveLocation from, int fromIndex, MoveLocation to, Integer toIndex, MoveActionRequest m) {
        return checkMove(from, fromIndex, to, toIndex, m.getFrom(), m.getTo(), m.getFromIndexesSet(), m.getToIndexesSet());
    }

    private boolean checkMove(MoveLocation from, int fromIndex, MoveLocation to, Integer toIndex, MoveLocation getFrom, MoveLocation getTo, Set<Integer> getFromIndexesSet, Set<Integer> getToIndexSet) {
        if (from.equals(getFrom))
            if (to.equals(getTo))
                if (getFromIndexesSet.contains(fromIndex))
                    if (toIndex != null && getToIndexSet != null) {
                        return getToIndexSet.contains(toIndex);
                    } else {
                        return toIndex == null && getToIndexSet == null;
                    }
        return false;
    }

    private MovedStudent checkMoveChoice(String[] in) {
        if (checkRightMoment(MessageType.MULTIPLE_POSSIBLE_MOVES) || checkRightMoment(MessageType.MOVE_STUDENT)) {
            if (in.length == 4 || in.length == 5) {
                List<MoveActionRequest> moves;
                MoveLocation from = MoveLocation.getFromString(in[1]);
                MoveLocation to = MoveLocation.getFromString(in[3]);
                if (from == null || to == null) {
                    return null;
                }
                int fromIndex;
                Integer toIndex = null;
                if (from.equals(MoveLocation.ENTRANCE)) {
                    if (in[2].matches("-?\\d+")) {
                        fromIndex = Integer.parseInt(in[2]);
                    } else {
                        return null;
                    }
                } else {
                    StudentColor st = StudentColor.getColorFromString(in[2]);
                    if (st == null) {
                        return null;
                    }
                    fromIndex = st.ordinal();
                }
                if (!to.requiresToIndex()) {
                    if (in.length == 5) {
                        return null;
                    }
                } else {
                    if (to.equals(MoveLocation.ENTRANCE) || to.equals(MoveLocation.ISLAND)) {
                        if (in.length != 5) {
                            return null;
                        }
                        if (in[4].matches("-?\\d+")) {
                            toIndex = Integer.parseInt(in[4]);
                        } else {
                            return null;
                        }
                    } else {
                        StudentColor st = StudentColor.getColorFromString(in[4]);
                        if (st == null) {
                            return null;
                        }
                        toIndex = st.ordinal();
                    }
                }
                int check = 0;
                if (checkRightMoment(MessageType.MULTIPLE_POSSIBLE_MOVES)) {
                    moves = ((MultiplePossibleMoves) lastRequest).getPossibleMoves();
                    for (MoveActionRequest m : moves) {
                        if (checkMove(from, fromIndex, to, toIndex, m)) {
                            check++;
                        }
                    }
                } else {
                    MoveStudent move = ((MoveStudent) lastRequest);
                    if (checkMove(from, fromIndex, to, toIndex, move.getFrom(), move.getTo(), move.getFromIndexesSet(), move.getToIndexesSet()))
                        check++;
                }
                if (check == 1) {
                    return new MovedStudent(from, fromIndex, to, toIndex);
                }
            }
        }
        return null;
    }

    public void setCanEndEffect(boolean b) {
        this.canEndEffect = b;
    }
}
