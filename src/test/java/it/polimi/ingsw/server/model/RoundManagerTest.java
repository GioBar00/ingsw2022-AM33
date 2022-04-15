package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.RoundManager;
import it.polimi.ingsw.server.model.enums.GamePhase;
import it.polimi.ingsw.server.model.enums.GamePreset;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoundManagerTest {
    /**
     * for every type of GamePreset (TWO, THREE, FOUR), the test checks that the RoundManager changes the phases of
     * the game in the right order; it also controls that the last round can be set correctly
     */
    @Test
    void roundPhaseOrder(){
        for (GamePreset gp: GamePreset.values()) {
            RoundManager roundManagerTest = new RoundManager(gp);
            // check of initial set up > phase: PLANNING
            assertEquals(roundManagerTest.getGamePhase(), GamePhase.PLANNING);
            assertFalse(roundManagerTest.isLastRound());
            assertEquals(0, roundManagerTest.getRoundNum());
            // phase: MOVE STUDENTS
            roundManagerTest.startActionPhase();
            assertNotEquals(roundManagerTest.getGamePhase(), GamePhase.PLANNING);
            assertEquals(roundManagerTest.getGamePhase(), GamePhase.MOVE_STUDENTS);
            // phase: MOVE_MOTHER_NATURE
            for(int i = 0; i < gp.getMaxNumMoves(); i ++){
                assertTrue(roundManagerTest.canMoveStudents());
                roundManagerTest.addMoves();
            }
            assertNotEquals(roundManagerTest.getGamePhase(), GamePhase.MOVE_STUDENTS);
            assertEquals(roundManagerTest.getGamePhase(), GamePhase.MOVE_MOTHER_NATURE);
            // phase: CHOOSE CLOUD
            roundManagerTest.startChooseCloudPhase();
            assertNotEquals(roundManagerTest.getGamePhase(), GamePhase.MOVE_MOTHER_NATURE);
            assertEquals(roundManagerTest.getGamePhase(), GamePhase.CHOOSE_CLOUD);
            // go to the next round
            roundManagerTest.nextRound();
            assertNotEquals(roundManagerTest.getGamePhase(), GamePhase.CHOOSE_CLOUD);
            assertEquals(roundManagerTest.getGamePhase(), GamePhase.PLANNING);
            assertEquals(1, roundManagerTest.getRoundNum());
            // set last round
            assertFalse(roundManagerTest.isLastRound());
            roundManagerTest.setLastRound();
            assertTrue(roundManagerTest.isLastRound());
        }
    }
}