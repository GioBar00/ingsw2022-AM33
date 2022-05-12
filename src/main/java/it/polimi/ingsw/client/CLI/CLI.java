package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.client.Login;
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
import static org.fusesource.jansi.Ansi.ansi;


public class CLI implements UI {
    private Client client;
    private String nickname;
    private final String os;
    private Scanner input;
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
    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    @Override
    public void setPossibleMoves(Message message){
        lastRequest = message;
        inputParser.setLastRequest(message);
        showPossibleMoves();
    }

    @Override
    public void setHost() {
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

        String server = input.nextLine();
        System.out.print("Insert port -> ");
        int port = Integer.parseInt(input.nextLine());
        System.out.print("Choose Nickname -> ");
        nickname = input.nextLine();
        client = new Client(server, port, this);
        notifyListener(new Login(nickname));
        executorService = Executors.newFixedThreadPool(2);
        executorService.submit(this::readInput);
    }

    private void readInput(){
        while(!partyEnded()){
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

                lastState = ViewState.CHOSE_WIZARD;
            }
        }
    }
    @Override
    public void showLobbyScreen() {
        clearTerminal();
        for(int i = 0; i < 4;i++){
            System.out.println();
        }
        String[] view = buildTeamLobby();

        for (String s : view) {
            System.out.println(s);
        }

        inputParser.canWrite();
        System.out.println("Select/change team by Typing TEAM <BLACK/WHITE>");
        if(inputParser.isCanStart() && inputParser.isHost()){
            System.out.println("The match can start now. Type START if you want to start it");
        }
        showWizardMenu();
    }

    @Override
    synchronized public void hostCanStart() {
        if(inputParser.isHost()){
            inputParser.canWrite();
            inputParser.setCanStart(true);
            if(teamsView == null)
                System.out.println("\nThe match can start now. Type START if you want to start it");
            else showLobbyScreen();
        }
    }

    synchronized public void hostCantStart(){
        if(teamsView != null){
           inputParser.setCanStart(false);
           showLobbyScreen();
        }
    }

    @Override
    public void showGameScreen() {
        lastRequest = null;
        teamsView = null;
        inputParser.setCanChoseWizard(false);
        inputParser.cantWrite();
        inputParser.setCanStart(false);
        inputParser.setCanChoseWizard(false);
        System.out.println("CIAO SEI NELLA SCHERMATA INIZIALE");
        for(int i = 0; i< 5 ; i++)
            System.out.println();
        //show island
        ArrayList<String> islands = getIslandsLines(gameView.getIslandsView(), gameView.getMotherNatureIndex());
        for (String line : islands) {
            System.out.println(line);
        }
        //show details
        //show school boards
        for (PlayerView pv : gameView.getPlayersView()){
            ArrayList<String> board = getSchoolBoardLines(pv.getSchoolBoardView());
            for (String line : board) {
                System.out.println(line);
            }
        }
        showPossibleMoves();
    }

    public void showPossibleMoves() {
        if(nickname.equals(gameView.getCurrentPlayer()))
            inputParser.canWrite();
        Map<String, Integer> characterCard = playableCharacterCards();
        if (characterCard != null) {
            System.out.println("Choose next action : ");
            System.out.println("Activate a CharacterCard by typing ACTIVE <name>");
            StringBuilder text = new StringBuilder("[ ");
            for(String s : characterCard.keySet())
                text.append(s.toUpperCase()).append(" |");
            text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "] ");
            System.out.println("Available Character Card " + text );
        }
        if(lastRequest == null)
            return;
        switch(MessageType.retrieveByMessage(lastRequest)){
            case CHOOSE_CLOUD -> {
                Set<Integer> choices = ((ChooseCloud)lastRequest).getAvailableCloudIndexes();
                System.out.println("You have to chose a Cloud");
                System.out.println("Available Cloud " + buildSequence(choices));
                System.out.println("Type CLOUD <INDEX>");
            }
            case CHOOSE_ISLAND ->{
                Set<Integer> choices = ((ChooseIsland)lastRequest).getAvailableIslandIndexes();
                System.out.println("You have to chose an Island");
                System.out.println("Available Island " + buildSequence(choices));
                System.out.println("Type ISLAND <INDEX>");
            }
            case CHOOSE_STUDENT_COLOR -> {
                EnumSet<StudentColor> choices = ((ChooseStudentColor)lastRequest).getAvailableStudentColors();
                System.out.println("You have to chose a StudentColor");
                System.out.println("Available StudentColor " + buildSequence(choices));
                System.out.println("Type COLOR <COLOR>");
            }
            case MOVE_MOTHER_NATURE -> {
                System.out.println("You have to chose a StudentColor");
                System.out.println("Insert the number of steps mother nature has to take [up to " +
                ((MoveMotherNature)lastRequest).getMaxNumMoves() + " ]");
                System.out.println("Type MOTHERNATURE <STEPS>");
            }
            case MOVE_STUDENT -> {
                if(characterCard != null){
                    System.out.println("Make a move by typing MOVE <from> <index/color> <to> <index/color>");
                }
                System.out.println("You have to make a MOVE");
                printMove((MoveStudent)lastRequest);
                System.out.println("Type MOVE <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR> ");
            }
            case MULTIPLE_POSSIBLE_MOVES -> {
                if(characterCard != null){
                    System.out.println("Make a move by typing MOVE <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR>");
                }
                for(MoveActionRequest m : ((MultiplePossibleMoves)lastRequest).getPossibleMoves())
                    printMove(m);
                System.out.println("Type MOVE <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR> ");
            }
            case SWAP_STUDENTS -> {
                if(characterCard != null){
                    System.out.println("Make a swap by typing SWAP <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR>");
                }
                printSwap((SwapStudents)lastRequest);
                System.out.println("Type SWAP <FROM> <INDEX/COLOR> <TO> <INDEX/COLOR>");
            }
            case PLAY_ASSISTANT_CARD -> {
                System.out.println("Choose an AssistantCard");
                System.out.println("Cards Available :");
                for(AssistantCard a : ((PlayAssistantCard)lastRequest).getPlayableAssistantCards()){
                    System.out.println("[ Card number : "+ a.getValue() + " max Mother Nature moves : " + a.getMoves() +" ]");
                }
                System.out.println("Type ASSISTANT <NUMBER>");
            }
            default -> {System.out.println("sei qui");}
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
        System.out.println("\n"+ message.getType().getMessage());
        if(lastState.equals(ViewState.CHOSE_WIZARD) && message.getType().equals(CommMsgType.ERROR_IMPOSSIBLE_MOVE)){
            lastState = ViewState.SETUP;
            this.showWizardMenu();
        }
        if(lastState.equals(ViewState.PLAY_CARD) && message.getType().equals(CommMsgType.ERROR_IMPOSSIBLE_MOVE)){
            this.showPossibleMoves();
        }
        if(lastState.equals(ViewState.PLAYING)){
            this.showPossibleMoves();
        }
    }

    private void clearTerminal() {
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
        System.out.println("Available Moves ");
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
    }

    private String[] buildTeamLobby(){
        String[] view = new String[7];
        view[0] ="";
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
        List <String> white = teamsView.getTeams().get(Tower.WHITE);
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

    private ArrayList<String> getSchoolBoardLines(SchoolBoardView sbv){
        ArrayList<String> schoolBoardLines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        int indexEntrance = 0;
        int countTower = 0;
        // CONTORNO SOPRA
        stringBuilder.append(ansi().render("@|yellow ╔|@"));
        for(int i = 0; i < 92; i ++){
            if ((i == 14) || (i == 77) || (i == 67)) {
                stringBuilder.append(ansi().render("@|yellow ╦|@"));
            } else {
                stringBuilder.append(ansi().render("@|yellow ═|@"));
            }
        }
        stringBuilder.append(ansi().render("@|yellow ╗|@"));
        schoolBoardLines.add(0, stringBuilder.toString());

        // INTERNO RIGA PER RIGA
        for(int rows = 0; rows < 5; rows ++) {
            stringBuilder.delete(0, stringBuilder.length());

            // ENTRANCE
            stringBuilder.append(ansi().render("@|yellow ║|@"));
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(ansi().render("@|white ░|@"));
            }
            if (indexEntrance < sbv.getEntrance().size()) {
                appendStudent(sbv.getEntrance().get(indexEntrance), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            indexEntrance++;
            for (int i = 0; i < 4; i++) {
                stringBuilder.append(ansi().render("@|white ░|@"));
            }
            if (indexEntrance < sbv.getEntrance().size()) {
                appendStudent(sbv.getEntrance().get(indexEntrance), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            indexEntrance++;
            for (int i = 0; i < 2; i++) {
                stringBuilder.append(ansi().render("@|white ░|@"));
            }

            // HALL
            stringBuilder.append(ansi().render("@|yellow ║|@"));
            appendHall(rows, stringBuilder, sbv.getStudentsHall());


            // PROFESSORS
            stringBuilder.append(ansi().render("@|yellow ║|@"));
            for (int i = 0; i < 3; i++) {
                stringBuilder.append(ansi().render("@|white ░|@"));
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
                    case RED -> stringBuilder.append(ansi().render("@|red  ■ |@"));
                    case BLUE -> stringBuilder.append(ansi().render("@|blue  ■ |@"));
                    case GREEN -> stringBuilder.append(ansi().render("@|green  ■ |@"));
                    case PINK -> stringBuilder.append(ansi().render("@|magenta  ■ |@"));
                    case YELLOW -> stringBuilder.append(ansi().render("@|yellow  ■ |@"));
                }
            } else {
                stringBuilder.append("   ");
            }
            for(int i = 0; i < 3; i ++){
                stringBuilder.append(ansi().render("@|white ░|@"));
            }

            // TOWERS
            stringBuilder.append(ansi().render("@|yellow ║|@"));
            for(int i = 0; i < 2; i ++){
                stringBuilder.append(ansi().render("@|white ░|@"));
            }
            if (countTower < sbv.getNumTowers()) {
                appendTower(sbv.getTower(), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            countTower ++;
            for(int i = 0; i < 4; i ++){
                stringBuilder.append(ansi().render("@|white ░|@"));
            }
            if (countTower < sbv.getNumTowers()) {
                appendTower(sbv.getTower(), stringBuilder);
            } else {
                stringBuilder.append("   ");
            }
            countTower ++;
            for(int i = 0; i < 2; i ++){
                stringBuilder.append(ansi().render("@|white ░|@"));
            }
            stringBuilder.append(ansi().render("@|yellow ║|@"));
            schoolBoardLines.add(rows+1, stringBuilder.toString());
        }

        // CONTORNO SOTTO
        stringBuilder.delete(0, stringBuilder.length());
        stringBuilder.append(ansi().render("@|yellow ╚|@"));
        for(int i = 0; i < 92; i ++){
            if ((i == 14) || (i == 77) || (i == 67)) {
                stringBuilder.append(ansi().render("@|yellow ╩|@"));
            } else {
                stringBuilder.append(ansi().render("@|yellow ═|@"));
            }
        }
        stringBuilder.append(ansi().render("@|yellow ╝|@"));
        schoolBoardLines.add(6, stringBuilder.toString());

        // NOMI
        schoolBoardLines.add(7, "\tEntrance\t\t\t\t\t\tHall\t\t\t\t\t\t\tProfessors\t\s\s\sTowers");

        for (String s : schoolBoardLines) {
            System.out.println(s);
        }

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
        stringBuilder.append(ansi().render("@|blue ░ |@"));
        for (IslandGroupView igv : views) {
            stringBuilder.append(ansi().render("@|green ╔|@"));
            for(int i = 0; i < igv.getIslands().size(); i++){
                stringBuilder.append(ansi().render("@|green ═══════|@"));
                if (i == igv.getIslands().size() - 1)
                    stringBuilder.append(ansi().render("@|green ╗|@"));
                else
                    stringBuilder.append(ansi().render("@|green ╦|@"));
            }
            stringBuilder.append(ansi().render("@|blue ░ |@"));
        }


        // first line (tower + blocks)
        islandsLines.add(1, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(ansi().render("@|blue ░ |@"));
        int islandIndex = 0;
        for (IslandGroupView igv : views) {
            stringBuilder.append(ansi().render("@|green ║|@"));
            for(int i = 0; i < igv.getIslands().size(); i++) {
                Tower tower = igv.getIslands().get(i).getTower();
                if (tower == null)
                    stringBuilder.append("   ");
                else
                    appendTower(tower, stringBuilder);
                if (i == 0 && islandIndex == motherNatureIndex)
                    stringBuilder.append(ansi().render("@|green ©|@"));
                else
                    stringBuilder.append(" ");
                appendBlock(igv.isBlocked(), stringBuilder);
                stringBuilder.append(ansi().render("@|green ║|@"));
            }
            stringBuilder.append(ansi().render("@|blue ░ |@"));
            islandIndex ++;
        }


        // second line (blue, red and green students)
        islandsLines.add(2, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(ansi().render("@|blue ░ |@"));
        for (IslandGroupView igv : views) {
            stringBuilder.append(ansi().render("@|green ║|@"));
            for(int i = 0; i < igv.getIslands().size(); i++) {
                appendStudentNumber(BLUE, igv.getIslands().get(i).getStudents().get(StudentColor.BLUE), stringBuilder);
                appendStudentNumber(RED, igv.getIslands().get(i).getStudents().get(StudentColor.RED), stringBuilder);
                appendStudentNumber(GREEN, igv.getIslands().get(i).getStudents().get(GREEN), stringBuilder);
                stringBuilder.append(ansi().render("@|green ║|@"));
            }
            System.out.print(ansi().eraseScreen().render("@|blue  ░ |@"));
            stringBuilder.append(ansi().render("@|blue ░ |@"));
        }

        // third line (pink and yellow students)
        islandsLines.add(3, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(ansi().render("@|blue ░ |@"));
        for (IslandGroupView igv : views){
            stringBuilder.append(ansi().render("@|green ║|@"));
            for(int i = 0; i < igv.getIslands().size(); i++) {
                appendStudentNumber(PINK, igv.getIslands().get(i).getStudents().get(PINK), stringBuilder);
                appendStudentNumber(YELLOW, igv.getIslands().get(i).getStudents().get(YELLOW), stringBuilder);
                stringBuilder.append(ansi().render("@|green   ║|@"));
            }
            stringBuilder.append(ansi().render("@|blue ░ |@"));
        }


        // bottom border
        islandsLines.add(4, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());

        stringBuilder.append(ansi().render("@|blue ░ |@"));
        for (IslandGroupView igv : views) {
            stringBuilder.append(ansi().render("@|green ╚|@"));
            for(int i = 0; i < igv.getIslands().size(); i++){
                stringBuilder.append(ansi().render("@|green ═══════|@"));
                if (i == igv.getIslands().size() - 1)
                    stringBuilder.append(ansi().render("@|green ╝|@"));
                else
                    stringBuilder.append(ansi().render("@|green ╩|@"));
            }
            stringBuilder.append(ansi().render("@|blue ░ |@"));
        }

        // water layer
        islandsLines.add(5, stringBuilder.toString());
        stringBuilder.delete(0, stringBuilder.length());
        appendWater(stringBuilder);

        return islandsLines;
    }


    private void appendStudent(StudentColor studentColor, StringBuilder s){
        switch (studentColor) {
            case RED -> s.append(ansi().render("@|red  © |@"));
            case BLUE -> s.append(ansi().render("@|blue  © |@"));
            case GREEN -> s.append(ansi().render("@|green  © |@"));
            case PINK -> s.append(ansi().render("@|magenta  © |@"));
            case YELLOW -> s.append(ansi().render("@|yellow  © |@"));
        }
    }

    private void appendTower(Tower tower, StringBuilder s){
        switch (tower){
            case WHITE -> s.append(" ■ ");
            case BLACK -> s.append(ansi().render("@|black  ■ |@"));
            case GREY -> s.append(ansi().render("@|white  ■ |@"));
        }
    }

    private void appendBorder(StudentColor studentColor, StringBuilder s){
        switch (studentColor) {
            case RED -> s.append(ansi().render("@|red ░░|@"));
            case BLUE -> s.append(ansi().render("@|blue ░░|@"));
            case GREEN -> s.append(ansi().render("@|green ░░|@"));
            case PINK -> s.append(ansi().render("@|magenta ░░|@"));
            case YELLOW -> s.append(ansi().render("@|yellow ░░|@"));
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
            s.append(ansi().render("@|red  X |@"));
        else
            s.append("   ");
    }

    private void appendStudentNumber(StudentColor studentColor, int num, StringBuilder s){
        if (num == 0)
            s.append("   ");
        else
            switch (studentColor) {
                case RED -> s.append(ansi().render("@|red  %d |@", num));
                case BLUE -> s.append(ansi().render("@|blue  %d |@", num));
                case GREEN -> s.append(ansi().render("@|green  %d |@", num));
                case PINK -> s.append(ansi().render("@|magenta  %d |@", num));
                case YELLOW -> s.append(ansi().render("@|yellow  %d |@", num));
            }
    }

    private void appendWater(StringBuilder s){
        for (int i = 0; i < 137; i ++) {
            s.append(ansi().render("@|blue ░|@"));
        }
    }
}

