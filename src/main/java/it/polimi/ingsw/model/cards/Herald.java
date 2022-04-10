package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;


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
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs) {
        for (Pair<StudentColor, Integer> pair: pairs) {
            Integer islandGroupIndex = pair.getSecond();
            if (islandGroupIndex != null && effectHandler.calcInfluenceOnIslandGroup(islandGroupIndex)) {
                additionalCost++;
                appliedEffect = true;
                return true;
            }
            return false;
        }
        return false;
    }
}
