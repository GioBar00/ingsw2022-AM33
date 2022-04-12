package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.model.RoundManager;
import it.polimi.ingsw.model.enums.*;
import it.polimi.ingsw.model.islands.IslandsManager;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PlayersManager;
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
