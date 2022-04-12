package it.polimi.ingsw.model.enums;

/**
 * types of students, by color
 */
public enum StudentColor {
    GREEN, RED, YELLOW, PINK, BLUE;

    private static final StudentColor[] values = StudentColor.values();

    public static StudentColor retrieveStudentColorByOrdinal(int ordinal) {
        return values[ordinal];
    }
}
