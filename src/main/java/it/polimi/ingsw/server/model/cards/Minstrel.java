package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.network.messages.ActionRequest;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.actions.requests.SwapStudents;

import java.util.*;

/**
 * Minstrel character card.
 */
public class Minstrel extends CharacterCard {

    /**
     * Creates minstrel.
     */
    public Minstrel() {
        super(CharacterType.MINSTREL, 1, 1, 2);
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * Can exchange up to 2 students between the entrance and the hall.
     *
     * @param effectHandler handler for the effects.
     * @param parameters    for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect && parameters != null) {
            StudentColor s = parameters.getStudentColor();
            Integer entranceIndex = parameters.getIndex();
            if (s == null || entranceIndex == null)
                return false;
            if (effectHandler.removeStudentFromHall(s)) {
                StudentColor onEntrance = effectHandler.popStudentFromEntrance(entranceIndex);
                if (onEntrance != null) {
                    if (effectHandler.addStudentToHall(onEntrance)) {
                        effectHandler.addStudentOnEntrance(s, entranceIndex);
                        currentChoicesNumber++;
                        if (currentChoicesNumber >= maximumChoicesNumber)
                            endEffect();
                        return true;
                    } else {
                        effectHandler.addStudentOnEntrance(onEntrance, entranceIndex);
                        effectHandler.addStudentToHall(s);
                    }
                } else
                    effectHandler.addStudentToHall(s);
            }
        }
        return false;
    }

    /**
     * @param effectHandler effect handler.
     * @return swap student message between entrance and hall.
     */
    @Override
    public ActionRequest getRequiredAction(EffectHandler effectHandler) {
        List<StudentColor> entrance = effectHandler.getStudentsInEntrance();
        Set<Integer> availableStudents = getAvailableStudentsOrdinal(effectHandler.getHall());
        EnumMap<StudentColor, Integer> students = effectHandler.getHall();
        Set<Integer> entranceIndexes = getAvailableEntranceIndexes(effectHandler);
        entranceIndexes.removeIf(i -> students.get(entrance.get(i)) >= 10);
        return new SwapStudents(MoveLocation.ENTRANCE, entranceIndexes, MoveLocation.HALL, availableStudents);
    }
}
