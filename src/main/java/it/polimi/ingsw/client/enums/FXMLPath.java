package it.polimi.ingsw.client.enums;

public enum FXMLPath {
    START_SCREEN("/fxml/start-screen.fxml"),
    CHOOSE_GAME("/fxml/choose-game.fxml"),
    CHOOSE_WIZARD("/fxml/choose-wizard.fxml"),
    TEAM_LOBBY("/fxml/team-lobby-screen.fxml"),
    LOBBY("/fxml/lobby-screen.fxml"),
    CHOOSE_ASSISTANT("/fxml/team-lobby-screen.fxml");
    private final String path;

    FXMLPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
