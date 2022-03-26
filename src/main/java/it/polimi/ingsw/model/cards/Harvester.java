package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class Harvester extends CharacterCard {

    private final EnumSet<StudentColor> skipStudentColors = EnumSet.noneOf(StudentColor.class);

    public Harvester() {
        super(CharacterType.HARVESTER, 3);
    }

    @Override
    public void applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        for (Map.Entry<StudentColor, List<Integer>> entry: pairs.entrySet()) {
            if (skipStudentColors.contains(entry.getKey()))
                return;
            skipStudentColors.add(entry.getKey());
            effectHandler.ignoreStudentColor(entry.getKey(), true);
            additionalCost++;
            return;
        }

    }

    @Override
    public void endEffect(EffectHandler effectHandler) {
        for (StudentColor s: skipStudentColors) {
            effectHandler.ignoreStudentColor(s, false);
        }
    }
}
