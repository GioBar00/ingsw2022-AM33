package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.RoundManager;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePhase;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.GameState;
import it.polimi.ingsw.server.model.islands.IslandsManager;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.PlayersManager;
import it.polimi.ingsw.network.messages.Message;

import java.util.*;

public class GameView extends Message {
    GameMode mode;
    GamePreset preset;
    GameState state;
    GamePhase phase;

    ArrayList<IslandGroupView> islandsView;
    ArrayList<PlayerView> playersView;
    
    Integer reserve = 0;
    Integer motherNatureIndex = 0;

    ArrayList<CharacterCardView> characterCardView;

    public GameView(GameMode mode, GameState state, PlayersManager playersManager, RoundManager roundManager, IslandsManager islandsManager, Player destPlayer, Integer reserve, int motherNatureIndex) {
        this.mode = mode;
        this.preset = playersManager.getPreset();
        this.phase = roundManager.getGamePhase();
        this.state = state;

        this.motherNatureIndex += motherNatureIndex;

        this.islandsView = islandsManager.getIslandsView();
        this.playersView = playersManager.getPlayersView(destPlayer);

        this.reserve += reserve;

        this.characterCardView = null;
    }

    public ArrayList<PlayerView> getPlayersView() {
        return playersView;
    }

    public ArrayList<CharacterCardView> getCharacterCardView() {
        return characterCardView;
    }
}
