package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.List;

public class Farmer extends CharacterCard {

    private EnumMap<StudentColor, Integer> original;

    public Farmer() {
        super(CharacterType.FARMER, 2);
    }

    @Override
    public void applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        original = effectHandler.tryGiveProfsToCurrPlayer();
        additionalCost++;
    }

    @Override
    public void endEffect(EffectHandler effectHandler) {
        effectHandler.restoreProfsToOriginalPlayer(original);
    }
}
