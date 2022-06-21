package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.network.messages.actions.ChosenIsland;
import it.polimi.ingsw.network.messages.actions.ChosenStudentColor;
import it.polimi.ingsw.network.messages.actions.MovedStudent;
import it.polimi.ingsw.network.messages.actions.SwappedStudents;
import it.polimi.ingsw.network.messages.actions.requests.SwapStudents;
import it.polimi.ingsw.server.model.cards.CharacterParameters;
import it.polimi.ingsw.server.model.enums.StudentColor;

/**
 * Static class used to convert a message into a CharacterParamters
 */
public class CharacterChoiceAdapter {


    /**
     * Convert a ChosenIsland message into the proper CharacterParameters
     *
     * @param message is an instance of ChosenIsland
     * @return the proper CharacterParameters
     */
    static CharacterParameters convert(ChosenIsland message) {
        return new CharacterParameters(message.getIslandIndex());
    }

    /**
     * Convert a ChosenStudentColor message into the proper CharacterParameters
     *
     * @param message is an instance of ChosenStudentColor
     * @return the proper CharacterParameters
     */
    static CharacterParameters convert(ChosenStudentColor message) {
        return new CharacterParameters(message.getStudentColor());
    }

    /**
     * Convert a MovedStudent message into the proper CharacterParameters
     *
     * @param message is an instance of MovedStudent
     * @return the proper CharacterParameters
     */
    static CharacterParameters convert(MovedStudent message) {
        return new CharacterParameters(StudentColor.retrieveStudentColorByOrdinal(message.getFromIndex()), message.getToIndex());
    }

    /**
     * Convert a SwappedStudents message into the proper CharacterParameters.
     *
     * @param message is an instance of SwappedStudents.
     * @return the proper CharacterParameters.
     */
    static CharacterParameters convert(SwappedStudents message) {
        return new CharacterParameters(StudentColor.retrieveStudentColorByOrdinal(message.getToIndex()), message.getFromIndex());
    }
}
