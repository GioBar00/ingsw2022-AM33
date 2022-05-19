package it.polimi.ingsw.client.enums;

public enum SceneFXMLPath {
    START_SCREEN("/fxml/start-screen.fxml"),
    CHOOSE_GAME("/fxml/choose-game.fxml"),
    CHOOSE_WIZARD("/fxml/choose-wizard.fxml"),

    TEAM_LOBBY("/fxml/lobby-screen.fxml");
    private final String path;

    SceneFXMLPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
