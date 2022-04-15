package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.Tower;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class TeamsView {
    private final EnumMap<Tower, List<String>> teams;

    public TeamsView() {
        this.teams = new EnumMap<>(Tower.class);
        for (Tower t: GamePreset.FOUR.getTowers()) {
            teams.put(t, new LinkedList<>());
        }
    }

    public void addTeammate(Tower tower, String nickname){
        List<String> temp;
        temp = teams.get(tower);
        temp.add(nickname);
        teams.put(tower, temp);
    }
}
