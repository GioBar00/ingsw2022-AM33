package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.EnumSet;
import java.util.List;

public class Harvester extends CharacterCard {

    private final EnumSet<StudentColor> skipStudentColors = EnumSet.noneOf(StudentColor.class);

    public Harvester() {
        super(CharacterType.HARVESTER, 3);
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        for (Pair<StudentColor, List<Integer>> pair: pairs) {
            StudentColor s = pair.getFirst();
            if (s == null)
                return false;
            if (skipStudentColors.contains(s))
                return false;
            skipStudentColors.add(s);
            effectHandler.ignoreStudentColor(s, true);
            additionalCost++;
            return true;
        }

        return false;
    }

    @Override
    public void endEffect(EffectHandler effectHandler) {
        for (StudentColor s: skipStudentColors) {
            effectHandler.ignoreStudentColor(s, false);
        }
    }
}
