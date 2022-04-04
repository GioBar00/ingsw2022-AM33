package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;
import it.polimi.ingsw.util.LinkedPairList;
import it.polimi.ingsw.util.Pair;

import java.util.*;

public class Jester extends CharacterCard {

    private final EnumMap<StudentColor, Integer> students = new EnumMap<>(StudentColor.class);

    public Jester() {
        super(CharacterType.JESTER, 1);
        for (StudentColor s: StudentColor.values())
            students.put(s, 0);
    }

    @Override
    public void initialize(EffectHandler effectHandler) {
        for (int i = 0; i < 6; i++) {
            StudentColor s = effectHandler.getStudentFromBag();
            students.put(s, students.get(s) + 1);
        }
    }

    @Override
    public boolean applyEffect(EffectHandler effectHandler, LinkedPairList<StudentColor, List<Integer>> pairs) {
        if (areMovesValid(effectHandler, pairs, 3, new EnumMap<>(students))) {
            for (Pair<StudentColor, List<Integer>> pair: pairs) {
                StudentColor s = pair.getFirst();
                List<Integer> second = pair.getSecond();
                StudentColor onEntrance = effectHandler.popStudentFromEntrance(second.get(0));
                effectHandler.addStudentOnEntrance(s, second.get(0));
                students.replace(s, students.get(s) - 1);
                students.replace(onEntrance, students.get(onEntrance) + 1);
            }
            return true;
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
