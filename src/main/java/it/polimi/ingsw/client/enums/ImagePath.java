package it.polimi.ingsw.client.enums;

public enum ImagePath {
    ICON("/images/icon.jpg"),
    START("/images/icons/start.png"),
    START_HIGHLIGHTED("/images/icons/start-highlighted.png"),
    VOLUME("/images/icons/volume.png"),
    MUTE("/images/icons/mute.png"),
    TITLE("/images/title.png"),
    CHEETAH("/images/cards/assistant/cheetah.png"),
    CAT("/images/cards/assistant/cat.png"),
    DOG("/images/cards/assistant/dog.png"),
    EAGLE("/images/cards/assistant/eagle.png"),
    ELEPHANT("/images/cards/assistant/elephant.png"),
    FOX("/images/cards/assistant/fox.png"),
    OCTOPUS("/images/cards/assistant/octopus.png"),
    OSTRICH("/images/cards/assistant/ostrich.png"),
    SNAKE("/images/cards/assistant/snake.png"),
    TURTLE("/images/cards/assistant/turtle.png"),
    BACK_CARD("/images/cards/back.jpg");


    private final String path;

    ImagePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
