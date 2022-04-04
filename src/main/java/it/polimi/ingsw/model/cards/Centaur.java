package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;

import java.util.List;

public class Centaur extends CharacterCard {

    public Centaur() {
        super(CharacterType.CENTAUR, 3);
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        effectHandler.ignoreTowers(true);
        additionalCost++;
        return true;
    }

    @Override
    public void endEffect(EffectHandler effectHandler) {
        effectHandler.ignoreTowers(false);
    }
}
