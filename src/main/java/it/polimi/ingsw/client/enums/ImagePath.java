package it.polimi.ingsw.client.enums;

public enum ImagePath {
    ICON("/images/icon.jpg"),
    START("/images/icons/start.png"),
    START_HIGHLIGHTED("/images/icons/start-highlighted.png"),
    VOLUME("/images/icons/volume.png"),
    MUTE("/images/icons/mute.png"),
    TITLE("/images/title.png");


    private final String path;

    ImagePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
