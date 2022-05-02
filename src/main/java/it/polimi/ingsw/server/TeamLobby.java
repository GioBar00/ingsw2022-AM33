package it.polimi.ingsw.server;

import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class TeamLobby extends Lobby{


    public TeamLobby(int maxPlayers) {
        super(maxPlayers);
    }

    @Override
    public boolean canStart() {
        if(maxPlayers != super.players.size())
            return false;
        else {
            int black = 0;
            int white = 0;
            for(PlayerDetails p : players) {
                if (p.getTower().equals(Tower.WHITE))
                    white++;
                else if (p.getTower().equals(Tower.BLACK))
                    black++;
            }
            return white == 2 && black == 2;
        }
    }

    @Override
    public boolean changeTeam(String nickname, Tower tower) {
        PlayerDetails update = null;
        for(PlayerDetails p : players){
            if(p.getNickname().equals(nickname))
                update = p;
        }
        if(update != null) {
            update.setTower(tower);

            notifyListeners(new MessageEvent(this, new CurrentTeams(getTeamView())));
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
}
