package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.messagesView.TeamsView;

public class CurrentTeams extends Message {
    private final TeamsView teamsView;

    public CurrentTeams(TeamsView teamsView) {
        this.teamsView = teamsView;
    }

    public TeamsView getTeamsView() {
        return teamsView;
    }

    @Override
    public boolean isValid() {
        if (teamsView.getTeams() == null && teamsView.getLobby() == null)
            return false;
        return true;
    }
}
