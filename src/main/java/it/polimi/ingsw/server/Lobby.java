package it.polimi.ingsw.server;

import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.network.listeners.ConcreteMessageListenerSubscriber;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.util.*;

public class Lobby extends ConcreteMessageListenerSubscriber {

    protected List<PlayerDetails> players;

    protected final int maxPlayers;

    protected final MessageListener host;
    public Lobby(int maxPlayers, MessageListener host) {
        players = new ArrayList<>();
        this.maxPlayers = maxPlayers;
        this.host = host;
    }

    public boolean addPlayer(String nickname) {
        if(players.size() >= maxPlayers)
            return false;
        for(PlayerDetails p :players){
            if(p.getNickname().equals(nickname))
                return false;
        }
        players.add(new PlayerDetails(nickname));
        return true;
    }

    public boolean setWizard(Wizard wizard, String nickname) {
        PlayerDetails update = null;
        for(PlayerDetails p : players){
            if(p.getNickname().equals(nickname)) {
                update = p;
            }
            else if (p.getWizard() != null)
                if(p.getWizard().equals(wizard))
                    return false;
        }
        if(update != null) {
            if (update.getWizard() == null) {
                update.setWizard(wizard);
                sendStart();
                return true;
            }
        }
        return false;
    }

    public String getMaster() {
        return players.get(0).getNickname();
    }

    public boolean canStart() {
        if (maxPlayers == players.size()){
            for(PlayerDetails p : players){
                if(p.getWizard() == null)
                    return false;
            }
            return true;
        }
        return false;
    }

    public ArrayList<PlayerDetails> getPlayers() {
        return new ArrayList<>(players);
    }

    public boolean removePlayer (String nickname){
        if(nickname.equals(players.get(0).getNickname())){
            return false;
        }

        for(PlayerDetails p : players){
            if(p.getNickname().equals(nickname)){
                players.remove(p);
                return true;
            }
        }
        return false;
    }
    public boolean changeTeam(String nickname, Tower tower){
        return false;
    }

    public TeamsView getTeamView() {
        return null;
    }

    /**
     * @return current available wizards
     */
    public WizardsView getWizardsView(){
        LinkedList<Wizard> wizards = new LinkedList<>(Arrays.asList(Wizard.values()));
        int counter = 0;

        for (PlayerDetails pd : players) {
            if (pd.getWizard() != null){
                counter ++;
                wizards.remove(pd.getWizard());
            }
        }

        // enum set doesn't allow empty set
        if (counter == 4)
            return new WizardsView(EnumSet.noneOf(Wizard.class));
        return new WizardsView(EnumSet.copyOf(wizards));
    }

    public void sendInitialStats(MessageListener messageListener){
        notifyListener(messageListener.getIdentifier(),new MessageEvent(this, new AvailableWizards(getWizardsView())));
    }

    private void sendStart(){
        if(canStart()){
            host.onMessage(new MessageEvent(this, new CommMessage(CommMsgType.CAN_START)));
        }
    }
}
