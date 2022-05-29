package it.polimi.ingsw.client.enums;

public enum FXMLPath {
    START_SCREEN("/fxml/start-screen.fxml"),
    CHOOSE_GAME("/fxml/choose-game.fxml"),
    CHOOSE_WIZARD("/fxml/choose-wizard.fxml"),
    TEAM_LOBBY("/fxml/team-lobby-screen.fxml"),
    LOBBY("/fxml/lobby-screen.fxml"),
    CHOOSE_ASSISTANT("/fxml/choose-assistant.fxml"),
    CHARACTER_CARD("/fxml/character-card.fxml"),
    GAME_SCREEN("/fxml/game-screen.fxml"),
    SCHOOL_BOARD("/fxml/school-board.fxml"),
    ISLAND("/fxml/island.fxml"),
    ISLANDS("/fxml/islandsFlow.fxml"),
    CLOUD("/fxml/cloud.fxml"),
    PLAYER("/fxml/player.fxml"),

    CHOOSE_COLOR("/fxml/choose-color.fxml");

    private final String path;

    FXMLPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
