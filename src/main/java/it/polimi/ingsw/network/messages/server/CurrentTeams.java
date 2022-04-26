package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.views.TeamsView;

/**
 * Message sent by the server to the client to notify the current teams
 */
public class CurrentTeams implements Message {
    /**
     * The current teams
     */
    private final TeamsView teamsView;

    /**
     * Constructor
     * @param teamsView the current teams
     */
    public CurrentTeams(TeamsView teamsView) {
        this.teamsView = teamsView;
    }

    /**
     * Getter
     * @return the current teams
     */
    public TeamsView getTeamsView() {
        return teamsView;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return teamsView.getTeams() != null || teamsView.getLobby() != null;
    }
}
