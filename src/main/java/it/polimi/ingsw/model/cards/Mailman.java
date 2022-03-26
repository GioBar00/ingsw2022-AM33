package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.List;

public class Mailman extends CharacterCard {

    public Mailman() {
        super(CharacterType.MAILMAN, 1);
    }

    @Override
    public void applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        effectHandler.addAdditionalMovement(2);
        additionalCost++;
    }

    @Override
    public void endEffect(EffectHandler effectHandler) {
        effectHandler.addAdditionalMovement(-2);
    }
}
