package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the enumerations contained in Server package.
 */
public class EnumsTest {

    /**
     * Tests method getFromInt in class AssistantCard.
     */
    @Test
    public void testAssistantCard() {
        assertEquals(AssistantCard.CHEETAH, AssistantCard.getFromInt(1));
        assertEquals(AssistantCard.OSTRICH, AssistantCard.getFromInt(2));
        assertEquals(AssistantCard.CAT, AssistantCard.getFromInt(3));
        assertEquals(AssistantCard.EAGLE, AssistantCard.getFromInt(4));
        assertEquals(AssistantCard.FOX, AssistantCard.getFromInt(5));
        assertEquals(AssistantCard.SNAKE, AssistantCard.getFromInt(6));
        assertEquals(AssistantCard.OCTOPUS, AssistantCard.getFromInt(7));
        assertEquals(AssistantCard.DOG, AssistantCard.getFromInt(8));
        assertEquals(AssistantCard.ELEPHANT, AssistantCard.getFromInt(9));
        assertEquals(AssistantCard.TURTLE, AssistantCard.getFromInt(10));
        assertNull(AssistantCard.getFromInt(11));
    }

    /**
     * Tests method getFromChar in class GameMode.
     */
    @Test
    public void testGameMode() {
        assertEquals(GameMode.EASY, GameMode.getFromChar("n"));
        assertEquals(GameMode.EXPERT, GameMode.getFromChar("e"));
        assertNull(GameMode.getFromChar("f"));
    }

    /**
     * Tests method getFromNumber in class GamePreset.
     */
    @Test
    public void testGamePreset() {
        assertEquals(GamePreset.TWO, GamePreset.getFromNumber(2));
        assertEquals(GamePreset.THREE, GamePreset.getFromNumber(3));
        assertEquals(GamePreset.FOUR, GamePreset.getFromNumber(4));
        assertNull(GamePreset.getFromNumber(5));
    }

    /**
     * Tests method getColorFromString in class StudentColor.
     */
    @Test
    public void testStudentColor() {
        assertEquals(StudentColor.GREEN, StudentColor.getColorFromString("green"));
        assertEquals(StudentColor.BLUE, StudentColor.getColorFromString("blue"));
        assertEquals(StudentColor.RED, StudentColor.getColorFromString("red"));
        assertEquals(StudentColor.YELLOW, StudentColor.getColorFromString("yellow"));
        assertEquals(StudentColor.MAGENTA, StudentColor.getColorFromString("magenta"));
        assertNull(StudentColor.getColorFromString("black"));
    }

    /**
     * Tests method getWizardFromString in class Wizard.
     */
    @Test
    public void testWizard() {
        assertEquals(Wizard.SENSEI, Wizard.getWizardFromString("sensei"));
        assertEquals(Wizard.KING, Wizard.getWizardFromString("king"));
        assertEquals(Wizard.MERLIN, Wizard.getWizardFromString("merlin"));
        assertEquals(Wizard.WITCH, Wizard.getWizardFromString("witch"));
        assertNull(Wizard.getWizardFromString("panda"));


    }
}
