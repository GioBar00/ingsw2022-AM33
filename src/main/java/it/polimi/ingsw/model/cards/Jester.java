package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

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
    public boolean applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        Set<Map.Entry<StudentColor, List<Integer>>> entrySet = pairs.entrySet();
        int num = 0;
        for (Map.Entry<StudentColor, List<Integer>> entry: entrySet) {
            num += entry.getValue().size();
            if (students.get(entry.getKey()) < entry.getValue().size())
                return false;
        }
        if (num > 3)
            return false;

        //FIXME: add get stud in entrance so that we can check if the sequence of moves is valid with the input given without modifying the model

        EnumMap<StudentColor, List<Integer>> removed = new EnumMap<>(StudentColor.class);
        for (Map.Entry<StudentColor, List<Integer>> entry: entrySet) {
            StudentColor s = entry.getKey();
            List<Integer> entranceIndexes = entry.getValue();
            for (Integer entranceIndex : entranceIndexes) {
                StudentColor ss = effectHandler.popStudentFromEntrance(entranceIndex);
                if (ss == null) {
                    for (Map.Entry<StudentColor, List<Integer>> e: removed.entrySet())
                        for (Integer i: e.getValue())
                            effectHandler.addStudentOnEntrance(e.getKey(), i);
                    return false;
                }
                if (removed.containsKey(ss)) {
                    List<Integer> list = new LinkedList<>();
                    list.add(entranceIndex);
                    removed.put(ss, list);
                }
                else
                    removed.get(ss).add(entranceIndex);

                effectHandler.addStudentOnEntrance(s, entranceIndex);
                students.replace(s, students.get(s) - 1);
                students.replace(ss, students.get(s) + 1);
            }
        }
        additionalCost++;
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
