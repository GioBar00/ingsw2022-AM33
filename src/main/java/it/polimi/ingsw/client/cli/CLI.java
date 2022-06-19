package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.Color;
import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.Winners;
import it.polimi.ingsw.network.messages.views.*;
import it.polimi.ingsw.server.model.enums.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CLI implements UI {
    private final Client client;
    private String nickname;
    private final Scanner input;
    private boolean firstConnection;
    private ViewListener listener;
    private TeamsView teamsView;
    private GameView gameView;
    private WizardsView wizardsView;
    private Message lastRequest;

    private ViewState lastState;

    final Map<String, String> colors;
    private final InputParser inputParser;
    private ExecutorService executorService;

    private final CLIPrinter cliPrinter;

    private final String GREEN = Color.GREEN.getName();
    private final String RESET = Color.RESET.getName();
    private final String YELLOW = Color.YELLOW.getName();

    public CLI() {
        lastState = ViewState.SETUP;
        executorService = null;
        firstConnection = true;
        colors = Color.getColors();
        cliPrinter = new CLIPrinter(colors);
        inputParser = new InputParser(this);
        input = new Scanner(System.in);
        client = new Client(this);
        setViewListener(client);

    }

    @Override
    public void setWizardView(WizardsView wizardsView) {
        this.wizardsView = wizardsView;
        inputParser.setWizardsView(wizardsView);
        showWizardMenu();
    }

    @Override
    synchronized public void setTeamsView(TeamsView teamsView) {
        this.teamsView = teamsView;
        showLobbyScreen();
    }

    @Override
    synchronized public void setGameView(GameView gameView) {
        this.gameView = gameView;
        showGameScreen();
    }

    @Override
    synchronized public void setPossibleActions(Message message) {
        lastRequest = message;
        inputParser.setLastRequest(message);
        showPossibleMoves();
    }

    @Override
    public void serverUnavailable() {
        if (executorService == null) {
            System.out.println("We are sorry, the server is unavailable");
            System.out.println("Type a character if you want to close the application");
            input.nextLine();
            System.exit(0);
        } else {
            System.out.println("We are sorry, the server is unavailable");
            System.out.println("Press C for close the game or R for reconnect");
            inputParser.setServerStatus(false);
        }
    }

    void sendLogin() {
        inputParser.setServerStatus(true);
        if (!client.sendLogin()) {
            showStartScreen();
        }
    }

    @Override
    public void close() {
        inputParser.cantWrite();
        System.exit(0);
    }

    @Override
    public void chooseGame() {
        inputParser.setHost();
        inputParser.canWrite();


        cliPrinter.clearTerminal();
        if (nickname.equals("ingConti")) {
            System.out.println("Welcome our Master  \n lord of the these lands \n supreme commander of every known IDE");
        } else {
            System.out.println("Welcome host");
        }

        System.out.println("Insert the type of the game by typing NEWGAME <NUM.PLAYER> <TYPE>");
        System.out.println("Choices are 2 | 3 | 4 players normal(n) | expert(e)");

        setUpInputReader();
    }

    @Override
    public void showStartScreen() {
        printGameName();
        printRequests();
    }

    private void printRequests() {
        requestServerAddress();
        chooseNickname();
    }

    private void printGameName() {
        cliPrinter.printGameName();
    }

    private void requestServerAddress() {
        String server;
        int port;
        do {
            do {
                System.out.print("\n" + colors.get(RESET) + "Insert Server ip/address -> ");
                server = input.nextLine();
            } while (!Client.validateServerString(server));
            String portString;
            do {
                System.out.print("Insert port -> ");
                portString = input.nextLine();
            } while (!Client.validateServerPort(portString));
            port = Integer.parseInt(portString);
        } while (!client.setServerAddress(server, port));
    }

    private void chooseNickname() {
        do {
            System.out.print("Choose Nickname -> ");
            nickname = input.nextLine();
            if (nickname.length() > 25)
                System.out.println("Nickname has to be shorter than 25 characters");
            if (nickname.equals(""))
                System.out.println("Nickname cannot be null");
        } while (nickname.length() > 25 || nickname.equals(""));
        client.setNickname(nickname);
        sendLogin();
    }

    private void setUpInputReader() {
        if (firstConnection) {
            executorService = Executors.newSingleThreadExecutor();
            executorService.submit(this::readInput);
            firstConnection = false;
        }
    }

    private void readInput() {
        while (!partyEnded() && !Thread.interrupted()) {
            input.reset();
            String command = input.nextLine();
            inputParser.parse(command);
        }
        input.close();
        client.closeConnection();
    }

    private boolean partyEnded() {
        if (gameView == null)
            return false;
        else {
            return gameView.getState().equals(GameState.ENDED);
        }
    }

    public void showWizardMenu() {
        if (lastState.equals(ViewState.SETUP)) {

            System.out.println();
            System.out.println();

            if (wizardsView != null) {
                System.out.println("Choose a Wizard ");
                System.out.println("Available Wizard " + wizardsView.getAvailableWizards());
                System.out.println("Type WIZARD <NAME>");
                inputParser.setCanChoseWizard(true);
                inputParser.canWrite();
            }
            setUpInputReader();
        }
    }

    @Override
    public void showLobbyScreen() {
        cliPrinter.clearTerminal();

        if (lastState.equals(ViewState.SETUP)) {
            showWizardMenu();
            lastState = ViewState.CHOOSE_WIZARD;
            return;
        }
        for (int i = 0; i < 4; i++) {
            System.out.println();
        }
        String[] view = cliPrinter.buildTeamLobby(this.teamsView);

        for (String s : view) {
            System.out.println(s);
        }

        inputParser.canWrite();
        System.out.println("Select/change team by Typing TEAM <BLACK/WHITE>");
    }

    @Override
    synchronized public void hostCanStart() {
        if (inputParser.isHost()) {
            inputParser.canWrite();
            inputParser.setCanStart(true);
            if (teamsView != null)
                showLobbyScreen();
        }
        System.out.println("The match can start now. Type START if you want to start it");
    }

    synchronized public void hostCantStart() {
        if (teamsView != null) {
            inputParser.setCanStart(false);
            showLobbyScreen();
        }
    }

    @Override
    public void showGameScreen() {
        lastState = ViewState.PLAYING;
        lastRequest = null;
        teamsView = null;
        inputParser.cantWrite();
        inputParser.setCanStart(false);
        inputParser.setCanChoseWizard(false);

        if (executorService == null && !gameView.getState().equals(GameState.ENDED)) {
            setUpInputReader();
        }

        cliPrinter.clearTerminal();

        StringBuilder builder = new StringBuilder();
        builder.append(colors.get(RESET)).append("╔");
        builder.append("═".repeat(142));
        builder.append("╗");
        System.out.println(colors.get(RESET) + builder);
        builder.delete(0, builder.length());
        builder.append(cliPrinter.getFirstLine(nickname, gameView));
        builder.append(" ".repeat(142 - builder.length()));
        System.out.println("║" + colors.get(GREEN) + builder + colors.get(RESET) + "║");

        builder.delete(0, builder.length());

        builder.append("╚");
        builder.append("═".repeat(142));
        builder.append("╝");
        System.out.println(colors.get(RESET) + builder);

        //show island
        ArrayList<String> islands = cliPrinter.getIslandsLines(gameView.getIslandsView(), gameView.getMotherNatureIndex());
        for (String line : islands) {
            System.out.println(line);
        }

        System.out.println("\n");

        //show details
        //show school boards
        ArrayList<String> schoolBoardRows = new ArrayList<>();
        for (PlayerView pv : gameView.getPlayersView()) {
            String text;
            if (pv.getNickname().equals(nickname)) {
                text = "Your School Board   ";
                text = text + " ".repeat(94 - text.length());
                text = colors.get(GREEN) + text + colors.get(RESET);
            } else {
                text = pv.getNickname() + "'s School Board  ";
                text = text + " ".repeat(94 - text.length());
            }
            schoolBoardRows.add(text);
            text = "Tower : " + pv.getSchoolBoardView().getTower().toString() + "    ";
            if (pv.getPlayedCard() != null) {
                text = text + "Last Played Card : Value [" + pv.getPlayedCard().getValue() + "] Moves [" + pv.getPlayedCard().getMoves() + "]     ";
            }
            if (gameView.getMode().equals(GameMode.EXPERT)) {
                text = text + "Coin(s) : " + gameView.getPlayerCoins().get(pv.getNickname());
            }
            text = text + " ".repeat(94 - text.length());
            schoolBoardRows.add(text);
            schoolBoardRows.addAll(cliPrinter.getSchoolBoardLines(pv.getSchoolBoardView(), gameView.getPreset()));
            schoolBoardRows.add(" ".repeat(94));
        }
        //show clouds
        ArrayList<String> clouds = cliPrinter.getCloudsLines(gameView.getCloudViews());

        ArrayList<String> cardView = null;
        if (gameView.getMode().equals(GameMode.EXPERT)) {
            cardView = new ArrayList<>();
            for (CharacterCardView c : gameView.getCharacterCardView()) {
                cardView.addAll(cliPrinter.getCardLines(c));
            }
        }
        Set<Integer> numClouds = new HashSet<>();
        int i = 0;
        for (CloudView ignored : gameView.getCloudViews()) {
            numClouds.add(i);
            i++;
        }

        System.out.println(schoolBoardRows.get(0));
        System.out.println(schoolBoardRows.get(1) + " ".repeat(16) + "Clouds " + cliPrinter.buildSequence(numClouds));
        for (i = 0; i < 4; i++) {
            System.out.println(schoolBoardRows.get(2 + i) + " ".repeat(15) + clouds.get(i));
        }
        System.out.println(schoolBoardRows.get(6));
        System.out.println(schoolBoardRows.get(7));

        if (cardView != null) {
            System.out.println(schoolBoardRows.get(8) + " ".repeat(16) + "Cards");
            for (int j = 0; j < 6; j++)
                System.out.println(schoolBoardRows.get(9 + j) + " ".repeat(16) + cardView.get(j) + cardView.get(j + 6));
            for (int j = 0; j < 6; j++)
                System.out.println(schoolBoardRows.get(15 + j) + " ".repeat(16 + 13) + cardView.get(12 + j));
            for (i = 21; i < schoolBoardRows.size(); i++)
                System.out.println(schoolBoardRows.get(i));
        } else {
            for (i = 8; i < schoolBoardRows.size(); i++)
                System.out.println(schoolBoardRows.get(i));
        }

        showPossibleMoves();
    }

    /**
     * This method shows the winners of the game.
     *
     * @param winners a {@link Winners} message containing the winners.
     */
    public void showWinners(Winners winners) {
        if (winners.getWinners() != null)
            System.out.println(colors.get(YELLOW) + winners.getWinners().toString() + " has won" + colors.get(RESET));
        else System.out.println(colors.get(YELLOW) + "The game ended with a draw" + colors.get(RESET));
        System.exit(0);
    }

    public void showPossibleMoves() {
        if (nickname.equals(gameView.getCurrentPlayer()))
            inputParser.canWrite();

        if (lastRequest == null)
            return;

        Map<String, Integer> characterCard = playableCharacterCards();

        boolean canEndEffect = checkCanEndEffect();

        if (!characterCard.isEmpty()) {
            System.out.println(colors.get(GREEN) + "Choose next action : ");
            System.out.println("Activate a CharacterCard by typing ACTIVATE <name>" + colors.get(RESET));
            StringBuilder text = new StringBuilder("[ ");
            for (String s : characterCard.keySet())
                text.append(s.toUpperCase()).append(" | ");
            text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "] ");
            System.out.println("Available Character Card " + text);
        }
        System.out.println(MessageBuilder.toJson(lastRequest));
        switch (MessageType.retrieveByMessage(lastRequest)) {
            case CHOOSE_CLOUD -> {
                Set<Integer> choices = ((ChooseCloud) lastRequest).getAvailableCloudIndexes();
                System.out.println(colors.get(GREEN) + "You have to chose a Cloud");
                System.out.println("Available Cloud " + cliPrinter.buildSequence(choices));
                System.out.println("Type CLOUD <INDEX>" + colors.get(RESET));
            }
            case CHOOSE_ISLAND -> {
                Set<Integer> choices = ((ChooseIsland) lastRequest).getAvailableIslandIndexes();
                System.out.println(colors.get(GREEN) + "You have to chose an Island");
                System.out.println("Available Island " + cliPrinter.buildSequence(choices));
                System.out.println("Type ISLAND <INDEX>" + colors.get(RESET));
            }
            case CHOOSE_STUDENT_COLOR -> {
                EnumSet<StudentColor> choices = ((ChooseStudentColor) lastRequest).getAvailableStudentColors();
                System.out.println(colors.get(GREEN) + "You have to chose a StudentColor");
                System.out.println("Available StudentColor " + cliPrinter.buildSequence(choices));
                System.out.println("Type COLOR <COLOR>" + colors.get(RESET));
            }
            case MOVE_MOTHER_NATURE -> {
                System.out.println(colors.get(GREEN) + "You have to move Mother Nature");
                System.out.println("Insert the number of steps Mother Mature has to take [up to " +
                        ((MoveMotherNature) lastRequest).getMaxNumMoves() + " ]");
                System.out.println("Type MOTHERNATURE <STEPS>" + colors.get(RESET));
            }
            case MOVE_STUDENT -> {
                if (!characterCard.isEmpty()) {
                    System.out.println(colors.get(GREEN) + "Or select one of the following moves" + colors.get(RESET));
                }
                System.out.println(colors.get(GREEN) + "You have to make a MOVE" + colors.get(RESET));
                cliPrinter.printMove((MoveStudent) lastRequest);
                System.out.println(colors.get(GREEN) + "Type MOVE <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR> " + colors.get(RESET));
            }
            case MULTIPLE_POSSIBLE_MOVES -> {
                if (!characterCard.isEmpty()) {
                    System.out.println(colors.get(GREEN) + "Or select one of the following moves" + colors.get(RESET));
                }
                for (MoveActionRequest m : ((MultiplePossibleMoves) lastRequest).getPossibleMoves())
                    cliPrinter.printMove(m);
                System.out.println(colors.get(GREEN) + "Type MOVE <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR>" + colors.get(RESET));
            }
            case SWAP_STUDENTS -> {
                if (!characterCard.isEmpty()) {
                    System.out.println(colors.get(GREEN) + "Or select one of the following moves" + colors.get(RESET));
                }
                cliPrinter.printSwap((SwapStudents) lastRequest);
                System.out.println(colors.get(GREEN) + "Type SWAP <INDEX/COLOR> <INDEX/COLOR>" + colors.get(RESET));

                if (canEndEffect)
                    System.out.println(colors.get(GREEN) + "Type CONCLUDE to end the effect" + colors.get(RESET));
            }
            case PLAY_ASSISTANT_CARD -> {
                System.out.println(colors.get(GREEN) + "Choose an AssistantCard");
                System.out.println("Cards Available :" + colors.get(RESET));
                for (AssistantCard a : ((PlayAssistantCard) lastRequest).getPlayableAssistantCards()) {
                    System.out.println("[ Card number : " + a.getValue() + " max Mother Nature moves : " + a.getMoves() + " ]");
                }
                System.out.println(colors.get(GREEN) + "Type ASSISTANT <NUMBER>" + colors.get(RESET));
            }
            default -> {
            }
        }
    }

    private boolean checkCanEndEffect() {
        boolean canEnd = false;

        if (gameView != null)
            if (gameView.getMode() == GameMode.EXPERT)
                for (CharacterCardView c : gameView.getCharacterCardView())
                    if (c.canEndEffect() && c.isActivating()) {
                        canEnd = true;
                        break;
                    }


        inputParser.setCanEndEffect(canEnd);
        return canEnd;
    }

    @Override
    public void showCommMessage(CommMessage message) {
        if (lastState.equals((ViewState.SETUP)) && message.getType().equals(CommMsgType.ERROR_NICKNAME_UNAVAILABLE)) {
            System.out.println("\n" + message.getType().getMessage());
            System.out.println("Try to reconnect with a different nickname");
            return;
        }
        if (lastState.equals(ViewState.SETUP) && message.getType().equals(CommMsgType.ERROR_NO_SPACE)) {
            System.out.println("Sorry the match is full");
            System.exit(0);
            return;
        }
        if ((lastState.equals(ViewState.SETUP) || lastState.equals(ViewState.CHOOSE_WIZARD)) && message.getType().equals(CommMsgType.OK)) {
            System.out.println("Waiting for other players...");
            lastState = ViewState.CHOOSE_WIZARD;
            return;
        }
        if (lastState.equals(ViewState.CHOOSE_WIZARD) && message.getType().equals(CommMsgType.ERROR_IMPOSSIBLE_MOVE)) {
            lastState = ViewState.SETUP;
            return;
        }
        if (lastState.equals(ViewState.PLAYING)) {
            System.out.println("\n" + message.getType().getMessage());
            this.showPossibleMoves();
            return;
        }
        System.out.println("\n" + message.getType().getMessage());
    }


    /**
     * Sets the view listener.
     *
     * @param listener the listener to set
     */
    @Override
    public void setViewListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Notifies the listener that a request has occurred.
     *
     * @param message the request to notify
     */
    @Override
    public void notifyViewListener(Message message) {
        listener.onMessage(message);
    }


    Map<String, Integer> playableCharacterCards() {
        if (gameView != null)
            if (gameView.getMode().equals(GameMode.EXPERT)) {
                List<CharacterCardView> ch = gameView.getCharacterCardView();
                Map<String, Integer> playable = new HashMap<>();
                int i = 0;
                boolean isActive = false;
                for (CharacterCardView c : ch) {
                    if (c.canBeUsed())
                        playable.put(c.getType().toString(), i);
                    i++;
                    if (c.isActivating()) {
                        isActive = true;
                        break;
                    }
                }
                if (!isActive && !playable.isEmpty())
                    return playable;
            }
        return new HashMap<>();
    }


}

