package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePreset;

/**
 * Message sent by the client to choose the game
 */
public class ChosenGame implements Message {
    /**
     * The chosen game preset
     */
    private final GamePreset preset;
    /**
     * The chosen game mode
     */
    private final GameMode mode;

    /**
     * Constructor
     * @param preset the chosen game preset
     * @param mode the chosen game mode
     */
    public ChosenGame(GamePreset preset, GameMode mode) {
        this.preset = preset;
        this.mode = mode;
    }

    /**
     * Gets the chosen game preset
     * @return the chosen game preset
     */
    public GamePreset getPreset() {
        return preset;
    }

    /**
     * Gets the chosen game mode
     * @return the chosen game mode
     */
    public GameMode getMode() {
        return mode;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return (preset.equals(GamePreset.TWO) || preset.equals(GamePreset.THREE) || preset.equals(GamePreset.FOUR))&&(mode.equals(GameMode.EASY) || mode.equals(GameMode.EXPERT));
    }
}
