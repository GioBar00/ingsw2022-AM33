package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.List;

public class Herald extends CharacterCard {

    public Herald() {
        super(CharacterType.HERALD, 3);
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        for (Pair<StudentColor, List<Integer>> pair: pairs) {
            List<Integer> second = pair.getSecond();
            if (second != null && second.size() > 0 && effectHandler.calcInfluenceOnIslandGroup(second.get(0))) {
                additionalCost++;
                return true;
            }
            return false;
        }
        return false;
    }
}
