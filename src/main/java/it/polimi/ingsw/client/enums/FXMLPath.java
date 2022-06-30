package it.polimi.ingsw.client.enums;

/**
 * Enum for the FXML paths.
 */
public enum FXMLPath {
    /**
     * path to start-screen.fxml
     */
    START_SCREEN("/fxml/start-screen.fxml"),
    /**
     * path to choose-game.fxml
     */
    CHOOSE_GAME("/fxml/choose-game.fxml"),
    /**
     * path to choose-wizard.fxml
     */
    CHOOSE_WIZARD("/fxml/choose-wizard.fxml"),
    /**
     * path to team-lobby-screen.fxml
     */
    TEAM_LOBBY("/fxml/team-lobby-screen.fxml"),
    /**
     * path to lobby-screen.fxml
     */
    LOBBY("/fxml/lobby-screen.fxml"),
    /**
     * path to assistant-card-scene.fxml
     */
    CHOOSE_ASSISTANT("/fxml/assistant-card-scene.fxml"),
    /**
     * path to character-card.fxml
     */
    CHARACTER_CARD("/fxml/character-card.fxml"),
    /**
     * path to game-screen.fxml
     */
    GAME_SCREEN("/fxml/game-screen.fxml"),
    /**
     * path to school-board.fxml
     */
    SCHOOL_BOARD("/fxml/school-board.fxml"),
    /**
     * path to island.fxml
     */
    ISLAND("/fxml/island.fxml"),
    /**
     * path to islands.fxml
     */
    ISLANDS("/fxml/islands.fxml"),
    /**
     * path to cloud.fxml
     */
    CLOUD("/fxml/cloud.fxml"),
    /**
     * path to player.fxml
     */
    PLAYER("/fxml/player.fxml"),

    /**
     * path to choose-color.fxml
     */
    CHOOSE_COLOR("/fxml/choose-color.fxml"),
    /**
     * path to winner-screen.fxml
     */
    WINNER_SCREEN("/fxml/winner-screen.fxml"),

    /**
     * path to waiting.fxml
     */
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
