package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.StudentColor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Princess extends CharacterCard {

    private final EnumMap<StudentColor, Integer> students = new EnumMap<>(StudentColor.class);

    public Princess() {
        super(CharacterType.PRINCESS, 2);
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
    public boolean applyEffect(EffectHandler effectHandler, EnumMap<StudentColor, List<Integer>> pairs) {
        for (Map.Entry<StudentColor, List<Integer>> entry: pairs.entrySet()) {
            StudentColor s = entry.getKey();
            if (students.get(s) > 0) {
                if (effectHandler.addStudentToHall(s)) {
                    students.put(s, students.get(s) - 1);
                    additionalCost++;
                    s = effectHandler.getStudentFromBag();
                    if (s != null)
                        students.put(s, students.get(s) + 1);
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
