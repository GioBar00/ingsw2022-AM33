package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.messagesView.TeamsView;

import java.util.EnumMap;
import java.util.List;

public class CurrentTeams extends Message {
    private TeamsView teamsView;

    public CurrentTeams(EnumMap<Tower, List<Player>> teams) {
        for (Tower t: teams.keySet()) {
            for (Player p: teams.get(t)) {
                teamsView.addTeammate(t, p.getNickname());
            }
        }
    }

    public TeamsView getTeamsView() {
        return teamsView;
    }

    @Override
    public boolean isValid() {
        return teamsView != null;
    }
}
