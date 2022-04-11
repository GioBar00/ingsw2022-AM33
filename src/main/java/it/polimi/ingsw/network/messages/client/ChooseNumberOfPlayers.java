package it.polimi.ingsw.network.messages.client;


import it.polimi.ingsw.model.enums.GamePreset;
import it.polimi.ingsw.network.messages.Message;

public class ChooseNumberOfPlayers extends Message {
    GamePreset preset;

    public ChooseNumberOfPlayers(GamePreset preset) {
        this.preset = preset;
    }

    public GamePreset getPreset() {
        return preset;
    }

    @Override
    public boolean isValid() {
        return preset.equals(GamePreset.TWO) || preset.equals(GamePreset.THREE) || preset.equals(GamePreset.FOUR);
    }
}
