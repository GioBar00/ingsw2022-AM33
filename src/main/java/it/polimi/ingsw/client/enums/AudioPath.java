package it.polimi.ingsw.client.enums;

/**
 * Enum for the audio paths.
 */
public enum AudioPath {
    START("/audio/start.mp3"),
    GAME("/audio/game.mp3"),
    LOBBY("/audio/lobby.mp3"),
    LOST("/audio/lost.mp3"),
    WON("/audio/won.mp3"),
    DRAW("/audio/draw.mp3");

    /**
     * The path of the audio file.
     */
    private final String path;

    /**
     * Constructor.
     *
     * @param path the path of the audio file.
     */
    AudioPath(String path) {
        this.path = path;
    }

    /**
     * Getter of the path of the audio file.
     *
     * @return the path of the audio file.
     */
    public String getPath() {
        return path;
    }
}
