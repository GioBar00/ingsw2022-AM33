package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.network.messages.ActionRequest;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.actions.requests.ChooseStudentColor;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Thief character card.
 */
public class Thief extends CharacterCard {

    /**
     * Creates Thief
     */
    public Thief() {
        super(CharacterType.THIEF, 3, 1);
    }

    /**
     * Applies the effect of the character card if the parameters are correct.
     * Tries to remove 3 students of the selected student color from all the halls.
     * @param effectHandler handler for the effects.
     * @param parameters for the effect.
     * @return if the effect was applied.
     */
    @Override
    public boolean applyEffect(EffectHandler effectHandler, CharacterParameters parameters) {
        if (!appliedEffect && parameters != null) {
            StudentColor s = parameters.getStudentColor();
            //FIXME: check professor and remove if 0
            if (s != null) {
                effectHandler.tryRemoveStudentsFromHalls(s, 3);
                effectHandler.checkProfessorOnRemove(s);
                currentChoicesNumber++;
                endEffect();
                return true;
            }
        }
        return false;
    }

    /**
     * @param effectHandler effect handler.
     * @return choose student color message.
     */
    @Override
    public ActionRequest getRequiredAction(EffectHandler effectHandler) {
        return new ChooseStudentColor(EnumSet.copyOf(Arrays.asList(StudentColor.values())));
    }
}
