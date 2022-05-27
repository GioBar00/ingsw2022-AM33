package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.*;
import it.polimi.ingsw.server.model.enums.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.server.model.enums.StudentColor.*;


public class CLI implements UI {
    private final Client client;
    private String nickname;
    private final String os;
    private final Scanner input;
    private boolean firstConnection;
    private ViewListener listener;
    private TeamsView teamsView;
    private GameView gameView;
    private WizardsView wizardsView;
    private Message lastRequest;

    private ViewState lastState;
    final HashMap<String, String> colors;
    private final InputParser inputParser;
    private ExecutorService executorService;

    public CLI() {
        os = System.getProperty("os.name");
        lastState = ViewState.SETUP;
        executorService = null;
        firstConnection = true;
        colors = new HashMap<>();
        colors.put("reset", "\033[0m");
        colors.put("black", "\033[30m");
        colors.put("red", "\033[31m");
        colors.put("green", "\033[32m");
        colors.put("yellow", "\033[33m");
        colors.put("blue", "\033[34m");
        colors.put("magenta", "\033[35m");
        colors.put("cyan", "\033[36m");
        colors.put("white", "\033[37m");
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
    synchronized public void setPossibleMoves(Message message) {
        lastRequest = message;
        inputParser.setLastRequest(message);
        showPossibleMoves();
    }

    @Override
    public void serverUnavailable() {
        if (executorService == null) {
            System.out.println("We are sorry, the server is unavailable");
            System.out.println("Type a character if you want to close the application");
            String in = input.nextLine();
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

        clearTerminal();
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

    private void printRequests(){
        requestServerAddress();
        chooseNickname();
    }
    private void printGameName() {
        clearTerminal();
        System.out.println(colors.get("yellow") + "███████╗██████╗ ██╗ █████╗ ███╗   ██╗████████╗██╗   ██╗███████╗");
        System.out.println("██╔════╝██╔══██╗██║██╔══██╗████╗  ██║╚══██╔══╝╚██╗ ██╔╝██╔════╝");
        System.out.println("█████╗  ██████╔╝██║███████║██╔██╗ ██║   ██║    ╚████╔╝ ███████╗");
        System.out.println("██╔══╝  ██╔══██╗██║██╔══██║██║╚██╗██║   ██║     ╚██╔╝  ╚════██║");
        System.out.println("███████╗██║  ██║██║██║  ██║██║ ╚████║   ██║      ██║   ███████║ ");
        System.out.println("╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝   ╚══════╝ " + colors.get("reset"));
    }

    private void requestServerAddress() {
        String server;
        int port;
        do {
            do {
                System.out.print("\n" + colors.get("reset") + "Insert Server ip/address -> ");
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
            if(nickname.equals(""))
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
        clearTerminal();

        if (lastState.equals(ViewState.SETUP)) {
            showWizardMenu();
            lastState = ViewState.CHOOSE_WIZARD;
            return;
        }
        for (int i = 0; i < 4; i++) {
            System.out.println();
        }
        String[] view = buildTeamLobby();

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

        clearTerminal();

        StringBuilder builder = new StringBuilder();
        builder.append(colors.get("reset")).append("╔");
        builder.append("═".repeat(142));
        builder.append("╗");
        System.out.println(colors.get("reset") + builder);
        builder.delete(0, builder.length());
        builder.append(getFirstLine());
        builder.append(" ".repeat(142 - builder.length()));
        System.out.println("║" + colors.get("green") + builder + colors.get("reset") + "║");

        builder.delete(0, builder.length());

        builder.append("╚");
        builder.append("═".repeat(142));
        builder.append("╝");
        System.out.println(colors.get("reset") + builder);

        //show island
        ArrayList<String> islands = getIslandsLines(gameView.getIslandsView(), gameView.getMotherNatureIndex());
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
                text = colors.get("green") + text + colors.get("reset");
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
            schoolBoardRows.addAll(getSchoolBoardLines(pv.getSchoolBoardView(), gameView.getPreset()));
            schoolBoardRows.add(" ".repeat(94));
        }
        //show clouds
        ArrayList<String> clouds = getCloudsLines(gameView.getCloudViews());

        ArrayList<String> cardView = null;
        if (gameView.getMode().equals(GameMode.EXPERT)) {
            cardView = new ArrayList<>();
            for (CharacterCardView c : gameView.getCharacterCardView()) {
                cardView.addAll(getCardLines(c));
            }
        }
        Set<Integer> numClouds = new HashSet<>();
        int i = 0;
        for (CloudView ignored : gameView.getCloudViews()) {
            numClouds.add(i);
            i++;
        }

        System.out.println(schoolBoardRows.get(0));
        System.out.println(schoolBoardRows.get(1) + " ".repeat(16) + "Clouds " + buildSequence(numClouds));
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


        if (gameView.getState().equals(GameState.ENDED)) {
            if (gameView.getWinners() != null)
                System.out.println(colors.get("yellow") + gameView.getWinners().toString() + " has won" + colors.get("reset") );
            else System.out.println(colors.get("yellow") + "The game ended with a draw" + colors.get("reset"));
            System.exit(0);
        }


        showPossibleMoves();
    }

    public void showPossibleMoves() {
        if (nickname.equals(gameView.getCurrentPlayer()))
            inputParser.canWrite();
        if (lastRequest == null)
            return;
        Map<String, Integer> characterCard = playableCharacterCards();
        if (!characterCard.isEmpty()) {
            System.out.println(colors.get("green") + "Choose next action : ");
            System.out.println("Activate a CharacterCard by typing ACTIVATE <name>" + colors.get("reset"));
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
                System.out.println(colors.get("green") + "You have to chose a Cloud");
                System.out.println("Available Cloud " + buildSequence(choices));
                System.out.println("Type CLOUD <INDEX>" + colors.get("reset"));
            }
            case CHOOSE_ISLAND -> {
                Set<Integer> choices = ((ChooseIsland) lastRequest).getAvailableIslandIndexes();
                System.out.println(colors.get("green") + "You have to chose an Island");
                System.out.println("Available Island " + buildSequence(choices));
                System.out.println("Type ISLAND <INDEX>" + colors.get("reset"));
            }
            case CHOOSE_STUDENT_COLOR -> {
                EnumSet<StudentColor> choices = ((ChooseStudentColor) lastRequest).getAvailableStudentColors();
                System.out.println(colors.get("green") + "You have to chose a StudentColor");
                System.out.println("Available StudentColor " + buildSequence(choices));
                System.out.println("Type COLOR <COLOR>" + colors.get("reset"));
            }
            case MOVE_MOTHER_NATURE -> {
                System.out.println(colors.get("green") + "You have to move Mother Nature");
                System.out.println("Insert the number of steps Mother Mature has to take [up to " +
                        ((MoveMotherNature) lastRequest).getMaxNumMoves() + " ]");
                System.out.println("Type MOTHERNATURE <STEPS>" + colors.get("reset"));
            }
            case MOVE_STUDENT -> {
                if (!characterCard.isEmpty()) {
                    System.out.println(colors.get("green") + "Or select one of the following moves" + colors.get("reset"));
                }
                System.out.println(colors.get("green") + "You have to make a MOVE" + colors.get("reset"));
                printMove((MoveStudent) lastRequest);
                System.out.println(colors.get("green") + "Type MOVE <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR> " + colors.get("reset"));
            }
            case MULTIPLE_POSSIBLE_MOVES -> {
                if (!characterCard.isEmpty()) {
                    System.out.println(colors.get("green") + "Or select one of the following moves" + colors.get("reset"));
                }
                for (MoveActionRequest m : ((MultiplePossibleMoves) lastRequest).getPossibleMoves())
                    printMove(m);
                System.out.println(colors.get("green") + "Type MOVE <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR>" + colors.get("reset"));
            }
            case SWAP_STUDENTS -> {
                if (!characterCard.isEmpty()) {
                    System.out.println(colors.get("green") + "Or select one of the following moves" + colors.get("reset"));
                }
                printSwap((SwapStudents) lastRequest);
                System.out.println(colors.get("green") + "Type SWAP <INDEX/COLOR> <INDEX/COLOR>" + colors.get("reset"));
                System.out.println(colors.get("green") + "Type CONCLUDE to end the effect" + colors.get("reset"));
            }
            case PLAY_ASSISTANT_CARD -> {
                System.out.println(colors.get("green") + "Choose an AssistantCard");
                System.out.println("Cards Available :" + colors.get("reset"));
                for (AssistantCard a : ((PlayAssistantCard) lastRequest).getPlayableAssistantCards()) {
                    System.out.println("[ Card number : " + a.getValue() + " max Mother Nature moves : " + a.getMoves() + " ]");
                }
                System.out.println(colors.get("green") + "Type ASSISTANT <NUMBER>" + colors.get("reset"));
            }
            default -> {
            }
        }
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

    private void clearTerminal() {
        if (os.contains("Windows")) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Runtime.getRuntime().exec("clear");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\033[H\033[2J");
        System.out.flush();

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

    EnumSet<StudentColor> fromIntegersToEnums(Set<Integer> choices) {
        List<StudentColor> ret = new ArrayList<>();
        for (int i : choices) {
            ret.add(StudentColor.retrieveStudentColorByOrdinal(i));
        }
        return EnumSet.copyOf(ret);
    }

    <T extends Enum<T>> String buildSequence(EnumSet<T> choices) {
        StringBuilder text = new StringBuilder("[ ");
        for (T w : choices) {
            text.append(w.toString());
            text.append(" | ");
        }
        text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "]");
        return text.toString();
    }

    String buildSequence(Set<Integer> choices) {
        StringBuilder text = new StringBuilder("[ ");
        List<Integer> sorted = choices.stream().sorted().toList();
        for (Integer i : sorted) {
            text.append(i).append(" | ");
        }
        text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "]");
        return text.toString();
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

    private void printMove(MoveActionRequest move) {
        StringBuilder text = printCommonMoveParts(move);
        Set<Integer> choices = move.getToIndexesSet();
        if (choices != null)
            text.append(" ").append(buildSequence(choices));
        System.out.println(text);
    }

    private StringBuilder printCommonMoveParts(MoveActionRequest move) {
        Set<Integer> choices;
        StringBuilder text = new StringBuilder("Move from " + move.getFrom().toString());
        choices = move.getFromIndexesSet();
        if (choices != null)
            if (move.getFrom() == MoveLocation.ENTRANCE)
                text.append(" ").append(buildSequence(choices));
            else {
                text.append(" ").append(buildSequence(fromIntegersToEnums(choices)));
            }
        text.append(" to ").append(move.getTo().toString());
        return text;
    }

    private void printSwap(MoveActionRequest move) {
        StringBuilder text = printCommonMoveParts(move);
        Set<Integer> choices = move.getToIndexesSet();
        if (choices != null && !choices.isEmpty()) {
            if (move.getTo() == MoveLocation.ENTRANCE || move.getTo() == MoveLocation.ISLAND) {
                text.append(" ").append(buildSequence(choices));
            } else {
                text.append(" ").append(buildSequence(fromIntegersToEnums(choices)));
            }
        }
        System.out.println(text);
    }

    private String[] buildTeamLobby() {
        String[] view = new String[7];
        view[0] = "";
        for (int i = 0; i < 15; i++) {
            view[0] = view[0] + " ";
        }
        view[0] = view[0] + colors.get("blue") + "BLACK TEAM";
        for (int i = 0; i < 43; i++) {
            view[0] = view[0] + " ";
        }
        view[0] = view[0] + colors.get("yellow") + " LOBBY";
        for (int i = 0; i < 43; i++) {
            view[0] = view[0] + " ";
        }
        view[0] = view[0] + colors.get("cyan") + "WHITE TEAM ";
        StringBuilder sup = new StringBuilder("┌──────────────────────────────────────┐");
        String sup2 = "          ";
        view[1] = colors.get("blue") + sup + " " + sup2 + colors.get("yellow") + sup + " " + sup2;
        view[1] = view[1] + colors.get("cyan") + sup + " ";

        List<String> black = teamsView.getTeams().get(Tower.BLACK);
        List<String> white = teamsView.getTeams().get(Tower.WHITE);
        List<String> lobby = teamsView.getLobby();

        sup = new StringBuilder();
        sup.append(" ".repeat(37));

        for (int i = 0; i < 4; i++) {
            view[2 + i] = colors.get("blue") + "│ ";
            if (black.size() >= i + 1) {
                String name = black.get(i);
                int size = name.length();
                view[2 + i] = view[2 + i] + colors.get("blue") + name + " ";
                for (int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            } else {
                view[2 + i] = view[2 + i] + sup;
            }
            view[2 + i] = view[2 + i] + colors.get("blue") + "│ ";
            view[2 + i] = view[2 + i] + sup2;

            view[2 + i] = view[2 + i] + colors.get("yellow") + "│ ";
            if (lobby.size() >= i + 1) {
                String name = lobby.get(i);
                int size = name.length();
                view[2 + i] = view[2 + i] + colors.get("yellow") + name + " ";
                for (int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            } else {
                view[2 + i] = view[2 + i] + sup;
            }
            view[2 + i] = view[2 + i] + colors.get("yellow") + "│ ";

            view[2 + i] = view[2 + i] + sup2;

            view[2 + i] = view[2 + i] + colors.get("cyan") + "│ ";
            if (white.size() >= i + 1) {
                String name = white.get(i);
                int size = name.length();
                view[2 + i] = view[2 + i] + colors.get("cyan") + name + " ";
                for (int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            } else {
                view[2 + i] = view[2 + i] + sup;
            }
            view[2 + i] = view[2 + i] + colors.get("cyan") + "│ ";
        }

        sup = new StringBuilder("└──────────────────────────────────────┘");
        sup2 = "          ";
        view[6] = colors.get("blue") + sup + " " + sup2 + colors.get("yellow") + sup + " " + sup2;
        view[6] = view[6] + colors.get("cyan") + sup + " " + colors.get("reset");
        return view;
    }

    private ArrayList<String> getSchoolBoardLines(SchoolBoardView sbv, GamePreset preset) {
        ArrayList<String> schoolBoardLines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        int indexEntrance = 0;
        int countTower = 0;
        // CONTORNO SOPRA
        stringBuilder.append(colors.get("yellow")).append("╔");
        for (int i = 0; i < 92; i++) {
            if ((i == 14) || (i == 77) || (i == 67)) {
                stringBuilder.append(("╦"));
            } else {
                stringBuilder.append(("═"));
            }
        }
        stringBuilder.append("╗").append(colors.get("reset"));
        schoolBoardLines.add(0, stringBuilder.toString());

        // INTERNO RIGA PER RIGA
        for (int rows = 0; rows < 5; rows++) {
            stringBuilder.delete(0, stringBuilder.length());

            // ENTRANCE
            stringBuilder.append(colors.get("yellow")).append("║");
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }
            if (indexEntrance < preset.getEntranceCapacity() && sbv.getEntrance().get(indexEntrance) != null) {
                appendStudent(sbv.getEntrance().get(indexEntrance), stringBuilder);
            } else
                stringBuilder.append("   ");
            indexEntrance++;
            for (int i = 0; i < 4; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }
            if (indexEntrance < preset.getEntranceCapacity() && sbv.getEntrance().get(indexEntrance) != null) {
                appendStudent(sbv.getEntrance().get(indexEntrance), stringBuilder);
            } else
                stringBuilder.append("   ");
            indexEntrance++;
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }

            // HALL
            stringBuilder.append(colors.get("yellow")).append("║");
            appendHall(rows, stringBuilder, sbv.getStudentsHall());


            // PROFESSORS
            stringBuilder.append(colors.get("yellow")).append("║");
            for (int i = 0; i < 3; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }
            StudentColor currentProfLine = studentColorByRow(rows);
            if (sbv.getProfessors().contains(currentProfLine)) {
                switch (currentProfLine) {
                    case RED -> stringBuilder.append(colors.get("red")).append(" ■ ");
                    case BLUE -> stringBuilder.append(colors.get("blue")).append(" ■ ");
                    case GREEN -> stringBuilder.append(colors.get("green")).append(" ■ ");
                    case MAGENTA -> stringBuilder.append(colors.get("magenta")).append(" ■ ");
                    case YELLOW -> stringBuilder.append(colors.get("yellow")).append(" ■ ");
                }
            } else {
                stringBuilder.append("   ");
            }
            for (int i = 0; i < 3; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }

            // TOWERS
            stringBuilder.append(colors.get("yellow")).append("║");
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }
            if (countTower < sbv.getNumTowers()) {
                appendTower(sbv.getTower(), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            countTower++;
            for (int i = 0; i < 4; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }
            if (countTower < sbv.getNumTowers()) {
                appendTower(sbv.getTower(), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            countTower++;
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }
            stringBuilder.append(colors.get("yellow")).append("║");
            schoolBoardLines.add(rows + 1, stringBuilder.toString());
        }

        // CONTORNO SOTTO
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(colors.get("yellow")).append("╚");
        for (int i = 0; i < 92; i++) {
            if ((i == 14) || (i == 77) || (i == 67)) {
                stringBuilder.append("╩");
            } else {
                stringBuilder.append("═");
            }
        }
        stringBuilder.append("╝").append(colors.get("reset"));
        schoolBoardLines.add(6, stringBuilder.toString());

        // NOMI
        schoolBoardLines.add(7, "    Entrance                         Hall                            Professors    Towers" + " ".repeat(5));
        return schoolBoardLines;
    }

    private ArrayList<String> getIslandsLines(List<IslandGroupView> views, int motherNatureIndex) {
        ArrayList<String> islandsLines = new ArrayList<>(7);
        StringBuilder stringBuilder = new StringBuilder();


        appendWater(stringBuilder);
        islandsLines.add(0, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        // top border
        stringBuilder.append(colors.get("blue")).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get("green")).append("╔");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                stringBuilder.append(colors.get("green")).append("═══════");
                if (i == igv.getIslands().size() - 1)
                    stringBuilder.append(colors.get("green")).append("╗");
                else
                    stringBuilder.append(colors.get("green")).append("╦");
            }
            stringBuilder.append(colors.get("blue")).append(" ░ ");
        }


        // first line (tower + blocks)
        islandsLines.add(1, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(colors.get("blue")).append("░");
        int islandIndex = 0;
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get("green")).append("║");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                Tower tower = igv.getIslands().get(i).getTower();
                if (tower == null)
                    stringBuilder.append("   ");
                else
                    appendTower(tower, stringBuilder);
                if (i == 0 && islandIndex == motherNatureIndex)
                    stringBuilder.append(colors.get("green")).append("©");
                else
                    stringBuilder.append(" ");
                appendBlock(igv.isBlocked(), stringBuilder);
                stringBuilder.append(colors.get("green")).append("║");
            }
            stringBuilder.append(colors.get("blue")).append(" ░ ");
            islandIndex++;
        }


        // second line (blue, red and green students)
        islandsLines.add(2, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(colors.get("blue")).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get("green")).append("║ ");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                appendStudentNumber(BLUE, igv.getIslands().get(i).getStudents().get(StudentColor.BLUE), stringBuilder);
                appendStudentNumber(RED, igv.getIslands().get(i).getStudents().get(StudentColor.RED), stringBuilder);
                appendStudentNumber(GREEN, igv.getIslands().get(i).getStudents().get(GREEN), stringBuilder);
                stringBuilder.append(colors.get("green")).append("║ ");
            }
            stringBuilder.append(colors.get("blue")).append("░ ");
        }

        // third line (pink and yellow students)
        islandsLines.add(3, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(colors.get("blue")).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get("green")).append("║ ");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                appendStudentNumber(MAGENTA, igv.getIslands().get(i).getStudents().get(MAGENTA), stringBuilder);
                appendStudentNumber(YELLOW, igv.getIslands().get(i).getStudents().get(YELLOW), stringBuilder);
                stringBuilder.append("  ");
                stringBuilder.append(colors.get("green")).append("║ ");
            }
            stringBuilder.append(colors.get("blue")).append("░ ");
        }


        // bottom border
        islandsLines.add(4, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(colors.get("blue")).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get("green")).append("╚");
            for (int i = 0; i < igv.getIslands().size(); i++) {
                stringBuilder.append(colors.get("green")).append("═══════");
                if (i == igv.getIslands().size() - 1)
                    stringBuilder.append(colors.get("green")).append("╝");
                else
                    stringBuilder.append(colors.get("green")).append("╩");
            }
            stringBuilder.append(colors.get("blue")).append(" ░ ");
        }

        // water layer
        islandsLines.add(5, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        appendWater(stringBuilder);
        islandsLines.add(6, stringBuilder.toString());


        return islandsLines;
    }

    private ArrayList<String> getCardLines(CharacterCardView characterCardView) {
        ArrayList<String> cardLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        // top border
        sb.append(colors.get("red")).append("╔═══════════╗").append(colors.get("reset"));

        // name of the card
        cardLines.add(0, sb.toString());
        sb.delete(0, sb.length());
        sb.append(colors.get("red")).append("║ ").append(colors.get("reset"));
        sb.append(colors.get("red")).append(characterCardView.getType().name()).append(colors.get("reset"));

        int spacesLeft = 10 - characterCardView.getType().name().length();
        sb.append(" ".repeat(Math.max(0, spacesLeft)));
        sb.append(colors.get("red")).append("║").append(colors.get("reset"));

        // cost of the card
        cardLines.add(1, sb.toString());
        sb.delete(0, sb.length());
        sb.append(colors.get("red")).append("║ ").append(colors.get("reset"));

        int total = characterCardView.getOriginalCost() + characterCardView.getAdditionalCost();
        sb.append(colors.get("white")).append(characterCardView.getOriginalCost()).append("+").append(characterCardView.getAdditionalCost()).append(" = ").append(total).append(colors.get("reset"));
        sb.append(colors.get("red")).append("   ║").append(colors.get("reset"));

        // addictionals (students or blocks)
        cardLines.add(2, sb.toString());
        sb.delete(0, sb.length());

        // if the card has students
        if (characterCardView.getStudent() != null) {
            sb.append(colors.get("red")).append("║ ").append(colors.get("reset"));

            int studCount = 0;
            for (StudentColor sc : characterCardView.getStudent().keySet()) {
                for (int i = 0; i < characterCardView.getStudent().get(sc); i++) {
                    appendStudent(sc, sb);
                    if (studCount == 2) {
                        sb.append(colors.get("red")).append(" ║").append(colors.get("reset"));

                        cardLines.add(3, sb.toString());
                        sb.delete(0, sb.length());

                        sb.append(colors.get("red")).append("║ ").append(colors.get("reset"));
                    }
                    studCount++;
                }
            }
            if (studCount == 5) {
                sb.append("   ");
                studCount++;
            }
            if (studCount == 4) {
                sb.append("      ");
            }
            sb.append(colors.get("red")).append(" ║").append(colors.get("reset"));

            // if the card can manage blocks
        } else if (characterCardView.getType().equals(CharacterType.HERBALIST)) {
            sb.append(colors.get("red")).append("║ ").append(colors.get("reset"));

            sb.append(colors.get("white")).append("blocks: ").append(characterCardView.getNumBlocks()).append(colors.get("reset"));

            sb.append(colors.get("red")).append(" ║").append(colors.get("reset"));

            cardLines.add(3, sb.toString());
            sb.delete(0, sb.length());

            sb.append(colors.get("red")).append("║           ║").append(colors.get("reset"));
            // if the card has no addictional
        } else {
            sb.append(colors.get("red")).append("║           ║").append(colors.get("reset"));

            cardLines.add(3, sb.toString());
            sb.delete(0, sb.length());

            sb.append(colors.get("red")).append("║           ║").append(colors.get("reset"));
        }

        // bottom border
        cardLines.add(4, sb.toString());
        sb.delete(0, sb.length());

        sb.append(colors.get("red")).append("╚═══════════╝").append(colors.get("reset"));
        cardLines.add(5, sb.toString());
        sb.delete(0, sb.length());

        return cardLines;
    }

    private ArrayList<String> getCloudsLines(List<CloudView> cloudViews) {
        ArrayList<String> cloudsLines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        // top border
        stringBuilder.append(" ");
        for (CloudView ignored : cloudViews) {
            stringBuilder.append(colors.get("cyan")).append("╔══════╗").append(colors.get("reset"));
            stringBuilder.append(" ");
        }

        // first line of students
        cloudsLines.add(0, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        for (CloudView cv : cloudViews) {
            stringBuilder.append(" ");
            stringBuilder.append(colors.get("cyan")).append("║").append(colors.get("reset"));
            if (cv.getStudents().get(0) == null)
                stringBuilder.append("   ");
            else
                appendStudent(cv.getStudents().get(0), stringBuilder);
            if (cv.getStudents().get(1) == null)
                stringBuilder.append("   ");
            else
                appendStudent(cv.getStudents().get(1), stringBuilder);
            stringBuilder.append(colors.get("cyan")).append("║").append(colors.get("reset"));
        }
        stringBuilder.append(" ");

        // second line of students
        cloudsLines.add(1, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(" ");
        for (CloudView cv : cloudViews) {
            stringBuilder.append(colors.get("cyan")).append("║").append(colors.get("reset"));

            if (cv.getStudents().size() == 3) {
                if (cv.getStudents().get(2) == null)
                    stringBuilder.append("   ");
                else appendStudent(cv.getStudents().get(2), stringBuilder);
                stringBuilder.append("   ");
            } else {
                if (cv.getStudents().get(2) == null)
                    stringBuilder.append("   ");
                else appendStudent(cv.getStudents().get(2), stringBuilder);
                if (cv.getStudents().size() == 4)
                    if (cv.getStudents().get(3) == null)
                        stringBuilder.append("   ");
                    else appendStudent(cv.getStudents().get(3), stringBuilder);
            }
            stringBuilder.append(colors.get("cyan")).append("║").append(colors.get("reset"));
            stringBuilder.append(" ");
        }

        // bottom border
        cloudsLines.add(2, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(" ");
        for (CloudView ignored : cloudViews) {
            stringBuilder.append(colors.get("cyan")).append("╚══════╝").append(colors.get("reset"));
            stringBuilder.append(" ");
        }
        cloudsLines.add(3, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        return cloudsLines;
    }

    private void appendStudent(StudentColor studentColor, StringBuilder s) {
        switch (studentColor) {
            case RED -> s.append(colors.get("red")).append(" © ").append(colors.get("reset"));
            case BLUE -> s.append(colors.get("blue")).append(" © ").append(colors.get("reset"));
            case GREEN -> s.append(colors.get("green")).append(" © ").append(colors.get("reset"));
            case MAGENTA -> s.append(colors.get("magenta")).append(" © ").append(colors.get("reset"));
            case YELLOW -> s.append(colors.get("yellow")).append(" © ").append(colors.get("reset"));
        }
    }

    private void appendTower(Tower tower, StringBuilder s) {
        switch (tower) {
            case WHITE -> s.append(colors.get("reset")).append(" W ").append(colors.get("reset"));
            case BLACK -> s.append(colors.get("reset")).append(" B ").append(colors.get("reset"));
            case GREY -> s.append(colors.get("reset")).append(" G ").append(colors.get("reset"));
        }
    }

    private void appendBorder(StudentColor studentColor, StringBuilder s) {
        switch (studentColor) {
            case RED -> s.append(colors.get("red")).append("░░").append(colors.get("reset"));
            case BLUE -> s.append(colors.get("blue")).append("░░").append(colors.get("reset"));
            case GREEN -> s.append(colors.get("green")).append("░░").append(colors.get("reset"));
            case MAGENTA -> s.append(colors.get("magenta")).append("░░").append(colors.get("reset"));
            case YELLOW -> s.append(colors.get("yellow")).append("░░").append(colors.get("reset"));
        }
    }

    private void appendHall(int row, StringBuilder s, EnumMap<StudentColor, Integer> hallView) {
        StudentColor studentColor = studentColorByRow(row);
        appendBorder(studentColor, s);
        for (int j = 0; j < 10; j++) {
            if (j < hallView.get(studentColor)) {
                appendStudent(studentColor, s);
            } else {
                s.append("   ");
            }
            appendBorder(studentColor, s);
        }
    }

    private StudentColor studentColorByRow(int row) {
        StudentColor studentColor = null;
        switch (row) {
            case 0 -> studentColor = StudentColor.GREEN;
            case 1 -> studentColor = StudentColor.RED;
            case 2 -> studentColor = StudentColor.YELLOW;
            case 3 -> studentColor = StudentColor.MAGENTA;
            case 4 -> studentColor = StudentColor.BLUE;
        }
        return studentColor;
    }

    private void appendBlock(boolean isBlocked, StringBuilder s) {
        if (isBlocked)
            s.append(colors.get("red")).append(" X ").append(colors.get("reset"));
        else
            s.append("   ");
    }

    private void appendStudentNumber(StudentColor studentColor, int num, StringBuilder s) {
        if (num == 0)
            s.append("  ");
        else
            switch (studentColor) {
                case RED -> s.append(colors.get("red")).append(num).append(" ").append(colors.get("reset"));
                case BLUE -> s.append(colors.get("blue")).append(num).append(" ").append(colors.get("reset"));
                case GREEN -> s.append(colors.get("green")).append(num).append(" ").append(colors.get("reset"));
                case MAGENTA -> s.append(colors.get("magenta")).append(num).append(" ").append(colors.get("reset"));
                case YELLOW -> s.append(colors.get("yellow")).append(num).append(" ").append(colors.get("reset"));
            }
    }

    private void appendWater(StringBuilder s) {
        s.append(colors.get("blue"));
        s.append("░".repeat(144));
        s.append(colors.get("reset"));
    }

    private String getFirstLine() {
        String text = "Eriantys       Player : " + nickname;
        text = text + "   Current Player : " + gameView.getCurrentPlayer();
        if (gameView.getMode().equals(GameMode.EXPERT)) {
            text = text + "  Coins available : " + gameView.getReserve();
            text = text + " Your Coin(s) : " + gameView.getPlayerCoins().get(nickname);
        }
        return text;
    }
}

