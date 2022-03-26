package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.List;

public class Centaur extends CharacterCard {

    public Centaur() {
        super(CharacterType.CENTAUR, 3);
    }

    @Override
    public void applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        effectHandler.ignoreTowers(true);
        additionalCost++;
    }

    @Override
    public void endEffect(EffectHandler effectHandler) {
        effectHandler.ignoreTowers(false);
    }
}
