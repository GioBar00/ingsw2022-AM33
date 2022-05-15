package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.CurrentGameState;
import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.network.messages.views.*;
import it.polimi.ingsw.server.model.enums.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.server.model.enums.StudentColor.*;


public class CLI implements UI {
    private Client client;
    private String nickname;
    private final String os;
    private final Scanner input;
    private String server;
    private int port;
    private boolean firstConnection;
    private ViewListener listener;
    private TeamsView teamsView;
    private GameView gameView;
    private WizardsView wizardsView;
    private Message lastRequest;

    private ViewState lastState;
    final HashMap<String , String> colors;
    private final InputParser inputParser;
    private ExecutorService executorService;
    public CLI(){
        os = System.getProperty("os.name");
        lastState = ViewState.SETUP;
        firstConnection = true;
        colors = new HashMap<>();
        colors.put("reset","\033[0m");
        colors.put("black","\033[30m");
        colors.put("red","\033[31m");
        colors.put("green","\033[32m");
        colors.put("yellow","\033[33m");
        colors.put("blue","\033[34m");
        colors.put("magenta","\033[35m");
        colors.put("cyan","\033[36m");
        colors.put("white", "\033[37m");
        inputParser = new InputParser(this);
        input = new Scanner(System.in);
    }

    @Override
    public void setWizardView(WizardsView wizardsView) {
        this.wizardsView = wizardsView;
        inputParser.setWizardsView(wizardsView);
    }

    @Override
    public void setTeamsView(TeamsView teamsView) {
        this.teamsView = teamsView;
        showLobbyScreen();
    }

    @Override
    synchronized public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    @Override
    synchronized public void setPossibleMoves(Message message){
        lastRequest = message;
        inputParser.setLastRequest(message);
        showPossibleMoves();
    }

    @Override
    public void close() {
        inputParser.cantWrite();
        System.out.println("We are sorry, the server is unavailable");
        System.out.println("Press c for closing the game");
        executorService.shutdownNow();
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

        setUpFirstConnection();

    }

    @Override
    public void showStartScreen() {
        clearTerminal();
        System.out.println("\033[33m" +"███████╗██████╗ ██╗ █████╗ ███╗   ██╗████████╗██╗   ██╗███████╗");
        System.out.println("\033[33m" + "██╔════╝██╔══██╗██║██╔══██╗████╗  ██║╚══██╔══╝╚██╗ ██╔╝██╔════╝");
        System.out.println("\033[33m" + "█████╗  ██████╔╝██║███████║██╔██╗ ██║   ██║    ╚████╔╝ ███████╗");
        System.out.println("\033[33m" +"██╔══╝  ██╔══██╗██║██╔══██║██║╚██╗██║   ██║     ╚██╔╝  ╚════██║");
        System.out.println("\033[33m"+"███████╗██║  ██║██║██║  ██║██║ ╚████║   ██║      ██║   ███████║ ");
        System.out.println("\033[33m"+"╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝   ╚══════╝ ");

        System.out.print("\n" + "\033[0m" +"Insert Server name -> ");

        server = input.nextLine();
        System.out.print("Insert port -> ");
        port = Integer.parseInt(input.nextLine());
        chooseNickname();
    }

    private void chooseNickname(){
        System.out.print("Choose Nickname -> ");
        nickname = input.nextLine();
        client = new Client(server, port, this);
        notifyListener(new Login(nickname));
    }

    private void setUpFirstConnection(){
        if(firstConnection){
            executorService = Executors.newSingleThreadExecutor();
            executorService.submit(this::readInput);
            firstConnection = false;
        }
    }

    private void readInput(){
        while(!partyEnded() && !Thread.interrupted()){
            input.reset();
            String command = input.nextLine();
            inputParser.parse(command);
        }
        input.close();
        client.closeConnection();
    }

