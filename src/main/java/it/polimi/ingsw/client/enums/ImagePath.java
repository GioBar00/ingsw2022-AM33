package it.polimi.ingsw.client.enums;

/**
 * Enum for the image paths.
 */
public enum ImagePath {
    ICON("/images/icon.jpg"),
    START("/images/icons/start.png"),
    START_HIGHLIGHTED("/images/icons/start-highlighted.png"),
    VOLUME("/images/icons/volume.png"),
    MUTE("/images/icons/mute.png"),
    PROHIBITION("/images/icons/cancel.png"),
    GREEN_STUDENT("/images/pawns/students/green.png"),
    RED_STUDENT("/images/pawns/students/red.png"),
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
    BACK_CARD("/images/cards/back.jpg"),
    CENTAUR("/images/cards/character/centaur.jpg"),
    FARMER("/images/cards/character/farmer.jpg"),
    FRIAR("/images/cards/character/friar.jpg"),
    HARVESTER("/images/cards/character/harvester.jpg"),
    HERALD("/images/cards/character/herald.jpg"),
    HERBALIST("/images/cards/character/herbalist.jpg"),
    JESTER("/images/cards/character/jester.jpg"),
    KNIGHT("/images/cards/character/knight.jpg"),
    MAILMAN("/images/cards/character/mailman.jpg"),
    MINSTREL("/images/cards/character/minstrel.jpg"),
    PRINCESS("/images/cards/character/princess.jpg"),
    THIEF("/images/cards/character/thief.jpg"),
    CLOUD("/images/cloud.png"),
    ISLAND1("/images/island1.png"),
    ISLAND2("/images/island2.png"),
    ISLAND3("/images/island3.png"),
    MOTHER_NATURE("/images/mother_nature.png"),
    BLOCK("/images/deny_island_icon.png"),
    SENSEI("/images/wizards/sensei.jpg"),
    WITCH("/images/wizards/witch.jpg"),
    MERLIN("/images/wizards/merlin.jpg"),
    KING("/images/wizards/king.jpg"),
    RELOAD("/images/icons/reload.png"),
    RELOAD_HIGHLIGHTED("/images/icons/reload-highlighted.png");

    /**
     * The string path of the image.
     */
    private final String path;

    /**
     * Constructor of {@link ImagePath} class.
     *
     * @param path the path of the image.
     */
    ImagePath(String path) {
        this.path = path;
    }

    /**
     * Getter of the path of the image.
     *
     * @return the path of the image.
     */
    public String getPath() {
        return path;
    }
}
