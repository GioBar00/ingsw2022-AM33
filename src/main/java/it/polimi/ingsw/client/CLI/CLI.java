package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.client.*;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.CurrentGameState;
import it.polimi.ingsw.network.messages.views.CharacterCardView;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.server.model.enums.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
        //show details
        //show school boards
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
}

