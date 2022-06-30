package it.polimi.ingsw.client.enums;

/**
 * Enum for the image paths.
 */
public enum ImagePath {
    /**
     * path to icon.jpg
     */
    ICON("/images/icon.jpg"),
    /**
     * path to start.png
     */
    START("/images/icons/start.png"),
    /**
     * path to start-highlighted.png
     */
    START_HIGHLIGHTED("/images/icons/start-highlighted.png"),
    /**
     * path to volume.png
     */
    VOLUME("/images/icons/volume.png"),
    /**
     * path to mute.png
     */
    MUTE("/images/icons/mute.png"),
    /**
     * path to cancel.png
     */
    PROHIBITION("/images/icons/cancel.png"),
    /**
     * path to green.png
     */
    GREEN_STUDENT("/images/pawns/students/green.png"),
    /**
     * path to red.png
     */
    RED_STUDENT("/images/pawns/students/red.png"),
    /**
     * path to yellow.png
     */
    YELLOW_STUDENT("/images/pawns/students/yellow.png"),
    /**
     * path to magenta.png
     */
    MAGENTA_STUDENT("/images/pawns/students/magenta.png"),
    /**
     * path to blue.png
     */
    BLUE_STUDENTS("/images/pawns/students/blue.png"),
    /**
     * path to green-professor.png
     */
    GREEN_PROF("/images/pawns/students/green-professor.png"),
    /**
     * path to red-professor.png
     */
    RED_PROF("/images/pawns/students/red-professor.png"),
    /**
     * path to yellow-professor.png
     */
    YELLOW_PROF("/images/pawns/students/yellow-professor.png"),
    /**
     * path to magenta-professor.png
     */
    MAGENTA_PROF("/images/pawns/students/magenta-professor.png"),
    /**
     * path to blue-professor.png
     */
    BLUE_PROF("/images/pawns/students/blue-professor.png"),
    /**
     * path to white-tower.png
     */
    WHITE_TOWER("/images/pawns/towers/white-tower.png"),
    /**
     * path to gray-tower.png
     */
    GRAY_TOWER("/images/pawns/towers/gray-tower.png"),
    /**
     * path to black-tower.png
     */
    BLACK_TOWER("/images/pawns/towers/black-tower.png"),
    /**
     * path to title.png
     */
    TITLE("/images/title.png"),
    /**
     * path to cheetah.png
     */
    CHEETAH("/images/cards/assistant/cheetah.png"),
    /**
     * path to cat.png
     */
    CAT("/images/cards/assistant/cat.png"),
    /**
     * path to dog.png
     */
    DOG("/images/cards/assistant/dog.png"),
    /**
     * path to eagle.png
     */
    EAGLE("/images/cards/assistant/eagle.png"),
    /**
     * path to elephant.png
     */
    ELEPHANT("/images/cards/assistant/elephant.png"),
    /**
     * path to fox.png
     */
    FOX("/images/cards/assistant/fox.png"),
    /**
     * path to octopus.png
     */
    OCTOPUS("/images/cards/assistant/octopus.png"),
    /**
     * path to ostrich.png
     */
    OSTRICH("/images/cards/assistant/ostrich.png"),
    /**
     * path to snake.png
     */
    SNAKE("/images/cards/assistant/snake.png"),
    /**
     * path to turtle.png
     */
    TURTLE("/images/cards/assistant/turtle.png"),
    /**
     * path to back.png
     */
    BACK_CARD("/images/cards/back.jpg"),
    /**
     * path to centaur.png
     */
    CENTAUR("/images/cards/character/centaur.jpg"),
    /**
     * path to farmer.png
     */
    FARMER("/images/cards/character/farmer.jpg"),
    /**
     * path to friar.png
     */
    FRIAR("/images/cards/character/friar.jpg"),
    /**
     * path to harvester.png
     */
    HARVESTER("/images/cards/character/harvester.jpg"),
    /**
     * path to herald.png
     */
    HERALD("/images/cards/character/herald.jpg"),
    /**
     * path to herbalist.png
     */
    HERBALIST("/images/cards/character/herbalist.jpg"),
    /**
     * path to jester.png
     */
    JESTER("/images/cards/character/jester.jpg"),
    /**
     * path to knight.png
     */
    KNIGHT("/images/cards/character/knight.jpg"),
    /**
     * path to mailman.png
     */
    MAILMAN("/images/cards/character/mailman.jpg"),
    /**
     * path to minstrel.png
     */
    MINSTREL("/images/cards/character/minstrel.jpg"),
    /**
     * path to princess.png
     */
    PRINCESS("/images/cards/character/princess.jpg"),
    /**
     * path to thief.png
     */
    THIEF("/images/cards/character/thief.jpg"),
    /**
     * path to cloud.png
     */
    CLOUD("/images/cloud.png"),
    /**
     * path to island1.png
     */
    ISLAND1("/images/island1.png"),
    /**
     * path to island2.png
     */
    ISLAND2("/images/island2.png"),
    /**
     * path to island3.png
     */
    ISLAND3("/images/island3.png"),
    /**
     * path to mother_nature.png
     */
    MOTHER_NATURE("/images/mother_nature.png"),
    /**
     * path to deny_island_icon.png
     */
    BLOCK("/images/deny_island_icon.png"),
    /**
     * path to sensei.jpg
     */
    SENSEI("/images/wizards/sensei.jpg"),
    /**
     * path to witch.jpg
     */
    WITCH("/images/wizards/witch.jpg"),
    /**
     * path to merlin.jpg
     */
    MERLIN("/images/wizards/merlin.jpg"),
    /**
     * path to king.jpg
     */
    KING("/images/wizards/king.jpg"),
    /**
     * path to reload.png
     */
    RELOAD("/images/icons/reload.png"),
    /**
     * path to reload_highlighted.png
     */
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
