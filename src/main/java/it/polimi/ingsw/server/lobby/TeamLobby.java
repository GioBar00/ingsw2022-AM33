package it.polimi.ingsw.server.lobby;

import it.polimi.ingsw.network.messages.server.CurrentTeams;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.listeners.MessageEvent;
import it.polimi.ingsw.server.PlayerDetails;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Class that handle the game lobby if the party is a team game
 */
public class TeamLobby extends Lobby {

    /**
     * Public constructor
     *
     * @param maxPlayers the number of the max players that can be hosted
     */
    public TeamLobby(int maxPlayers) {
        super(maxPlayers);
    }

    /**
     * method to add a player to the lobby
     *
     * @param nickname of the player
     * @return true if the method was executed without errors
     */
    @Override
    public boolean addPlayer(String nickname) {
        if (super.addPlayer(nickname)) {
            notifyMessageListeners(new MessageEvent(this, new CurrentTeams(getTeamView())));
            return true;
        }
        return false;
    }

    /**
     * @return true if the teams are balanced and the game can be started
     */
    @Override
    public boolean canStart() {
        if (!super.canStart())
            return false;
        else {
            int black = 0;
            int white = 0;
            for (PlayerDetails p : players) {
                if (p.getTower() != null) {
                    if (p.getTower().equals(Tower.WHITE))
                        white++;
                    else if (p.getTower().equals(Tower.BLACK))
                        black++;
                }
            }
            return white == 2 && black == 2;
        }
    }

    /**
     * method to allow a player to change their teams
     *
     * @param nickname of the player
     * @param tower    of the teams that the player wants to be added to
     * @return true if the method was executed correctly
     */
    @Override
    public boolean changeTeam(String nickname, Tower tower) {
        PlayerDetails update = null;
        if (tower.equals(Tower.GREY))
            return false;
        for (PlayerDetails p : players) {
            if (p.getNickname().equals(nickname))
                update = p;
        }
        if (update != null) {
            update.setTower(tower);
            notifyTeams();
            return true;
        } else return false;
    }

    /**
     * @return the teams view
     */
    @Override
    public TeamsView getTeamView() {
        EnumMap<Tower, List<String>> teams = new EnumMap<>(Tower.class);
        List<String> lobby = new ArrayList<>();
        for (Tower t : Tower.values()) {
            if (!t.equals(Tower.GREY))
                teams.put(t, new ArrayList<>());
        }
        for (PlayerDetails p : players) {
            if (p.getTower() != null) {
                teams.get(p.getTower()).add(p.getNickname());
            } else lobby.add(p.getNickname());
        }
        return new TeamsView(teams, lobby);
    }

}
