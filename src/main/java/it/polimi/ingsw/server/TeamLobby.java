package it.polimi.ingsw.server;

import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.server.AvailableWizards;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.enums.Wizard;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

//todo test and javadoc
public class TeamLobby extends Lobby{


    public TeamLobby(int maxPlayers, MessageListener host) {
        super(maxPlayers, host);
    }

    @Override
    public boolean addPlayer(String nickname){
        if(super.addPlayer(nickname)){
            notifyListeners(new MessageEvent(this, new CurrentTeams(getTeamView())));
            return true;
        }
        return false;
    }

    @Override
    public boolean canStart() {
        if(!super.canStart())
            return false;
        else {
            int black = 0;
            int white = 0;
            for(PlayerDetails p : players) {
                if(p.getTower()!= null) {
                    if (p.getTower().equals(Tower.WHITE))
                        white++;
                    else if (p.getTower().equals(Tower.BLACK))
                        black++;
                }
            }
            return white == 2 && black == 2;
        }
    }

    @Override
    public boolean changeTeam(String nickname, Tower tower) {
        PlayerDetails update = null;
        if(tower.equals(Tower.GREY))
            return false;
        for(PlayerDetails p : players){
            if(p.getNickname().equals(nickname))
                update = p;
        }
        if(update != null) {
            update.setTower(tower);
            notifyListeners(new MessageEvent(this, new CurrentTeams(getTeamView())));
            sendStar();
            return true;
        }
        else return false;
    }

    @Override
    public TeamsView getTeamView() {
        EnumMap<Tower, List<String>> teams = new EnumMap<>(Tower.class);
        List <String> lobby = new ArrayList<>();
        for(Tower t : Tower.values()){
            if(!t.equals(Tower.GREY))
                teams.put(t, new ArrayList<>());
        }
        for(PlayerDetails p  : players) {
            if(p.getTower()!= null){
                teams.get(p.getTower()).add(p.getNickname());
            }
            else lobby.add(p.getNickname());
        }
        return new TeamsView(teams, lobby);
    }

    public void sendInitialStats(MessageListener messageListener){
        super.sendInitialStats(messageListener);
        notifyListener(messageListener.getIdentifier(),new MessageEvent(this, new CurrentTeams(getTeamView())));
    }

    private void sendStar(){
        if(canStart()){
            host.onMessage(new MessageEvent(this, new CommMessage(CommMsgType.CAN_START)));
        }
    }

    public boolean setWizard(Wizard wizard, String nickname) {
        if(super.setWizard(wizard, nickname)) {
            notifyListeners(new MessageEvent(this,new CurrentTeams(getTeamView())));
            return true;
        }
        return false;
    }
}
