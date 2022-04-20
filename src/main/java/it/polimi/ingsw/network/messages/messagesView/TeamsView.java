package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.Tower;

import java.util.EnumMap;
import java.util.List;

public class TeamsView {
    private final EnumMap<Tower, List<String>> teams;

    public TeamsView(EnumMap<Tower, List<String>> teams){
        this.teams = teams;
    }

    public EnumMap<Tower, List<String>> getTeams() {
        return teams;
    }
}
