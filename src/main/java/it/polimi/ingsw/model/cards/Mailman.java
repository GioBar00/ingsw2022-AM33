package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;

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
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect) {
            effectHandler.addAdditionalMovement(2);
            endEffect();
            return true;
        }
        return false;
    }

    /**
     * Reverts the effect of the character card.
     * @param effectHandler handler for the effects.
     */
    @Override
    public void revertEffect(EffectHandler effectHandler) {
        if (appliedEffect) {
            effectHandler.addAdditionalMovement(-2);
            appliedEffect = false;
        }

    }
}
