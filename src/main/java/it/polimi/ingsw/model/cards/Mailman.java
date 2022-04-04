package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;

import java.util.List;

public class Mailman extends CharacterCard {

    public Mailman() {
        super(CharacterType.MAILMAN, 1);
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        effectHandler.addAdditionalMovement(2);
        additionalCost++;
        return true;
    }

    @Override
    public void endEffect(EffectHandler effectHandler) {
        effectHandler.addAdditionalMovement(-2);
    }
}
