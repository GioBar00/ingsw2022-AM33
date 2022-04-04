package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.EnumMap;
import java.util.List;

/**
 * Princess character card.
 */
public class Princess extends CharacterCard {

    /**
     * Students on the card.
     */
    private final EnumMap<StudentColor, Integer> students = new EnumMap<>(StudentColor.class);

    /**
     * Creates princess.
     */
    public Princess() {
        super(CharacterType.PRINCESS, 2);
        for (StudentColor s: StudentColor.values())
            students.put(s, 0);
    }

    /**
     * Initializes the character card. It gets 4 students from the bag.
     * @param effectHandler handler for the effects.
     */
    @Override
    public void initialize(EffectHandler effectHandler) {
        for (int i = 0; i < 4; i++) {
            StudentColor s = effectHandler.getStudentFromBag();
            students.put(s, students.get(s) + 1);
        }
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * Gets one student from the card, adds it to the hall and then gets one student from the bag.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        for (Pair<StudentColor, List<Integer>> entry: pairs) {
            StudentColor s = entry.getFirst();
            if (s != null && students.get(s) > 0) {
                if (effectHandler.addStudentToHall(s)) {
                    students.put(s, students.get(s) - 1);
                    additionalCost++;
                    s = effectHandler.getStudentFromBag();
                    if (s != null)
                        students.put(s, students.get(s) + 1);
                    appliedEffect = true;
                    return true;
                }
            }
            return false;
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
