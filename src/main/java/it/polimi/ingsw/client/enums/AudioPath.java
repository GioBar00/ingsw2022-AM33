package it.polimi.ingsw.client.enums;

public enum AudioPath {
    START("/audio/start.mp3"),
    GAME("/audio/game.mp3"),
    LOBBY("/audio/lobby.mp3"),
    LOST("/audio/lost.mp3");
    private final String path;

    AudioPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
