package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.List;

public class Knight extends CharacterCard {

    public Knight() {
        super(CharacterType.KNIGHT, 2);
    }

    @Override
    public void applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        effectHandler.addAdditionalInfluence(2);
        additionalCost++;
    }

    @Override
    public void endEffect(EffectHandler effectHandler) {
        effectHandler.addAdditionalInfluence(-2);
    }
}
