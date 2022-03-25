package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.GameMode;
import it.polimi.ingsw.enums.GamePreset;

public abstract class GameBuilder {
    public static Game getGame(GamePreset preset, GameMode mode) {
        GameModel model;
        if (preset.equals(GamePreset.FOUR))
            model = new GameModelTeams();
        else
            model = new GameModel(preset);
        if (mode.equals(GameMode.EXPERT))
             return new GameModelExpert(model);
        return model;
    }
}
