package it.polimi.ingsw.server.model.enums;

/**
 * types of students, by color
 */
public enum StudentColor {
    /**
     * Green StudentColor
     */
    GREEN,
    /**
     * Red StudentColor
     */
    RED,
    /**
     * Yellow StudentColor
     */
    YELLOW,
    /**
     * Magenta StudentColor
     */
    MAGENTA,
    /**
     * Blue StudentColor
     */
    BLUE;

    /**
     * private attribute to store the value of the color.
     */
    private static final StudentColor[] values = StudentColor.values();

    /**
     * Returns the StudentColor from a number.
     *
     * @param ordinal the number of the color.
     * @return the StudentColor.
     */
    public static StudentColor retrieveStudentColorByOrdinal(int ordinal) {
        return values[ordinal];
    }

    /**
     * Returns the StudentColor from a string.
     *
     * @param input the string of the color.
     * @return the StudentColor.
     */
    public static StudentColor getColorFromString(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "green" -> GREEN;
            case "red" -> RED;
            case "yellow" -> YELLOW;
            case "magenta" -> MAGENTA;
            case "blue" -> BLUE;
            default -> null;
        };
    }
}
