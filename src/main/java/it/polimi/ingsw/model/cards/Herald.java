package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.List;

/**
 * Herald character card.
 */
public class Herald extends CharacterCard {

    /**
     * Creates the herald.
     */
    public Herald() {
        super(CharacterType.HERALD, 3);
    }

    /**
     * Applies the effect of the character card if the parameters are valid.
     * It forces the calc influence on an island group.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        for (Pair<StudentColor, List<Integer>> pair: pairs) {
            List<Integer> second = pair.getSecond();
            if (second != null && second.size() > 0 && second.get(0) != null && effectHandler.calcInfluenceOnIslandGroup(second.get(0))) {
                additionalCost++;
                appliedEffect = true;
                return true;
            }
            return false;
        }
        return false;
    }
}
