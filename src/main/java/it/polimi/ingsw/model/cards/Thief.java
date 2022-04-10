package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

/**
 * Thief character card.
 */
public class Thief extends CharacterCard {

    /**
     * Creates Thief
     */
    public Thief() {
        super(CharacterType.THIEF, 3);
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * Tries to remove 3 students of the selected student color from all the halls.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs) {
        for (Pair<StudentColor, Integer> pair: pairs) {
            if (pair.getFirst() != null) {
                effectHandler.tryRemoveStudentsFromHalls(pair.getFirst(), 3);
                additionalCost++;
                appliedEffect = true;
                return true;
            }
            return false;
        }
        return false;
    }
}
