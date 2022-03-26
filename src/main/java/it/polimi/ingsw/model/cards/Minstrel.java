package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import javax.naming.LimitExceededException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Minstrel extends CharacterCard {

    public Minstrel() {
        super(CharacterType.MINSTREL, 1);
    }

    @Override
    public void applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        Set<Map.Entry<StudentColor, List<Integer>>> entrySet = pairs.entrySet();
        int num = 0;
        for (Map.Entry<StudentColor, List<Integer>> entry: entrySet) {
            num += entry.getValue().size();
        }
        if (num > 3)
            return;
        for (Map.Entry<StudentColor, List<Integer>> entry: entrySet) {
            StudentColor s = effectHandler.getStudentFromEntrance(entry.getValue().get(0));
            try {
                try {
                    effectHandler.addStudentToHall(s);
                } catch (LimitExceededException e) {
                    effectHandler.addStudentOnEntrance(s, entry.getValue().get(0));
                }
                effectHandler.addStudentOnEntrance(entry.getKey(), entry.getValue().get(0));
            } catch (LimitExceededException ignored) {}

        }
        additionalCost++;
    }
}
