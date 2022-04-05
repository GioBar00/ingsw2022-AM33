package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.GameMode;
import it.polimi.ingsw.enums.GamePreset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for checking the creation of a new Game
 */
class GameBuilderTest {

    /**
     * Tries to create a new game with the right preset
     */
    @Test
    void getGame() {
        Game brandNew = GameBuilder.getGame(GamePreset.TWO, GameMode.EASY);
        assertEquals(GameMode.EASY,brandNew.getGameMode());
        brandNew = GameBuilder.getGame(GamePreset.THREE, GameMode.EASY);
        assertEquals(GameMode.EASY,brandNew.getGameMode());
        brandNew = GameBuilder.getGame(GamePreset.FOUR, GameMode.EXPERT);
        assertEquals(GameMode.EXPERT,brandNew.getGameMode());
    }
}