package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Herald extends CharacterCard {

    public Herald() {
        super(CharacterType.HERALD, 3);
    }

    @Override
    public void applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        for (Map.Entry<StudentColor, List<Integer>> entry: pairs.entrySet()) {
            effectHandler.calcInfluenceOnIslandGroup(entry.getValue().get(0));
            additionalCost++;
            return;
        }
    }
}
