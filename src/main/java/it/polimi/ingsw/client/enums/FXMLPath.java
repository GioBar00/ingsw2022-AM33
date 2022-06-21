package it.polimi.ingsw.client.enums;

/**
 * Enum for the FXML paths.
 */
public enum FXMLPath {
    START_SCREEN("/fxml/start-screen.fxml"),
    CHOOSE_GAME("/fxml/choose-game.fxml"),
    CHOOSE_WIZARD("/fxml/choose-wizard.fxml"),
    TEAM_LOBBY("/fxml/team-lobby-screen.fxml"),
    LOBBY("/fxml/lobby-screen.fxml"),
    CHOOSE_ASSISTANT("/fxml/assistant-card-scene.fxml"),
    CHARACTER_CARD("/fxml/character-card.fxml"),
    GAME_SCREEN("/fxml/game-screen-copia.fxml"),
    SCHOOL_BOARD("/fxml/school-board.fxml"),
    ISLAND("/fxml/island.fxml"),
    ISLANDS("/fxml/islands.fxml"),
    CLOUD("/fxml/cloud.fxml"),
    PLAYER("/fxml/player.fxml"),

    CHOOSE_COLOR("/fxml/choose-color.fxml"),
    WINNER_SCREEN("/fxml/winner-screen.fxml"),

    WAITING_SCREEN("/fxml/waiting.fxml");

    /**
     * The path of the FXML file.
     */
    private final String path;

    /**
     * Constructor.
     *
     * @param path the path of the FXML file.
     */
    FXMLPath(String path) {
        this.path = path;
    }

    /**
     * Getter of the path of the FXML file.
     *
     * @return the path of the FXML file.
     */
    public String getPath() {
        return path;
    }
}
