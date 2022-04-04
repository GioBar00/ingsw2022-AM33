package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Thief extends CharacterCard {

    public Thief() {
        super(CharacterType.THIEF, 3);
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        Set<Map.Entry<StudentColor, List<Integer>>> entries = pairs.entrySet();
        if (entries.size() > 1)
            return false;

        for (Map.Entry<StudentColor, List<Integer>> entry: entries) {
            effectHandler.tryRemoveStudentsFromHalls(entry.getKey(), 3);
            additionalCost++;
            return true;
        }
        return false;
    }
}
