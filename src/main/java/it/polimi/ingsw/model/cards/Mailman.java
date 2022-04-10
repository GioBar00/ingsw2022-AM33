package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;

/**
 * Mailman character card.
 */
public class Mailman extends CharacterCard {

    /**
     * Creates mailman.
     */
    public Mailman() {
        super(CharacterType.MAILMAN, 1);
    }

    /**
     * Applies the effect of the character card. Adds 2 additional possible movement.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs) {
        effectHandler.addAdditionalMovement(2);
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
            effectHandler.addAdditionalMovement(-2);
            appliedEffect = false;
        }

    }
}
