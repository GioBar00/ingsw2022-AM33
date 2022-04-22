package it.polimi.ingsw.network.messages.messagesView;

import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePhase;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.GameState;
import it.polimi.ingsw.network.messages.Message;

import java.util.*;

public class GameView extends Message {
    private final GameMode mode;
    private final GamePreset preset;
    private final GameState state;
    private final GamePhase phase;

    private final List<IslandGroupView> islandsView;
    private final List<PlayerView> playersView;

    private final Integer motherNatureIndex;

    private Integer reserve = 0;
    private List<CharacterCardView> characterCardView = null;
    private Map<String, Integer> playerCoins = null;

    public GameView(GameMode mode, GamePreset preset, GameState state, GamePhase phase, List<IslandGroupView> islandsView, List<PlayerView> playersView, Integer motherNatureIndex) {
        this.mode = mode;
        this.preset = preset;
        this.state = state;
        this.phase = phase;
        this.islandsView = islandsView;
        this.playersView = playersView;
        this.motherNatureIndex = motherNatureIndex;
    }

    public GameView(GameMode mode, GamePreset preset, GameState state, GamePhase phase, List<IslandGroupView> islandsView, List<PlayerView> playersView, Integer motherNatureIndex, Integer reserve, List<CharacterCardView> characterCardView, Map<String, Integer> playerCoins) {
        this.mode = mode;
        this.preset = preset;
        this.state = state;
        this.phase = phase;
        this.islandsView = islandsView;
        this.playersView = playersView;
        this.motherNatureIndex = motherNatureIndex;
        this.reserve = reserve;
        this.characterCardView = characterCardView;
        this.playerCoins = playerCoins;
    }
}
