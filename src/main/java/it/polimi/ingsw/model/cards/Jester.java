package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

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
        super(CharacterType.JESTER, 1);
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
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        if (areMovesValid(effectHandler, pairs, 3, new EnumMap<>(students))) {
            for (Pair<StudentColor, List<Integer>> pair: pairs) {
                StudentColor s = pair.getFirst();
                List<Integer> second = pair.getSecond();
                StudentColor onEntrance = effectHandler.popStudentFromEntrance(second.get(0));
                effectHandler.addStudentOnEntrance(s, second.get(0));
                students.put(s, students.get(s) - 1);
                students.put(onEntrance, students.get(onEntrance) + 1);
            }
            additionalCost++;
            appliedEffect = true;
            return true;
        }
        return false;
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
