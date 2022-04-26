package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class TeamsView implements Serializable {
    private final EnumMap<Tower, List<String>> teams;
    private final List<String> lobby;

    public TeamsView(EnumMap<Tower, List<Player>> teams, List<String> lobby){
        EnumMap<Tower, List<String>> teamsString = new EnumMap<>(Tower.class);
        for (Tower t: teams.keySet()) {
            teamsString.put(t, new LinkedList<>());
            ArrayList<String> playerNicks = new ArrayList<>();
            for (Player p : teams.get(t)) {
                playerNicks.add(p.getNickname());
            }
            teamsString.put(t, playerNicks);
        }
        this.teams = teamsString;
        this.lobby = lobby;
    }

    public EnumMap<Tower, List<String>> getTeams() {
        return teams;
    }

    public List<String> getLobby() {
        return lobby;
    }
}
