package it.polimi.ingsw.client.enums;

public enum ImagePath {
    ICON("/images/icon.jpg"),
    START("/images/icons/start.png"),
    START_HIGHLIGHTED("/images/icons/start-highlighted.png"),
    VOLUME("/images/icons/volume.png"),
    MUTE("/images/icons/mute.png"),
    GREEN_STUDENT("/images/pawns/students/green.png"),
    RED_STUDENT("images/pawns/students/red.png"),
    YELLOW_STUDENT("/images/pawns/students/yellow.png"),
    MAGENTA_STUDENT("/images/pawns/students/magenta.png"),
    BLUE_STUDENTS("/images/pawns/students/blue.png"),
    GREEN_PROF("/images/pawns/students/green-professor.png"),
    RED_PROF("/images/pawns/students/red-professor.png"),
    YELLOW_PROF("/images/pawns/students/yellow-professor.png"),
    MAGENTA_PROF("/images/pawns/students/magenta-professor.png"),
    BLUE_PROF("/images/pawns/students/blue-professor.png"),
    WHITE_TOWER("/images/pawns/towers/white-tower.png"),
    GRAY_TOWER("/images/pawns/towers/gray-tower.png"),
    BLACK_TOWER("/images/pawns/towers/black-tower.png"),
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
