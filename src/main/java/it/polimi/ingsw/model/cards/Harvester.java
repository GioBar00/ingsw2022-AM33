package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.EnumSet;

/**
 * Harvester character card.
 */
public class Harvester extends CharacterCard {

    /**
     * Creates harvester
     */
    public Harvester() {
        super(CharacterType.HARVESTER, 3);
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * Adds a student color to the ones to skip.
     * @param effectHandler handler for the effects.
     * @param pairs parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, Integer> pairs) {
        for (Pair<StudentColor, Integer> pair: pairs) {
            StudentColor s = pair.getFirst();
            if (s == null)
                return false;
            EnumSet<StudentColor> skipStudentColors = effectHandler.getSkippedStudentColors();
            if (skipStudentColors.contains(s))
                return false;
            skipStudentColors.add(s);
            additionalCost++;
            appliedEffect = true;
            return true;
        }

        return false;
    }

    /**
     * Ends the effect of the character card. It reverts the effect.
     * @param effectHandler handler for the effects.
     */
    @Override
    public void endEffect(EffectHandler effectHandler) {
        if (appliedEffect) {
            effectHandler.getSkippedStudentColors().clear();
            appliedEffect = false;
        }
    }
}
