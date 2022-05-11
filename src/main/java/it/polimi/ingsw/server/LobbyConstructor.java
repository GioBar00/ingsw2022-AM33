package it.polimi.ingsw.server;

import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.server.TeamLobby;
import it.polimi.ingsw.server.model.enums.GamePreset;

public abstract class LobbyConstructor {

    public static Lobby getLobby(GamePreset preset, MessageListener host){
        if(preset.equals(GamePreset.FOUR))
            return new TeamLobby(preset.getPlayersNumber(), host);
        else return new Lobby(preset.getPlayersNumber(), host);
    }
}
