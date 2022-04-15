package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CharacterType;

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
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect) {
            effectHandler.addAdditionalInfluence(2);
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
            effectHandler.addAdditionalInfluence(-2);
            appliedEffect = false;
        }

    }
}
