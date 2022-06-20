package it.polimi.ingsw.client.enums;

/**
 * Enum for the font paths.
 */
public enum FontPath {
    SONO("/fonts/Sono-Medium.ttf");

    /**
     * The path of the font file.
     */
    private final String path;

    /**
     * Constructor.
     *
     * @param path the path of the font file.
     */
    FontPath(String path) {
        this.path = path;
    }

    /**
     * Getter of the path of the font file.
     *
     * @return the path of the font file.
     */
    public String getPath() {
        return path;
    }
}
