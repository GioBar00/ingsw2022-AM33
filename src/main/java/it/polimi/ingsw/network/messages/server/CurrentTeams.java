package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.model.enums.Tower;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.messages.Message;

import java.util.EnumMap;
import java.util.List;

public class CurrentTeams extends Message {
    private final EnumMap<Tower, List<Player>> teams;

    public CurrentTeams(EnumMap<Tower, List<Player>> teams) {
        this.teams = teams;
    }

    public EnumMap<Tower, List<Player>> getTeams() {
        return teams;
    }

    @Override
    public boolean isValid() {
        for (Tower t: teams.keySet()) {
            if (t == null)
                return false;
        }
        return true;
    }
}
