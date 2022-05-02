package it.polimi.ingsw.server;

import it.polimi.ingsw.server.listeners.ConcreteMessageListenerSubscriber;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.util.ArrayList;
import java.util.List;

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
            if(p.getNickname().equals(nickname))
                update = p;
            else if (p.getWizard().equals(wizard))
                return false;
        }
        if(update != null) {
            update.setWizard(wizard);
            return true;
        }
        return false;
    }

    public String getMaster() {
        return players.get(0).getNickname();
    }

    public boolean canStart() {
        return maxPlayers == players.size();
    }

    public ArrayList<PlayerDetails> getPlayer() {
        return new ArrayList<>(players);
    }

    public boolean removePlayer (String nickname){
        if(nickname.equals(players.get(0).getNickname()))
            return false;
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
}
