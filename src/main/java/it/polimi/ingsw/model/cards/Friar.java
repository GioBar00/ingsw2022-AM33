package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.EnumMap;
import java.util.List;

public class Friar extends CharacterCard {

    private final EnumMap<StudentColor, Integer> students = new EnumMap<>(StudentColor.class);

    public Friar() {
        super(CharacterType.FRIAR, 1);
        for (StudentColor s: StudentColor.values())
            students.put(s, 0);
    }

    @Override
    public void initialize(EffectHandler effectHandler) {
        for (int i = 0; i < 4; i++) {
            StudentColor s = effectHandler.getStudentFromBag();
            students.put(s, students.get(s) + 1);
        }
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        for (Pair<StudentColor, List<Integer>> pair: pairs) {
            StudentColor s = pair.getFirst();
            if (s != null && students.get(s) > 0 && pair.getSecond().size() >= 2) {
                if (effectHandler.addStudentToIsland(s, pair.getSecond().get(0), pair.getSecond().get(1))) {
                    students.replace(s, students.get(s) - 1);
                    additionalCost++;
                    s = effectHandler.getStudentFromBag();
                    students.replace(s, students.get(s) + 1);
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean containsStudents() {
        return true;
    }

    @Override
    public EnumMap<StudentColor, Integer> getStudents() {
        return students;
    }
}
