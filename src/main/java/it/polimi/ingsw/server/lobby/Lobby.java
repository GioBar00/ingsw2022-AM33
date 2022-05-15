package it.polimi.ingsw.server.lobby;

import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.network.listeners.ConcreteMessageListenerSubscriber;
import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.util.*;

public class Lobby extends ConcreteMessageListenerSubscriber {

    protected List<PlayerDetails> players;

    protected final int maxPlayers;

    public Lobby(int maxPlayers) {
        players = new ArrayList<>();
        this.maxPlayers = maxPlayers;
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

    public void notifyAvailableWizards(String identifier) {
        System.out.println("Sending initial stats to " + identifier);
        notifyMessageListener(identifier, new MessageEvent(this, new AvailableWizards(getWizardsView())));
    }

    public void notifyAvailableWizards() {
        for (PlayerDetails pd : players) {
            if (pd.getWizard() == null)
                notifyAvailableWizards(pd.getNickname());
        }
    }

    public void notifyTeams(String identifier) {
        if (getTeamView() != null)
            notifyMessageListener(identifier, new MessageEvent(this, new CurrentTeams(getTeamView())));
    }

    public void notifyTeams() {
        if (getTeamView() != null)
            notifyMessageListeners(new MessageEvent(this, new CurrentTeams(getTeamView())));
    }

    void sendStart() {
        if(canStart()){
            notifyMessageListener(getMaster(), new MessageEvent(this, new CommMessage(CommMsgType.CAN_START)));
        }
    }

    public boolean containsPlayer(String identifier) {
        for (PlayerDetails pd : players) {
            if (pd.getNickname().equals(identifier))
                return true;
        }
        return false;
    }
}
