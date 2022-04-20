package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.messagesView.TeamsView;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class CurrentTeams extends Message {
    private final TeamsView teamsView;

    public CurrentTeams(EnumMap<Tower, List<Player>> teams) {
        EnumMap<Tower, List<String>> teamsString = new EnumMap<>(Tower.class);
        for (Tower t: teams.keySet()) {
            teamsString.put(t, new LinkedList<>());
            ArrayList<String> playerNicks = new ArrayList<>();
            for (Player p : teams.get(t)) {
                playerNicks.add(p.getNickname());
            }
            teamsString.put(t, playerNicks);
        }
        this.teamsView = new TeamsView(teamsString);
    }

    public TeamsView getTeamsView() {
        return teamsView;
    }

    @Override
    public boolean isValid() {
        return teamsView != null;
    }
}
