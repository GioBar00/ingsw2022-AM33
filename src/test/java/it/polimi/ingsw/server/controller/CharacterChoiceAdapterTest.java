package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.network.messages.actions.ChosenIsland;
import it.polimi.ingsw.network.messages.actions.ChosenStudentColor;
import it.polimi.ingsw.network.messages.actions.MovedStudent;
import it.polimi.ingsw.network.messages.actions.SwappedStudents;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.enums.StudentColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for CharacterChoiceAdapter class.
 */
class CharacterChoiceAdapterTest {

    /**
     * Tests all the methods contain in the static class CharacterChoiceAdapter
     */
    @Test
    void adapterTest() {
        CharacterParameters parameters;
        ChosenIsland chosenIsland = new ChosenIsland(10);
        parameters = CharacterChoiceAdapter.convert(chosenIsland);
        assertEquals(10, parameters.getIndex());
        assertNull(parameters.getStudentColor());

        ChosenStudentColor chosenStudentColor = new ChosenStudentColor(StudentColor.BLUE);
        parameters = CharacterChoiceAdapter.convert(chosenStudentColor);
        assertEquals(StudentColor.BLUE, parameters.getStudentColor());
        assertNull(parameters.getIndex());

        MovedStudent movedStudent = new MovedStudent(MoveLocation.CARD, 1, MoveLocation.CARD, 10);
        parameters = CharacterChoiceAdapter.convert(movedStudent);
        assertEquals(StudentColor.retrieveStudentColorByOrdinal(1), parameters.getStudentColor());
        assertEquals(10, parameters.getIndex());


        SwappedStudents swapStudents = new SwappedStudents(MoveLocation.CARD, 1, MoveLocation.HALL, 3);
        parameters = CharacterChoiceAdapter.convert(swapStudents);
        assertEquals(StudentColor.retrieveStudentColorByOrdinal(3), parameters.getStudentColor());
        assertEquals(StudentColor.retrieveStudentColorByOrdinal(1), StudentColor.retrieveStudentColorByOrdinal(parameters.getIndex()));


    }

}