    private boolean partyEnded(){
        if(gameView == null)
            return false;
        else{
            return gameView.getState().equals(GameState.ENDED);
        }
    }
    public void showWizardMenu(){
        if(lastState.equals(ViewState.SETUP)) {

            System.out.println();
            System.out.println();

            if (wizardsView != null) {
                System.out.println("Choose a Wizard ");
                System.out.println("Available Wizard " + wizardsView.getAvailableWizards());
                System.out.println("Type WIZARD <NAME>");
                inputParser.setCanChoseWizard(true);
                inputParser.canWrite();
            }
            setUpFirstConnection();
        }
    }
    @Override
    public void showLobbyScreen() {
        clearTerminal();

        System.out.println(MessageBuilder.toJson(new CurrentTeams(teamsView)));
        if(lastState.equals(ViewState.SETUP)){
            showWizardMenu();
            lastState = ViewState.CHOOSE_WIZARD;
            return;
        }
        for(int i = 0; i < 4;i++){
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
        if(inputParser.isHost()) {
            inputParser.canWrite();
            inputParser.setCanStart(true);
            System.out.println("The match can start now. Type START if you want to start it");
            if(teamsView != null)
                showLobbyScreen();
        }
    }

    synchronized public void hostCantStart(){
        if(teamsView != null) {
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
        clearTerminal();
        System.out.println(MessageBuilder.toJson(new CurrentGameState(gameView)));
        //show island
        ArrayList<String> islands = getIslandsLines(gameView.getIslandsView(), gameView.getMotherNatureIndex());
        for (String line : islands) {
            System.out.println(line);
        }
        //show details
        //show school boards
        for (PlayerView pv : gameView.getPlayersView()){
            ArrayList<String> board = getSchoolBoardLines(pv.getSchoolBoardView(), gameView.getPreset());
            for (String line : board) {
                System.out.println(line);
            }
        }
        showPossibleMoves();
    }

    public void showPossibleMoves() {
        if(nickname.equals(gameView.getCurrentPlayer()))
            inputParser.canWrite();
        if(lastRequest == null)
            return;
        Map<String, Integer> characterCard = playableCharacterCards();
        if (characterCard != null && !(MessageType.retrieveByMessage(lastRequest).equals(MessageType.MOVE_MOTHER_NATURE) || MessageType.retrieveByMessage(lastRequest).equals(MessageType.CHOOSE_CLOUD))){
            System.out.println(colors.get("green") + "Choose next action : ");
            System.out.println("Activate a CharacterCard by typing ACTIVE <name>" + colors.get("reset"));
            StringBuilder text = new StringBuilder("[ ");
            for(String s : characterCard.keySet())
                text.append(s.toUpperCase()).append(" |");
            text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "] ");
            System.out.println("Available Character Card " + text );
        }
        System.out.println(MessageBuilder.toJson(lastRequest));
        switch(MessageType.retrieveByMessage(lastRequest)){
            case CHOOSE_CLOUD -> {
                Set<Integer> choices = ((ChooseCloud)lastRequest).getAvailableCloudIndexes();
                System.out.println(colors.get("green") + "You have to chose a Cloud");
                System.out.println("Available Cloud " + buildSequence(choices));
                System.out.println("Type CLOUD <INDEX>" + colors.get("reset"));
            }
            case CHOOSE_ISLAND ->{
                Set<Integer> choices = ((ChooseIsland)lastRequest).getAvailableIslandIndexes();
                System.out.println(colors.get("green") + "You have to chose an Island");
                System.out.println("Available Island " + buildSequence(choices));
                System.out.println("Type ISLAND <INDEX>" + colors.get("reset"));
            }
            case CHOOSE_STUDENT_COLOR -> {
                EnumSet<StudentColor> choices = ((ChooseStudentColor)lastRequest).getAvailableStudentColors();
                System.out.println(colors.get("green") + "You have to chose a StudentColor");
                System.out.println("Available StudentColor " + buildSequence(choices));
                System.out.println("Type COLOR <COLOR>"+ colors.get("reset"));
            }
            case MOVE_MOTHER_NATURE -> {
                System.out.println(colors.get("green") + "You have to chose a StudentColor");
                System.out.println("Insert the number of steps mother nature has to take [up to " +
                ((MoveMotherNature)lastRequest).getMaxNumMoves() + " ]");
                System.out.println("Type MOTHERNATURE <STEPS>"+ colors.get("reset"));
            }
            case MOVE_STUDENT -> {
                if(characterCard != null){
                    System.out.println(colors.get("green") + "Or select one of the following moves"+ colors.get("reset"));
                }
                System.out.println(colors.get("green") + "You have to make a MOVE"+ colors.get("reset"));
                printMove((MoveStudent)lastRequest);
                System.out.println(colors.get("green") + "Type MOVE <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR> "+ colors.get("reset"));
            }
            case MULTIPLE_POSSIBLE_MOVES -> {
                if(characterCard != null){
                    System.out.println(colors.get("green") + "Or select one of the following moves"+ colors.get("reset"));
                }
                for(MoveActionRequest m : ((MultiplePossibleMoves)lastRequest).getPossibleMoves())
                    printMove(m);
                System.out.println(colors.get("green") +"Type MOVE <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR>"+ colors.get("reset"));
            }
            case SWAP_STUDENTS -> {
                if(characterCard != null){
                    System.out.println(colors.get("green") + "Or select one of the following moves"+ colors.get("reset"));
                }
                printSwap((SwapStudents)lastRequest);
                System.out.println(colors.get("green") +"Type SWAP <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR>"+ colors.get("reset"));
            }
            case PLAY_ASSISTANT_CARD -> {
                System.out.println(colors.get("green") + "Choose an AssistantCard");
                System.out.println("Cards Available :"+ colors.get("reset"));
                for(AssistantCard a : ((PlayAssistantCard)lastRequest).getPlayableAssistantCards()){
                    System.out.println("[ Card number : "+ a.getValue() + " max Mother Nature moves : " + a.getMoves() +" ]");
                }
                System.out.println(colors.get("green") + "Type ASSISTANT <NUMBER>"+ colors.get("reset"));
            }
            default -> {}
        }
    }


    @Override
    public void updateGameView() {

    }

    @Override
    public void updateLobbyView() {

    }

    @Override
    public void showCommMessage(CommMessage message) {
        if(lastState.equals((ViewState.SETUP)) && message.getType().equals(CommMsgType.ERROR_NICKNAME_UNAVAILABLE)){
            System.out.println("\n"+ message.getType().getMessage());
            client.closeConnection();
            chooseNickname();
            return;
        }
        if(lastState.equals(ViewState.SETUP) && message.getType().equals(CommMsgType.ERROR_NO_SPACE)){
            System.out.println("Sorry the match is full");
            client.closeConnection();
            executorService.shutdownNow();
            return;
        }
        if((lastState.equals(ViewState.SETUP)  || lastState.equals(ViewState.CHOOSE_WIZARD))&& message.getType().equals(CommMsgType.OK)){
            lastState = ViewState.CHOOSE_WIZARD;
            return;
        }
        if(lastState.equals(ViewState.CHOOSE_WIZARD) && message.getType().equals(CommMsgType.ERROR_IMPOSSIBLE_MOVE)){
            lastState = ViewState.SETUP;
            return;
        }
        if(lastState.equals(ViewState.PLAYING)){
            System.out.println("\n"+ message.getType().getMessage());
            this.showPossibleMoves();
            return;
        }
        System.out.println("\n"+ message.getType().getMessage());
    }

    private void clearTerminal() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        /*
        if (os.contains("Windows")) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                Runtime.getRuntime().exec("clear");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\033[H\033[2J");
        System.out.flush();

         */
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
     * @param event the request to notify
     */
    @Override
    public void notifyListener(Message event) {
            listener.onMessage(event);
    }

    EnumSet<StudentColor> fromIntegersToEnums(Set<Integer> choices){
        List<StudentColor> ret= new ArrayList<>();
        for(int i : choices){
            ret.add(StudentColor.retrieveStudentColorByOrdinal(i));
        }
        return EnumSet.copyOf(ret);
    }

    <T extends Enum<T>> String buildSequence(EnumSet<T> choices){
        StringBuilder text = new StringBuilder("[ ");
        for (T w : choices) {
            text.append(w.toString());
            text.append(" | ");
        }
        text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "]");
        return text.toString();
    }

