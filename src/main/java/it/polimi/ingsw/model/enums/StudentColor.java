package it.polimi.ingsw.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * types of students, by color
 */
public enum StudentColor {
    GREEN(0), RED(1), YELLOW(2), PINK(3), BLUE(4);

    private final int num;

    private static final Map<Integer, StudentColor> LOOKUP_MAP;

    static {
        LOOKUP_MAP = new HashMap<>();
        for (StudentColor s: StudentColor.values())
            LOOKUP_MAP.put(s.getNum(), s);
    }

    StudentColor(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public static StudentColor retrieveStudentColorByNumber(int num) {
        return LOOKUP_MAP.get(num);
    }
}
