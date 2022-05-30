package it.polimi.ingsw.client.enums;

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
