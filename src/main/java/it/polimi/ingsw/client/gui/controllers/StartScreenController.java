package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class StartScreenController implements GUIController {

    static String hostname = "";
    static String port = "";
    static String nickname = "";

    private GUI gui;
    @FXML
    public AnchorPane root;
    @FXML
    public ImageView imgBackground;
    @FXML
    public ImageView imgTitle;
    @FXML
    public GridPane grpConnect;
    @FXML
    public TextField txtFieldServer;
    @FXML
    public TextField txtFieldPort;
    @FXML
    public TextField txtFieldNickname;
    @FXML
    public Button btnStart;
    @FXML
    public ImageView imgStart;
    @FXML
    public Button btnPlay;
    @FXML
    public Button btnMute;

    /**
     * This method is used to set the GUI of the controller.
     *
     * @param gui the GUI of the controller.
     */
    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void init() {

        txtFieldServer.setText(hostname);
        txtFieldPort.setText(port);
        txtFieldNickname.setText(nickname);

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            imgBackground.setFitHeight(newVal.doubleValue());
            imgTitle.setFitWidth((root.getWidth() / 3 + root.getHeight() / 1.5) / 2);
        });

        root.widthProperty().addListener((obs, oldVal, newVal) -> imgTitle.setFitWidth((root.getWidth() / 3 + root.getHeight() / 1.5) / 2));

        txtFieldPort.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtFieldPort.setText(newValue.replaceAll("\\D", ""));
            }
        });

        txtFieldNickname.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() > 20)
                txtFieldNickname.setText(oldValue);
        });

        btnStart.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> imgStart.setImage(ResourceLoader.loadImage(ImagePath.START_HIGHLIGHTED)));
        btnStart.addEventHandler(MouseEvent.MOUSE_EXITED, e -> imgStart.setImage(ResourceLoader.loadImage(ImagePath.START)));

        hideCenter(true);
    }

    /**
     * This method is used to set the parent of the controller.
     *
     * @param root the parent of the controller.
     */
    @Override
    public void setRootPane(Pane root) {
        // root already set
    }

    /**
     * This method returns the node of the controller.
     *
     * @return the node of the controller.
     */
    @Override
    public Pane getRootPane() {
        return root;
    }

    /**
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    @Override
    public void loadScene(Stage stage) {
        stage.setScene(new Scene(getRootPane()));
        stage.setMinHeight(800.0);
        stage.setMinWidth(1200.0);
        stage.setResizable(true);
    }

    private boolean checkFields() {
        if (!txtFieldServer.getText().isEmpty() && !Client.validateServerString(txtFieldServer.getText())) {
            System.out.println("Server not valid");
            return false;
        }
        if (!txtFieldPort.getText().isEmpty() && Integer.parseInt(txtFieldPort.getText()) > 65535) {
            System.out.println("Port not valid");
            return false;
        }
        if (txtFieldNickname.getText().isEmpty()) {
            System.out.println("Nickname not valid");
            return false;
        }

        return true;
    }

    private void hideCenter(boolean hide) {
        btnPlay.setVisible(hide);
        btnPlay.setDisable(!hide);
        grpConnect.setDisable(hide);
        grpConnect.setVisible(!hide);
    }

    public void disableCenter(boolean disable) {
        grpConnect.setDisable(disable);
    }

    @FXML
    public void handlePlayButton() {
        hideCenter(false);
    }

    @FXML
    public void handleStartButton() {
        disableCenter(true);
        if (checkFields()) {
            String server = txtFieldServer.getText().isEmpty() ? "localhost" : txtFieldServer.getText();
            int port = txtFieldPort.getText().isEmpty() ? 1234 : Integer.parseInt(txtFieldPort.getText());
            String nickname = txtFieldNickname.getText();
            if (gui.getClient().setServerAddress(server, port)) {
                StartScreenController.hostname = txtFieldServer.getText();
                StartScreenController.port = txtFieldPort.getText();
                StartScreenController.nickname = nickname;
                new Thread(() -> {
                    gui.setNickname(nickname);
                    gui.getClient().setNickname(nickname);
                    gui.getClient().sendLogin();
                }).start();
            } else
                System.out.println("Server address and port not valid");

        }
    }

    @FXML
    public void handleMuteButton() {
        System.out.println("Mute");
    }
}
