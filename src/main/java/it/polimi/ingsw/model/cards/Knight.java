package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;

import java.util.List;

public class Knight extends CharacterCard {

    public Knight() {
        super(CharacterType.KNIGHT, 2);
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        effectHandler.addAdditionalInfluence(2);
        additionalCost++;
        return true;
    }

    @Override
    public void endEffect(EffectHandler effectHandler) {
        effectHandler.addAdditionalInfluence(-2);
    }
}
