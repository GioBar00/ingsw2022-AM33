package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.Tower;

import java.util.EnumMap;
import java.util.List;

public class TeamsView {
    private final EnumMap<Tower, List<String>> teams;
    private final List<String> lobby;

    public TeamsView(EnumMap<Tower, List<String>> teams, List<String> lobby){
        this.teams = teams;
        this.lobby = lobby;
    }

    public EnumMap<Tower, List<String>> getTeams() {
        return teams;
    }

    public List<String> getLobby() {
        return lobby;
    }
}
