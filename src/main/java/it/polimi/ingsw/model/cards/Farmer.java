package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;

import java.util.EnumMap;

/**
 * Farmer character card.
 */
public class Farmer extends CharacterCard {

    /**
     * Map of the professors to the corresponding original player index.
     */
    private EnumMap<StudentColor, Integer> original;

    /**
     * Creates the farmer.
     */
    public Farmer() {
        super(CharacterType.FARMER, 2);
    }

    /**
     * Applies the effect of the character card. Tries to give the professors to the current player if the students in the hall is equal.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs) {
        original = effectHandler.tryGiveProfsToCurrPlayer();
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
            effectHandler.restoreProfsToOriginalPlayer(original);
            appliedEffect = false;
        }
    }
}
