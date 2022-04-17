package it.polimi.ingsw.server.controllers;

import it.polimi.ingsw.network.messages.client.ChosenIsland;
import it.polimi.ingsw.network.messages.client.ChosenStudentColor;
import it.polimi.ingsw.network.messages.client.MovedStudent;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.enums.StudentColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for CharacterChoiceAdapter class
 */
class CharacterChoiceAdapterTest {

    /**
     * Tests all the methods contain in the static class CharacterChoiceAdapter
     */
    @Test
    void adapterTest(){
        CharacterParameters parameters;
        ChosenIsland chosenIsland = new ChosenIsland(10);
        parameters = CharacterChoiceAdapter.convert(chosenIsland);
        assertEquals(10,parameters.getIndex());
        assertNull(parameters.getStudentColor());

        ChosenStudentColor chosenStudentColor = new ChosenStudentColor(StudentColor.BLUE);
        parameters = CharacterChoiceAdapter.convert(chosenStudentColor);
        assertEquals(StudentColor.BLUE, parameters.getStudentColor());
        assertNull(parameters.getIndex());

        MovedStudent movedStudent = new MovedStudent(MoveLocation.CARD, 1,MoveLocation.CARD,10);
        parameters = CharacterChoiceAdapter.convert(movedStudent);
        assertEquals(StudentColor.retrieveStudentColorByOrdinal(1), parameters.getStudentColor());
        assertEquals(10, parameters.getIndex());


    }

}