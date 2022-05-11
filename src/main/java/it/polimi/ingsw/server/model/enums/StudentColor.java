package it.polimi.ingsw.server.model.enums;

/**
 * types of students, by color
 */
public enum StudentColor {
    GREEN, RED, YELLOW, PINK, BLUE;

    private static final StudentColor[] values = StudentColor.values();

    public static StudentColor retrieveStudentColorByOrdinal(int ordinal) {
        return values[ordinal];
    }

    public static StudentColor getColorFromString(String input){
        input = input.toLowerCase();
        return switch (input) {
            case "green" -> GREEN;
            case "red" -> RED;
            case "yellow" -> YELLOW;
            case "pink" -> PINK;
            case "blue" -> BLUE;
            default -> null;
        };
    }
}
