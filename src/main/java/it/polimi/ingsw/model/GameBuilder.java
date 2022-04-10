package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enums.GameMode;
import it.polimi.ingsw.model.enums.GamePreset;

/**
 * Used to build the Game based on the preset and the mode.
 */
public abstract class GameBuilder {
    /**
     * Builds the game based on the preset and the mode.
     * @param preset of the game to create.
     * @param mode of the game to create.
     * @return the game.
     */
    public static Game getGame(GamePreset preset, GameMode mode) {
        GameModel model;
        if (preset == GamePreset.FOUR)
            model = new GameModelTeams();
        else
            model = new GameModel(preset);
        if (mode == GameMode.EXPERT)
             return new GameModelExpert(model);
        return model;
    }
}
