package it.polimi.ingsw.network.messages.views;

import it.polimi.ingsw.network.messages.InvalidMessage;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePhase;
import it.polimi.ingsw.server.model.enums.GamePreset;
import it.polimi.ingsw.server.model.enums.GameState;

import java.util.*;

public class GameView extends InvalidMessage {
    /**
     * mode of the game (easy or expert)
     */
    private final GameMode mode;
    /**
     * preset of the game (two, three or four players)
     */
    private final GamePreset preset;
    /**
     * state of the game (uninitialized, started, ended)
     */
    private final GameState state;
    /**
     * phase of the round in the game (planning or the action phases)
     */
    private final GamePhase phase;

    /**
     * view of the Islands
     */
    private final List<IslandGroupView> islandsView;
    /**
     * view of the Players
     */
    private final List<PlayerView> playersView;

    /**
     * index of the islands on which mother nature is on
     */
    private final Integer motherNatureIndex;

    /**
     * reserve of coins
     */
    private final Integer reserve;
    /**
     * view of the character cards
     */
    private final List<CharacterCardView> characterCardView;
    /**
     * coins of each player
     */
    private final Map<String, Integer> playerCoins;

    /**
     * constructor of the GameView for an easy game
     */
    public GameView(GameMode mode, GamePreset preset, GameState state, GamePhase phase, List<IslandGroupView> islandsView, List<PlayerView> playersView, Integer motherNatureIndex) {
        this.mode = mode;
        this.preset = preset;
        this.state = state;
        this.phase = phase;
        this.islandsView = islandsView;
        this.playersView = playersView;
        this.motherNatureIndex = motherNatureIndex;
        this.reserve = null;
        this.characterCardView = null;
        this.playerCoins = null;
    }

    /**
     * constructor of the GameView for an expert game (sets also the attributes for the CharacterCard)
     */
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

    /**
     * @return the mode
     */
    public GameMode getMode() {
        return mode;
    }

    /**
     * @return the preset
     */
    public GamePreset getPreset() {
        return preset;
    }

    /**
     * @return the state
     */
    public GameState getState() {
        return state;
    }

    /**
     * @return the phase
     */
    public GamePhase getPhase() {
        return phase;
    }

    /**
     * @return the islandView
     */
    public List<IslandGroupView> getIslandsView() {
        return islandsView;
    }

    /**
     * @return the playersView
     */
    public List<PlayerView> getPlayersView() {
        return playersView;
    }

    /**
     * @return the index of mother nature
     */
    public Integer getMotherNatureIndex() {
        return motherNatureIndex;
    }

    /**
     * @return the reserve
     */
    public Integer getReserve() {
        return reserve;
    }

    /**
     * @return the characterCardView
     */
    public List<CharacterCardView> getCharacterCardView() {
        return characterCardView;
    }

    /**
     * @return the playerCoins
     */
    public Map<String, Integer> getPlayerCoins() {
        return playerCoins;
    }
}