    String buildSequence(Set<Integer> choices){
        StringBuilder text = new StringBuilder("[ ");
        for(Integer i : choices){
            text.append(i).append(" | ");
        }
        text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "]");
        return text.toString();
    }

    Map<String,Integer> playableCharacterCards(){
        if(gameView != null)
            if(gameView.getMode().equals(GameMode.EXPERT)){
                List <CharacterCardView> ch= gameView.getCharacterCardView();
                Map <String, Integer> playable = new HashMap<>();
                int i = 0;
                boolean isActive = false;
                for(CharacterCardView c : ch){
                    if(c.canBeUsed())
                        playable.put(c.getType().toString(),i);
                    i++;
                    if(c.isActivating()){
                        isActive = true;
                        break;
                    }
                }
                if(!isActive && !playable.isEmpty())
                    return playable;
            }
        return null;
    }

    private void printMove(MoveActionRequest move){
        StringBuilder text = printCommonMoveParts(move);
        Set<Integer> choices = move.getToIndexesSet();
        if(choices!= null)
            text.append(" ").append(buildSequence(choices));
        System.out.println(text);
    }

    private StringBuilder printCommonMoveParts(MoveActionRequest move){
        Set<Integer> choices;
        StringBuilder text = new StringBuilder("Move from " + move.getFrom().toString());
        choices = move.getFromIndexesSet();
        if(choices!= null)
            if(move.getFrom() == MoveLocation.ENTRANCE)
                text.append(" ").append(buildSequence(choices));
            else{
                text.append(" ").append(buildSequence(fromIntegersToEnums(choices)));
            }
        text.append(" to ").append(move.getTo().toString());
        return text;
    }
    private void printSwap(MoveActionRequest move){
        StringBuilder text = printCommonMoveParts(move);
        Set<Integer> choices = move.getToIndexesSet();
        if(choices!= null) {
            if (move.getTo() == MoveLocation.ENTRANCE || move.getTo() == MoveLocation.ISLAND) {
                text.append(" ").append(buildSequence(choices));
            } else {
                text.append(" ").append(buildSequence(fromIntegersToEnums(choices)));
            }
        }
        System.out.println(text);
    }

    private String[] buildTeamLobby(){
        String[] view = new String[7];
        view[0] = "";
        for(int i = 0; i < 15; i++){
            view[0] = view[0] +" ";
        }
        view[0] = view[0] + colors.get("blue") + "BLACK TEAM";
        for(int i = 0; i < 43; i++){
            view[0] = view[0] +" ";
        }
        view[0] = view[0] + colors.get("yellow") + " LOBBY";
        for(int i = 0; i < 43; i++){
            view[0] = view[0] +" ";
        }
        view[0] = view [0] + colors.get("cyan") + "WHITE TEAM ";
        StringBuilder sup = new StringBuilder("┌──────────────────────────────────────┐");
        String sup2 = "          ";
        view[1] = colors.get("blue") + sup +" "+ sup2 + colors.get("yellow") + sup +" " + sup2;
        view[1] = view[1] + colors.get("cyan") + sup +" ";

        List<String> black = teamsView.getTeams().get(Tower.BLACK);
        List<String> white = teamsView.getTeams().get(Tower.WHITE);
        List<String> lobby = teamsView.getLobby();

        sup = new StringBuilder();
        sup.append(" ".repeat(37));

        for(int i = 0; i < 4 ; i ++){
            view[2 + i] = colors.get("blue") +"│ ";
            if(black.size() > 0){
                String name = black.remove(0);
                int size = name.length();
                view[2 + i] = view[2 + i] + colors.get("blue") + name +" ";
                for(int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            }else{ view[2 + i] = view[2 + i] + sup; }
            view[2 + i] = view[2 + i] + colors.get("blue") +"│ ";
            view[2 + i] = view[2 + i] + sup2;

            view[2 + i] = view[2 + i] + colors.get("yellow") +"│ ";
            if(lobby.size()>0){
                String name = lobby.remove(0);
                int size = name.length();
                view[2 + i] = view[2 + i] + colors.get("yellow") + name +" ";
                for(int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            }else{ view[2 + i] = view[2 + i] + sup; }
            view[2 + i]= view[2 + i] + colors.get("yellow")+ "│ ";

            view[2 + i] = view[2 + i] + sup2;

            view[2 + i] = view[2 + i] + colors.get("cyan")+ "│ ";
            if(white.size()>0){
                String name = white.remove(0);
                int size = name.length();
                view[2 + i] = view[2 + i] + colors.get("cyan") + name +" ";
                for(int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            }else{ view[2 + i] = view[2 + i] + sup; }
            view[2 + i]= view[2 + i] + colors.get("cyan") + "│ ";
        }

        sup = new StringBuilder("└──────────────────────────────────────┘");
        sup2 = "          ";
        view[6] = colors.get("blue") +  sup +" "+ sup2 + colors.get("yellow") + sup +" " + sup2;
        view[6] = view[6] + colors.get("cyan") + sup +" " + colors.get("reset");
        return view;
    }

    private ArrayList<String> getSchoolBoardLines(SchoolBoardView sbv, GamePreset preset){
        ArrayList<String> schoolBoardLines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        int indexEntrance = 0;
        int countTower = 0;
        // CONTORNO SOPRA
        stringBuilder.append(colors.get("yellow")).append("╔");
        for(int i = 0; i < 92; i ++){
            if ((i == 14) || (i == 77) || (i == 67)) {
                stringBuilder.append(("╦"));
            } else {
                stringBuilder.append(("═"));
            }
        }
        stringBuilder.append("╗").append(colors.get("reset"));
        schoolBoardLines.add(0, stringBuilder.toString());

        // INTERNO RIGA PER RIGA
        for(int rows = 0; rows < 5; rows ++) {
            stringBuilder.delete(0, stringBuilder.length());

            // ENTRANCE
            stringBuilder.append(colors.get("yellow")).append("║");
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }
            if (indexEntrance < preset.getEntranceCapacity() && sbv.getEntrance().get(indexEntrance) != null){
                appendStudent(sbv.getEntrance().get(indexEntrance), stringBuilder);
            } else
                stringBuilder.append("   ");
            indexEntrance++;
            for (int i = 0; i < 4; i++) {
                stringBuilder.append(colors.get("white")).append("░");
            }
            if (indexEntrance < preset.getEntranceCapacity() && sbv.getEntrance().get(indexEntrance) != null){
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
            StudentColor currentProfLine = null;
            switch (rows){
                case 0 -> currentProfLine = StudentColor.GREEN;
                case 1 -> currentProfLine = StudentColor.RED;
                case 2 -> currentProfLine = StudentColor.YELLOW;
                case 3 -> currentProfLine = StudentColor.PINK;
                case 4 -> currentProfLine = StudentColor.BLUE;
            }
            if (sbv.getProfessors().contains(currentProfLine)) {
                switch (currentProfLine){
                    case RED -> stringBuilder.append(colors.get("red")).append(" ■ ");
                    case BLUE -> stringBuilder.append(colors.get("blue")).append(" ■ ");
                    case GREEN -> stringBuilder.append(colors.get("green")).append(" ■ ");
                    case PINK -> stringBuilder.append(colors.get("magenta")).append(" ■ ");
                    case YELLOW -> stringBuilder.append(colors.get("yellow")).append(" ■ ");
                }
            } else {
                stringBuilder.append("   ");
            }
            for(int i = 0; i < 3; i ++){
                stringBuilder.append(colors.get("white")).append("░");
            }

            // TOWERS
            stringBuilder.append(colors.get("yellow")).append("║");
            for(int i = 0; i < 2; i ++){
                stringBuilder.append(colors.get("white")).append("░");
            }
            if (countTower < sbv.getNumTowers()) {
                appendTower(sbv.getTower(), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            countTower ++;
            for(int i = 0; i < 4; i ++){
                stringBuilder.append(colors.get("white")).append("░");
            }
            if (countTower < sbv.getNumTowers()) {
                appendTower(sbv.getTower(), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            countTower ++;
            for(int i = 0; i < 2; i ++){
                stringBuilder.append(colors.get("white")).append("░");
            }
            stringBuilder.append(colors.get("yellow")).append("║");
            schoolBoardLines.add(rows+1, stringBuilder.toString());
        }

        // CONTORNO SOTTO
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(colors.get("yellow")).append("╚");
        for(int i = 0; i < 92; i ++){
            if ((i == 14) || (i == 77) || (i == 67)) {
                stringBuilder.append("╩");
            } else {
                stringBuilder.append("═");
            }
        }
        stringBuilder.append("╝").append(colors.get("reset"));
        schoolBoardLines.add(6, stringBuilder.toString());

        // NOMI
        schoolBoardLines.add(7, "  Entrance\t\t\tHall\t\t\t   Professors\t\s\s\sTowers");

        return schoolBoardLines;
    }

    private ArrayList<String> getIslandsLines(List<IslandGroupView> views, int motherNatureIndex){
        ArrayList<String> islandsLines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        // water layer
        appendWater(stringBuilder);

        islandsLines.add(0, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        // top border
        stringBuilder.append(colors.get("blue")).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get("green")).append("╔");
            for(int i = 0; i < igv.getIslands().size(); i++){
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
            for(int i = 0; i < igv.getIslands().size(); i++) {
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
                stringBuilder.append(colors.get("green")).append("║ ");
            }
            stringBuilder.append(colors.get("blue")).append("░ ");
            islandIndex ++;
        }


        // second line (blue, red and green students)
        islandsLines.add(2, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(colors.get("blue")).append("░");
        for (IslandGroupView igv : views) {
            stringBuilder.append(colors.get("green")).append("║ ");
            for(int i = 0; i < igv.getIslands().size(); i++) {
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
        for (IslandGroupView igv : views){
            stringBuilder.append(colors.get("green")).append("║ ");
            for(int i = 0; i < igv.getIslands().size(); i++) {
                appendStudentNumber(PINK, igv.getIslands().get(i).getStudents().get(PINK), stringBuilder);
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
            for(int i = 0; i < igv.getIslands().size(); i++){
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
        islandsLines.add(6,stringBuilder.toString());

        return islandsLines;
    }

    private ArrayList<String> getCardLines(CharacterCardView characterCardView){
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
        sb.append(colors.get("red")).append(" ║").append(colors.get("reset"));

        // addictionals (students or blocks)
        cardLines.add(2, sb.toString());
        sb.delete(0, sb.length());

        // if the card has students
        if (characterCardView.getStudent() != null){
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
            if (studCount < 6){
                sb.append("   ");
                studCount++;
            }
            sb.append(colors.get("red")).append(" ║").append(colors.get("reset"));

            // if the card can manage blocks
        } else if (characterCardView.getType().equals(CharacterType.HERBALIST)){
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

    private void appendStudent(StudentColor studentColor, StringBuilder s){
        switch (studentColor) {
            case RED -> s.append(colors.get("red")).append(" © ").append(colors.get("reset"));
            case BLUE -> s.append(colors.get("blue")).append(" © ").append(colors.get("reset"));
            case GREEN -> s.append(colors.get("green")).append(" © ").append(colors.get("reset"));
            case PINK -> s.append(colors.get("magenta")).append(" © ").append(colors.get("reset"));
            case YELLOW -> s.append(colors.get("yellow")).append(" © ").append(colors.get("reset"));
        }
    }

    private void appendTower(Tower tower, StringBuilder s){
        switch (tower){
            case WHITE -> s.append(colors.get("reset")).append(" W ").append(colors.get("reset"));
            case BLACK -> s.append(colors.get("reset")).append(" B ").append(colors.get("reset"));
            case GREY -> s.append(colors.get("reset")).append(" G ").append(colors.get("reset"));
        }
    }

    private void appendBorder(StudentColor studentColor, StringBuilder s){
        switch (studentColor) {
            case RED -> s.append(colors.get("red")).append("░░").append(colors.get("reset"));
            case BLUE -> s.append(colors.get("blue")).append("░░").append(colors.get("reset"));
            case GREEN -> s.append(colors.get("green")).append("░░").append(colors.get("reset"));
            case PINK -> s.append(colors.get("magenta")).append("░░").append(colors.get("reset"));
            case YELLOW -> s.append(colors.get("yellow")).append("░░").append(colors.get("reset"));
        }
    }

    private void appendHall(int row, StringBuilder s, EnumMap<StudentColor, Integer> hallView){
        StudentColor studentColor = null;
        switch (row){
            case 0 -> studentColor = StudentColor.GREEN;
            case 1 -> studentColor = StudentColor.RED;
            case 2 -> studentColor = StudentColor.YELLOW;
            case 3 -> studentColor = StudentColor.PINK;
            case 4 -> studentColor = StudentColor.BLUE;
        }

        appendBorder(studentColor, s);
        for(int j = 0; j < 10; j ++){
            if (j < hallView.get(studentColor)){
                appendStudent(studentColor, s);
            } else {
                s.append("   ");
            }
            appendBorder(studentColor, s);
        }
    }

    private void appendBlock(boolean isBlocked, StringBuilder s){
        if (isBlocked)
            s.append(colors.get("red")).append(" X ").append(colors.get("reset"));
        else
            s.append("   ");
    }

    private void appendStudentNumber(StudentColor studentColor, int num, StringBuilder s){
        if (num == 0)
            s.append("  ");
        else
            switch (studentColor) {
                case RED -> s.append(colors.get("red")).append(num).append(" ").append(colors.get("reset"));
                case BLUE -> s.append(colors.get("blue")).append(num).append(" ").append(colors.get("reset"));
                case GREEN -> s.append(colors.get("green")).append(num).append(" ").append(colors.get("reset"));
                case PINK -> s.append(colors.get("magenta")).append(num).append(" ").append(colors.get("reset"));
                case YELLOW -> s.append(colors.get("yellow")).append(num).append(" ").append(colors.get("reset"));
            }
    }

    private void appendWater(StringBuilder s){
        s.append(colors.get("blue"));
        s.append("░".repeat(137));
        s.append(colors.get("reset"));
    }
}

