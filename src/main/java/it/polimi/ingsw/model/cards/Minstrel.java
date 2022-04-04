package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.EnumMap;
import java.util.List;

/**
 * Minstrel character card.
 */
public class Minstrel extends CharacterCard {

    /**
     * Creates minstrel.
     */
    public Minstrel() {
        super(CharacterType.MINSTREL, 1);
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * Can exchange up to 2 students between the entrance and the hall.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        EnumMap<StudentColor, Integer> hallCopy = new EnumMap<>(StudentColor.class);
        for (StudentColor s: StudentColor.values())
            hallCopy.put(s, effectHandler.getStudentsInHall(s));
        if (areMovesValid(effectHandler, pairs, 2, hallCopy)) {
            for (Pair<StudentColor, List<Integer>> pair: pairs) {
                int index = pair.getSecond().get(0);
                StudentColor s = pair.getFirst();
                StudentColor onEntrance = effectHandler.popStudentFromEntrance(index);
                effectHandler.addStudentOnEntrance(s, index);
                effectHandler.removeStudentFromHall(s);
                effectHandler.addStudentToHall(onEntrance);
            }
            appliedEffect = true;
            return true;
        }
        return false;
    }


}
