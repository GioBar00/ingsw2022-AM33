package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.enums.CharacterType;
import it.polimi.ingsw.model.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;

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
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs) {
        effectHandler.ignoreTowers(true);
        additionalCost++;
        appliedEffect = true;
        return true;
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
