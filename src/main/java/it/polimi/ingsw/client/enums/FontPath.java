package it.polimi.ingsw.client.enums;

/**
 * Enum for the font paths.
 */
public enum FontPath {
    TRATTATELLO("/fonts/Trattatello.ttf");

    private final String path;

    FontPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
