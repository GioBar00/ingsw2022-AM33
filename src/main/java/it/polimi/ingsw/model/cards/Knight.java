package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;

/**
 * Knight character card.
 */
public class Knight extends CharacterCard {

    /**
     * Creates knight.
     */
    public Knight() {
        super(CharacterType.KNIGHT, 2);
    }

    /**
     * Applies the effect of the character card. It adds 2 influence points this turn.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs) {
        effectHandler.addAdditionalInfluence(2);
        additionalCost++;
        appliedEffect = true;
        return true;
    }

    /**
     * Ends the effect of the character card. It reverts the effect.
     * @param effectHandler handler for the effects.
     */
    @Override
    public void endEffect(EffectHandler effectHandler) {
        if (appliedEffect) {
            effectHandler.addAdditionalInfluence(-2);
            appliedEffect = false;
        }

    }
}
