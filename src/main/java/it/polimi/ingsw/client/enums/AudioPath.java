package it.polimi.ingsw.client.enums;

public enum AudioPath {
    START("");
    private final String path;

    AudioPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
