package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;

import java.util.List;

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
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        effectHandler.ignoreTowers(true);
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
            effectHandler.ignoreTowers(false);
            appliedEffect = false;
        }
    }
}
