package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;

/**
 * Centaur character card.
 */
public class Centaur extends CharacterCard {

    /**
     * Creates the centaur.
     */
    public Centaur() {
        super(CharacterType.CENTAUR, 3);
    }

    /**
     * Applies the effect of the character card. It sets to ignore the towers this turn.
     * @param effectHandler handler for the effects.
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect) {
            effectHandler.ignoreTowers(true);
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
            effectHandler.ignoreTowers(false);
            appliedEffect = false;
        }
    }
}
