package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

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
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs) {
        if (areMovesValid(effectHandler, pairs, 2, effectHandler.getHall())) {
            for (Pair<StudentColor, Integer> pair: pairs) {
                int entranceIndex = pair.getSecond();
                StudentColor s = pair.getFirst();
                StudentColor onEntrance = effectHandler.popStudentFromEntrance(entranceIndex);
                effectHandler.addStudentOnEntrance(s, entranceIndex);
                effectHandler.removeStudentFromHall(s);
                effectHandler.addStudentToHall(onEntrance);
            }
            additionalCost++;
            appliedEffect = true;
            return true;
        }
        return false;
    }


}
