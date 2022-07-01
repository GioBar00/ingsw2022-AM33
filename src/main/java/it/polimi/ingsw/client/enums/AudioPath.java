package it.polimi.ingsw.client.enums;

/**
 * Enum for the audio paths.
 */
public enum AudioPath {
    /**
     * start-screen audio file
     */
    START("/audio/start.mp3"),
    /**
     * game-screen audio file
     */
    GAME("/audio/game.mp3"),
    /**
     * lobby-screen and team-lobby-screen audio file
     */
    LOBBY("/audio/lobby.mp3"),
    /**
     * audio file for the winner-screen in case the player loses
     */
    LOST("/audio/lost.mp3"),
    /**
     * audio file for the winner-screen in case the player wins
     */
    WON("/audio/won.mp3"),
    /**
     * audio file for the winner-screen in case there is a draw
     */
    DRAW("/audio/draw.mp3"),
    /**
     * audio file for start effect
     */
    START_EFFECT("/audio/effects/start.mp3");

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
