package it.polimi.ingsw.client.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum class for the possible colors used in the CLI.
 */
public enum Color {
    RESET("reset"),
    BLACK("black"),
    GREEN("green"),
    YELLOW("yellow"),
    BLUE("blue"),
    MAGENTA("magenta"),
    CYAN("cyan"),
    WHITE("white"),
    RED("red");

    /**
     * The color's name.
     */
    private final String name;

    /**
     * Constructor.
     *
     * @param name the color's name.
     */
    Color(String name) {
        this.name = name;
    }

    /**
     * Getter of the color's name.
     *
     * @return the color's name.
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns a map that contains the color's name as key and the command for getting that color in the CLI.
     *
     * @return a map that contains the color's name as key and the command for getting that color in the CLI.
     */
    public static Map<String, String> getColors() {
        Map<String, String> colors = new HashMap<>();
        colors.put(RESET.getName(), "\033[0m");
        colors.put(BLACK.getName(), "\033[30m");
        colors.put(RED.getName(), "\033[31m");
        colors.put(GREEN.getName(), "\033[32m");
        colors.put(YELLOW.getName(), "\033[33m");
        colors.put(BLUE.getName(), "\033[34m");
        colors.put(MAGENTA.getName(), "\033[35m");
        colors.put(CYAN.getName(), "\033[36m");
        colors.put(WHITE.getName(), "\033[37m");
        return colors;
    }

}
