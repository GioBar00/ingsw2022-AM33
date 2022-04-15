package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.SwapStudents;

import java.util.*;

/**
 * Jester character card.
 */
public class Jester extends CharacterCard {

    /**
     * Students on the card.
     */
    private final EnumMap<StudentColor, Integer> students = new EnumMap<>(StudentColor.class);

    /**
     * Creates jester.
     */
    public Jester() {
        super(CharacterType.JESTER, 1, 1, 3);
        for (StudentColor s: StudentColor.values())
            students.put(s, 0);
    }

    /**
     * Initializes the character card. It gets 6 students from the bag.
     * @param effectHandler handler for the effects.
     */
    @Override
    public void initialize(EffectHandler effectHandler) {
        for (int i = 0; i < 6; i++) {
            StudentColor s = effectHandler.getStudentFromBag();
            students.put(s, students.get(s) + 1);
        }
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * Can exchange at most 3 students from this card with the ones in the entrance.
     * @param effectHandler handler for the effects.
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect && parameters != null) {
            StudentColor s = parameters.getStudentColor();
            if (s == null || students.get(s) <= 0)
                return false;
            Integer entranceIndex = parameters.getIndex();
            if (entranceIndex == null)
                return false;
            StudentColor onEntrance = effectHandler.popStudentFromEntrance(entranceIndex);
            if (onEntrance == null)
                return false;
            effectHandler.addStudentOnEntrance(s, entranceIndex);
            students.put(s, students.get(s) - 1);
            students.put(onEntrance, students.get(onEntrance) + 1);
            currentChoicesNumber++;
            if (currentChoicesNumber >= maximumChoicesNumber)
                endEffect();
            return true;
        }
        return false;
    }

    /**
     * @param effectHandler effect handler.
     * @return swap students message between card and entrance.
     */
    @Override
    public Message getCommandMessage(EffectHandler effectHandler) {
        Set<Integer> availableStudents = getAvailableStudentsOrdinal(students);
        Set<Integer> entranceIndexes = getAvailableEntranceIndexes(effectHandler);
        return new SwapStudents(MoveLocation.CARD, availableStudents, MoveLocation.ENTRANCE, entranceIndexes);
    }

    /**
     * @return if it contains students.
     */
    @Override
    public boolean containsStudents() {
        return true;
    }

    /**
     * @return the students on the card
     */
    @Override
    public EnumMap<StudentColor, Integer> getStudents() {
        return new EnumMap<>(students);
    }
}
