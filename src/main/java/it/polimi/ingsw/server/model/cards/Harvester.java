package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.ChooseStudentColor;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Harvester character card.
 */
public class Harvester extends CharacterCard {

    /**
     * Creates harvester
     */
    public Harvester() {
        super(CharacterType.HARVESTER, 3, 1);
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * Adds a student color to the ones to skip.
     * @param effectHandler handler for the effects.
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect && parameters != null) {
            StudentColor s = parameters.getStudentColor();
            if (s == null)
                return false;
            EnumSet<StudentColor> skipStudentColors = effectHandler.getSkippedStudentColors();
            if (skipStudentColors.contains(s))
                return false;
            skipStudentColors.add(s);
            currentChoicesNumber++;
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
            effectHandler.getSkippedStudentColors().clear();
            appliedEffect = false;
        }
    }

    /**
     * @param effectHandler effect handler.
     * @return choose student color message.
     */
    @Override
    public Message getRequiredAction(EffectHandler effectHandler) {
        return new ChooseStudentColor(EnumSet.copyOf(Arrays.asList(StudentColor.values())));
    }
}
