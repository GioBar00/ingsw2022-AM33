package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.server.model.enums.Tower;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class contains the information about the teams.
 */
public class TeamsView implements Serializable {
    /**
     * teams that have formed up to now
     */
    private final EnumMap<Tower, List<String>> teams;
    /**
     * nicknames of the players that haven't chosen a teams yet
     */
    private final List<String> lobby;

    /**
     * Constructor
     *
     * @param teams teams that have formed up to now
     * @param lobby nicknames of the players that haven't chosen a teams yet
     */
    public TeamsView(EnumMap<Tower, List<String>> teams, List<String> lobby) {
        EnumMap<Tower, List<String>> teamsString = new EnumMap<>(Tower.class);
        for (Tower t : teams.keySet()) {
            teamsString.put(t, new LinkedList<>());
            ArrayList<String> playerNicks = new ArrayList<>();
            for (String s : teams.get(t)) {
                playerNicks.add(s);
            }
            teamsString.put(t, playerNicks);
        }
        this.teams = teamsString;
        this.lobby = lobby;
    }

    /**
     * @return the teams
     */
    public EnumMap<Tower, List<String>> getTeams() {
        return teams;
    }

    /**
     * @return the lobby
     */
    public List<String> getLobby() {
        return lobby;
    }
}
