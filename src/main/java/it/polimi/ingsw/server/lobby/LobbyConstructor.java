package it.polimi.ingsw.server.lobby;

import it.polimi.ingsw.network.listeners.MessageListener;
import it.polimi.ingsw.server.model.enums.GamePreset;

/**
 * Class with a static method for building a Lobby
 */
public abstract class LobbyConstructor {

    /**
     * Return a specific lobby based on the preset given
     *
     * @param preset of the game
     * @return a team lobby that can hold the requested number of players
     */
    public static Lobby getLobby(GamePreset preset) {
        if (preset.equals(GamePreset.FOUR))
            return new TeamLobby(preset.getPlayersNumber());
        else return new Lobby(preset.getPlayersNumber());
    }
}
