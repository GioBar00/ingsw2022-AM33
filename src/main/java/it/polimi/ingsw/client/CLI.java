package it.polimi.ingsw.client;

import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.network.messages.client.ChosenTeam;
import it.polimi.ingsw.network.messages.client.ChosenWizard;
import it.polimi.ingsw.network.messages.client.Login;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

public class CLI implements UI{
    static BufferedReader stdIn;
    private ViewListener listener;
    private GameView gameView;
    private WizardsView wizardsView;

    private TeamsView teamsView;
    private final String os;
    private Client client;
    private String nickname;
    private boolean isHost;

    private ViewState lastState;

    CLI(){
        os = System.getProperty("os.name");
        isHost = false;
        lastState = ViewState.SETUP;
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

    @Override
    public void setHost() {
        isHost = true;
        stdIn = new BufferedReader(new InputStreamReader(System.in));
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
    public void showError(CommMessage message) {
        System.out.println("\n"+ message.getType().getMessage());
        if(lastState.equals(ViewState.CHOSE_WIZARD) && message.getType().equals(CommMsgType.ERROR_IMPOSSIBLE_MOVE)){
            lastState = ViewState.SETUP;
        }
    }

    @Override
    public void showStartScreen() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
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
                stdIn = new BufferedReader(new InputStreamReader(System.in));

                if (wizardsView != null) {
                    StringBuilder text = new StringBuilder("Choose a Wizard [");
                    for (Wizard w : wizardsView.getAvailableWizards()) {
                        text.append(w.toString());
                        text.append("|");
                    }
                    text = new StringBuilder(text.subSequence(0, text.lastIndexOf("|")) + "] ->");
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
            stdIn = new BufferedReader(new InputStreamReader(System.in));
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

    }

    @Override
    public void updateGameView() {

    }

    @Override
    public void updateLobbyView() {

    }

    @Override
    public void showCommMessage() {

    }

    private void clearTerminal() throws IOException {

        if (os.contains("Windows"))
        {
            Runtime.getRuntime().exec("cls");
        }
        else
        {
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
}
