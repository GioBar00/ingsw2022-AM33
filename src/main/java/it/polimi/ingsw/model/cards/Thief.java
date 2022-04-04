package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.List;

public class Thief extends CharacterCard {

    public Thief() {
        super(CharacterType.THIEF, 3);
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        for (Pair<StudentColor, List<Integer>> pair: pairs) {
            if (pair.getFirst() != null) {
                effectHandler.tryRemoveStudentsFromHalls(pair.getFirst(), 3);
                additionalCost++;
                return true;
            }
            return false;
        }
        return false;
    }
}
