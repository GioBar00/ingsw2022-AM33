package it.polimi.ingsw.server.model.enums;

/**
 * enumeration of GameModes
 */
public enum GameMode {
    EASY,
    EXPERT;

    /**
     * Convert a string into a Game Model
     *
     * @param in a String. Valid ones are "n" or "e"
     * @return a GameMode if the input is valid, null if it's not valid
     */
    public static GameMode getFromChar(String in) {
        if (in.equals("n"))
            return EASY;
        if (in.equals("e"))
            return EXPERT;
        else return null;
    }
}
