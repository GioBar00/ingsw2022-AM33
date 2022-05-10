package it.polimi.ingsw.client;

import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.network.messages.client.ChosenTeam;
import it.polimi.ingsw.network.messages.client.ChosenWizard;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.CharacterCardView;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.server.model.enums.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

public class CLI implements UI{
    static BufferedReader stdIn;
    private ViewListener listener;
    private GameView gameView;
    private WizardsView wizardsView;
    private TeamsView teamsView;
    private Message lastRequest;
    private final String os;
    private Client client;
    private String nickname;
    private boolean isHost;
    private ViewState lastState;

    CLI(){
        os = System.getProperty("os.name");
        isHost = false;
        lastState = ViewState.SETUP;
        stdIn = new BufferedReader(new InputStreamReader(System.in));

    }

    @Override
    public void setWizardView(WizardsView wizardsView) {
        this.wizardsView = wizardsView;
    }

    @Override
    public void setTeamsView(TeamsView teamsView) {
        this.teamsView = teamsView;
    }

    @Override
    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }


    public void setPossibleMoves(Message message){
        synchronized (this) {
            lastRequest = message;
            this.notifyAll();
        }
    }

    @Override
    public void setHost() {
        isHost = true;
        try {
            clearTerminal();
            if (nickname.equals("ingConti")) {
                System.out.println("Welcome our Master  \n lord of the these lands \n supreme commander of every known IDE");
            } else {
                System.out.println("Welcome host");
            }

            GamePreset preset;
            do {
                System.out.println("Insert the number of players [2|3|4] ->");
                preset = GamePreset.getFromNumber(Integer.parseInt(stdIn.readLine()));
            } while (preset == null);
            GameMode mode;
            do {
                System.out.println("Choose the mode [n | e ] ->");
                mode = GameMode.getFromChar(stdIn.readLine());
            } while (mode == null);

            notifyListener(new ChosenGame(preset, mode));
            clearTerminal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void showStartScreen() {
        System.out.println(ansi().eraseScreen().render("@|yellow ▄▄▌ ▐ ▄▌▄▄▄ .▄▄▌   ▄▄·       • ▌ ▄ ·. ▄▄▄ .    ▪   ▐ ▄     ▄▄▄ .▄▄▄  ▪   ▄▄▄·  ▐ ▄ ▄▄▄▄▄ ▄· ▄▌.▄▄ · |@"));
        System.out.println(ansi().eraseScreen().render( "@|yellow ██· █▌▐█▀▄.▀·██•  ▐█ ▌▪▪     ·██ ▐███▪▀▄.▀·    ██ •█▌▐█    ▀▄.▀·▀▄ █·██ ▐█ ▀█ •█▌▐█•██  ▐█▪██▌▐█ ▀. |@"));
        System.out.println(ansi().eraseScreen().render("@|yellow ██▪▐█▐▐▌▐▀▀▪▄██▪  ██ ▄▄ ▄█▀▄ ▐█ ▌▐▌▐█·▐▀▀▪▄    ▐█·▐█▐▐▌    ▐▀▀▪▄▐▀▀▄ ▐█·▄█▀▀█ ▐█▐▐▌ ▐█.▪▐█▌▐█▪▄▀▀▀█▄|@"));
        System.out.println(ansi().eraseScreen().render( "@|yellow ▐█▌██▐█▌▐█▄▄▌▐█▌▐▌▐███▌▐█▌.▐▌██ ██▌▐█▌▐█▄▄▌    ▐█▌██▐█▌    ▐█▄▄▌▐█•█▌▐█▌▐█ ▪▐▌██▐█▌ ▐█▌· ▐█▀·.▐█▄▪▐█|@"));
        System.out.println(ansi().eraseScreen().render(" @|yellow ▀▀▀▀ ▀▪ ▀▀▀ .▀▀▀ ·▀▀▀  ▀█▄▀▪▀▀  █▪▀▀▀ ▀▀▀     ▀▀▀▀▀ █▪     ▀▀▀ .▀  ▀▀▀▀ ▀  ▀ ▀▀ █▪ ▀▀▀   ▀ •  ▀▀▀▀ |@"));

        System.out.print("\n\nInsert Server name -> ");
        try {
            String server = stdIn.readLine();
            System.out.print("Insert port -> ");
            int port = Integer.parseInt(stdIn.readLine());
            System.out.print("Choose Nickname -> ");
            nickname = stdIn.readLine();
            client = new Client(server, port ,this);
            notifyListener(new Login(nickname));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void showWizardMenu(){
        if(lastState.equals(ViewState.SETUP)) {
            try {
                clearTerminal();

                if (wizardsView != null) {
                    String text = "Choose a Wizard";
                    text = text + buildSequence(wizardsView.getAvailableWizards());
                    Wizard choice;
                    do {
                        System.out.print(text);
                        choice = Wizard.getWizardFromString(stdIn.readLine());
                    } while (choice == null);
                    notifyListener(new ChosenWizard(choice));
                    lastState = ViewState.CHOSE_WIZARD;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void showLobbyScreen() {
        try {
            clearTerminal();
            clearTerminal();
            if(teamsView != null){
                String[] view = buildTeamLobby();
                for(String s : view){
                    if(s!= null)
                        System.out.println(ansi().eraseScreen().render(s));
                }
                System.out.println("\n");
            }

            if(teamsView != null){
                Tower tower;
                do {
                    System.out.print("Choose/Change team [BLACK | WHITE] -> ");
                    String input = stdIn.readLine().toLowerCase();
                    if (input.equals("black"))
                        tower = Tower.BLACK;
                    else if (input.equals("white")) {
                        tower = Tower.WHITE;
                    } else tower = null;
                }while(tower == null);
                notifyListener(new ChosenTeam(tower));
            }

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void showGameScreen() {
        lastRequest = null;
        //show island
        //show details
        //show school boards
        if(false) //sono il giocatore corrente
           showPossibleActions();
    }

    public void showPossibleActions(){
        synchronized (this) {
            if(lastRequest == null){
                System.out.println("Waiting for Server");
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if(gameView.getPhase().equals(GamePhase.PLANNING) || gameView.getPhase().equals(GamePhase.MOVE_MOTHER_NATURE) || gameView.getPhase().equals(GamePhase.CHOOSE_CLOUD))
                showPossibleMoves();
            else {
                Map<String, Integer> characterCard = playableCharacterCards();
                if (characterCard != null) {
                    System.out.println("Choose next action");
                    System.out.println("1 - Move Student");
                    System.out.println("2 - Activate a CharacterCard");
                    boolean isValid = false;
                    do {
                        String input = readLine();
                        if (input.matches("-?\\d+")) {
                            int val = Integer.parseInt(input);
                            if (val == 1 || val == 2) {
                                if (val == 1) {
                                    showPossibleMoves();
                                    isValid = true;
                                }
                                if (val == 2) {
                                    //todo chooseCharacterCard
                                    isValid = true;
                                }
                            }
                        }
                    } while (!isValid);
                }
            }
        }
    }

    public void showPossibleMoves() {
        switch(MessageType.retrieveByMessage(lastRequest)){
            case CHOOSE_CLOUD -> {
                Set<Integer> choices = ((ChooseCloud)lastRequest).getAvailableCloudIndexes();
                String text = buildSequence(choices);
                text = "\nChoose a Cloud "+ text + " -> ";
                notifyListener(new ChosenCloud(getIntFromSet(choices,text)));
            }
            case CHOOSE_ISLAND ->{
                Set<Integer> choices = ((ChooseIsland)lastRequest).getAvailableIslandIndexes();
                String text = buildSequence(choices);
                text = "\nChoose an Island "+ text + " -> ";
                notifyListener(new ChosenIsland(getIntFromSet(choices, text)));
            }
            case CHOOSE_STUDENT_COLOR -> {
                EnumSet<StudentColor> choices = ((ChooseStudentColor)lastRequest).getAvailableStudentColors();
                String text = "Choose a Color ";
                text = text + buildSequence(choices) + " -> ";
                notifyListener(new ChosenStudentColor(getColorFromSet(choices, text)));
            }
            case MOVE_MOTHER_NATURE -> {
                String text = "Insert the number of steps mother nature has to take [up to ";
                int maxMove = ((MoveMotherNature)lastRequest).getMaxNumMoves();
                text = text + maxMove + " ] -> ";
                boolean isValid = false;
                do {
                    System.out.print(text);
                    try {
                        int input = Integer.parseInt(stdIn.readLine());
                        if (input >0 && input <= maxMove) {
                            notifyListener(new MovedMotherNature(input));
                            isValid = true;
                        } else {
                            System.out.println(ansi().eraseScreen().render("@|red Insert a valid value |@"));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }while(!isValid);
            }
            case MOVE_STUDENT -> {
                printMove((MoveStudent)lastRequest);
                notifyListener(getMoveChoice((MoveStudent)lastRequest));

            }
            case MULTIPLE_POSSIBLE_MOVES -> {
                for(MoveActionRequest m : ((MultiplePossibleMoves)lastRequest).getPossibleMoves())
                    printMove(m);
                notifyListener(getMoveChoice(((MultiplePossibleMoves)lastRequest).getPossibleMoves()));
            }
            case SWAP_STUDENTS -> {
                printMove((SwapStudents)lastRequest);
                notifyListener(new SwappedStudents(getMoveChoice((SwapStudents)lastRequest)));
            }
            case PLAY_ASSISTANT_CARD -> showAssistantCard(((PlayAssistantCard)lastRequest).getPlayableAssistantCards());
            default -> {}
        }
    }

    public void showAssistantCard(EnumSet <AssistantCard> playableAssistantCards){
        if(playableAssistantCards != null){
            System.out.println("Cards Available :");
            for(AssistantCard a : playableAssistantCards){
                System.out.println("[ Card number : "+ a.getValue() + " max Mother Nature moves : " + a.getMoves() +" ]");
            }
            boolean isValid = false;
            do {
                System.out.println("Insert the number of the card -> ");
                try {
                    int input = Integer.parseInt(stdIn.readLine());
                    lastState = ViewState.PLAY_CARD;
                    AssistantCard as = AssistantCard.getFromInt(input);
                    if (as != null)
                        if (!playableAssistantCards.contains(as)) {
                            System.out.println(ansi().eraseScreen().render("@|red Insert a valid value |@"));
                        } else {
                            notifyListener(new PlayedAssistantCard(as));
                            isValid = true;
                        }
                    else System.out.println("Insert a valid value");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }while(!isValid);
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
        }
        if(lastState.equals(ViewState.PLAY_CARD) && message.getType().equals(CommMsgType.ERROR_IMPOSSIBLE_MOVE)){
            this.showPossibleMoves();
        }
        if(lastState.equals(ViewState.PLAYING)){
            this.showPossibleActions();
        }
    }

    private void clearTerminal() throws IOException {
        if (os.contains("Windows")) {
            Runtime.getRuntime().exec("cls");
        }
        else {
            Runtime.getRuntime().exec("clear");
        }
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


    private String[] buildTeamLobby(){
        String[] view = new String[7];
        view[0] ="";
        for(int i = 0; i < 15; i++){
            view[0] = view[0] +" ";
        }
        view[0] = view[0] + "@|black BLACK TEAM |@";
        for(int i = 0; i < 43; i++){
            view[0] = view[0] +" ";
        }
        view[0] = view[0] + "@|yellow LOBBY |@";
        for(int i = 0; i < 43; i++){
            view[0] = view[0] +" ";
        }
        view[0] = view [0] + "@|white WHITE TEAM |@";
        StringBuilder sup = new StringBuilder("┌──────────────────────────────────────┐");
        String sup2 = "          ";
        view[1] = "@|black " + sup +" |@"+ sup2 + "@|yellow "+ sup +" |@" + sup2;
        view[1] = view[1] + "@|white " + sup +" |@";

        List<String> black = teamsView.getTeams().get(Tower.BLACK);
        List <String> white = teamsView.getTeams().get(Tower.WHITE);
        List<String> lobby = teamsView.getLobby();

        sup = new StringBuilder();
        sup.append(" ".repeat(37));

        for(int i = 0; i < 4 ; i ++){
            view[2 + i] = "@|black │ |@";
            if(black.size() > 0){
                String name = black.remove(0);
                int size = name.length();
                view[2 + i] = view[2 + i] +"@|black "+ name +" |@";
                for(int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            }else{ view[2 + i] = view[2 + i] + sup; }
            view[2 + i] = view[2 + i] + "@|black │ |@";
            view[2 + i] = view[2 + i] + sup2;

            view[2 + i] = view[2 + i] + "@|yellow │ |@";
            if(lobby.size()>0){
                String name = lobby.remove(0);
                int size = name.length();
                view[2 + i] = view[2 + i] + "@|yellow "+ name +" |@";
                for(int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            }else{ view[2 + i] = view[2 + i] + sup; }
            view[2 + i]= view[2 + i] + "@|yellow │ |@";

            view[2 + i] = view[2 + i] + sup2;

            view[2 + i] = view[2 + i] + "@|white │ |@";
            if(white.size()>0){
                String name = white.remove(0);
                int size = name.length();
                view[2 + i] = view[2 + i] + "@|white "+ name +" |@";
                for(int j = 0; j < 36 - size; j++)
                    view[2 + i] = view[2 + i] + " ";
            }else{ view[2 + i] = view[2 + i] + sup; }
            view[2 + i]= view[2 + i] + "@|white │ |@";
        }

        sup = new StringBuilder("└──────────────────────────────────────┘");
        sup2 = "          ";
        view[6] = "@|black " + sup +" |@"+ sup2 + "@|yellow "+ sup +" |@" + sup2;
        view[6] = view[6] + "@|white " + sup +" |@";
        return view;
    }

    private EnumSet<StudentColor> fromIntegersToEnums (Set<Integer> choices){
        List<StudentColor> ret= new ArrayList<>();
        for(int i : choices){
            ret.add(StudentColor.retrieveStudentColorByOrdinal(i));
        }
        return EnumSet.copyOf(ret);
    }

    private <T extends Enum<T>> String buildSequence(EnumSet<T> choices){
        StringBuilder text = new StringBuilder("[ ");
        for (T w : choices) {
            text.append(w.toString());
            text.append(" | ");
        }
        text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "]");
        return text.toString();
    }

    private String buildSequence(Set<Integer> choices){
        StringBuilder text = new StringBuilder("[ ");
        for(Integer i : choices){
            text.append(i).append(" | ");
        }
        text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "]");
        return text.toString();
    }

    private Map<String,Integer> playableCharacterCards(){
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
        choices = move.getToIndexesSet();
        if(choices!= null)
            text.append(" ").append(buildSequence(choices));
        System.out.println(text);
    }

    private MovedStudent getMoveChoice(MoveActionRequest move){
        List<MoveActionRequest> moves = new ArrayList<>(1);
        moves.add(move);
        return getMoveChoice(moves);
    }
    private MovedStudent getMoveChoice(List<MoveActionRequest> moves){
        MoveLocation from;
        Integer fromIndex;
        MoveLocation to;
        Integer toIndex;
        boolean isValid = false;
        List <MoveLocation> froms = new ArrayList<>();
        List <MoveLocation> tos = new ArrayList<>();

        do {
            for(MoveActionRequest m : moves){
                froms.add(m.getFrom());
            }
            MoveActionRequest chosen = null;
            String text;
            do {
                if(froms.size() > 1) {
                    text = "Select from ";
                    text = text + buildSequence(EnumSet.copyOf(froms)) + " -> ";
                    from = getMoveLocation(froms, text);
                }
                else{ from = froms.get(0);}
                for (MoveActionRequest m : moves) {
                    if (m.getFrom().equals(from))
                        tos.add(m.getTo());
                }
                if(tos.size() > 1) {
                    text = "Select to ";
                    text = text + buildSequence(EnumSet.copyOf(tos)) + " -> ";

                    to = getMoveLocation(tos, text);
                }else { to = tos.get(0);}
                for (MoveActionRequest m : moves) {
                    if (m.getFrom().equals(from) && m.getTo().equals(to)) {
                        chosen = m;
                        break;
                    }
                }
            }while(chosen == null);

            System.out.println("Moving from " + from);
            fromIndex = getChosenIndex(from, chosen.getFromIndexesSet(), from.requiresFromIndex(), false);

            System.out.println("Moving to " + to);
            toIndex = getChosenIndex(to, chosen.getToIndexesSet(), to.requiresToIndex(), to.equals(MoveLocation.ISLAND));

            String sel;
            do{
                System.out.println("Confirm ? [y/n] ");
                sel = readLine().toLowerCase();

            }while(!(sel.equals("y") || sel.equals("n")));
            if(sel.equals("y"))
                isValid = true;
            froms.clear();
            tos.clear();
        }while(!isValid);
        return new MovedStudent(from, fromIndex, to , toIndex);
    }

    private int getIntFromSet(Set<Integer> choices, String text){
        int input = 0;
        boolean isValid = false;
        do {
            System.out.print(text);
            String in = readLine();
            if(in.matches("-?\\d+")){
                input = Integer.parseInt(in);
                if (!choices.contains(input)) {
                    System.out.println(ansi().eraseScreen().render("@|red Insert a valid value |@"));
                } else {
                    isValid = true;
                }
            }
        }while(!isValid);
        return input;
    }

    private StudentColor getColorFromSet(EnumSet<StudentColor> choices, String text){
        boolean isValid = false;
        StudentColor input;
        do {
            System.out.print(text);
            input = StudentColor.getColorFromString(readLine().toLowerCase());
            if(input != null){
                if (!choices.contains(input)) {
                    System.out.println(ansi().eraseScreen().render("@|red Insert a valid value |@"));
                } else {
                    isValid = true;
                }
            }
            else System.out.println(ansi().eraseScreen().render("@|red Insert a valid value |@"));
        }while(!isValid);
        return input;
    }

    private MoveLocation getMoveLocation(List<MoveLocation> choices,String text){
        boolean isValid = false;
        MoveLocation from;
        do {
            System.out.print(text);
            from = MoveLocation.getFromString(readLine());
            if (from != null)
                if (choices.contains(from)) {
                    isValid = true;
                } else {
                    System.out.println(ansi().eraseScreen().render("@|red Insert a valid value |@"));
                }
            else System.out.println(ansi().eraseScreen().render("@|red Insert a valid value |@"));

        } while (!isValid);
        return from;
    }

    private Integer getChosenIndex(MoveLocation location, Set <Integer> chosen, boolean requireIndex, boolean toIsland){
        String text;
        if(requireIndex){
            if(location.equals(MoveLocation.ENTRANCE)) {
                text = "Choose the entrance index " + buildSequence(chosen) + " -> ";
                return  getIntFromSet(chosen, text);
            }
            else if(toIsland ){
                text = "Choose the island index " + buildSequence(chosen) + " -> ";
                return  getIntFromSet(chosen, text);
            } else{
                EnumSet<StudentColor> choices = fromIntegersToEnums(chosen);
                text = "Choose the color of the student " +buildSequence(choices) + " -> ";
                return  getColorFromSet(choices, text).ordinal();
            }
        }
        else return null;
    }

    private static String readLine(){
        String line = null;
        do {
            try {
                line = stdIn.readLine();
            } catch (IOException e) {
                System.out.println("Client Error");
            }
        }while(Objects.equals(line, "\n"));
        return line;
    }
}
