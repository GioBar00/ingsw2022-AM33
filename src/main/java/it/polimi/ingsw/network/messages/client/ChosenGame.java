package it.polimi.ingsw.network.messages.client;


import it.polimi.ingsw.model.enums.GameMode;
import it.polimi.ingsw.model.enums.GamePreset;
import it.polimi.ingsw.network.messages.Message;

public class ChosenGame extends Message {
    GamePreset preset;
    GameMode mode;
    public ChosenGame(GamePreset preset, GameMode mode) {
        this.preset = preset;
        this.mode = mode;
    }


    public GamePreset getPreset() {
        return preset;
    }

    public GameMode getMode(){return mode;}
    @Override
    public boolean isValid() {
        return (preset.equals(GamePreset.TWO) || preset.equals(GamePreset.THREE) || preset.equals(GamePreset.FOUR))&&(mode.equals(GameMode.EASY) || mode.equals(GameMode.EXPERT));
    }
}
