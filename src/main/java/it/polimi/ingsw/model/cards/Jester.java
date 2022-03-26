package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import javax.naming.LimitExceededException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        Set<Map.Entry<StudentColor, List<Integer>>> entrySet = pairs.entrySet();
        int num = 0;
        for (Map.Entry<StudentColor, List<Integer>> entry: entrySet) {
            num += entry.getValue().size();
            if (students.get(entry.getKey()) < entry.getValue().size())
                return;
        }
        if (num > 3)
            return;
        for (Map.Entry<StudentColor, List<Integer>> entry: entrySet) {
            StudentColor s = entry.getKey();
            List<Integer> entranceIndexes = entry.getValue();
            for (Integer entranceIndex : entranceIndexes) {
                StudentColor ss = effectHandler.getStudentFromEntrance(entranceIndex);
                try {
                    effectHandler.addStudentOnEntrance(s, entranceIndex);
                } catch (LimitExceededException ignored) {
                }
                students.put(s, students.get(s) - 1);
                students.put(ss, students.get(s) + 1);
            }
        }
        additionalCost++;
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
