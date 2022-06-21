package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.client.gui.audio.MuteToggle;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * This class is the controller of the start screen.
 */
public class StartScreenController implements GUIController, MuteToggle {

    /**
     * hostname of the server.
     */
    static String hostname = "";

    /**
     * port of the server.
     */
    static String port = "";

    /**
     * username of the player.
     */
    static String nickname = "";

    /**
     * {@link GUI} instance.
     */
    private GUI gui;

    @FXML
    public AnchorPane root;
    @FXML
    private ImageView imgBackground;
    @FXML
    private ImageView imgViewMute;
    @FXML
    private ImageView imgTitle;
    @FXML
    private GridPane grpConnect;
    @FXML
    private TextField txtFieldServer;
    @FXML
    private TextField txtFieldPort;
    @FXML
    private TextField txtFieldNickname;
    @FXML
    private Button btnStart;
    @FXML
    private ImageView imgStart;
    @FXML
    private Button btnPlay;
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

    /**
     * This method initialize the view.
     */
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

        GUIUtils.changeImageHoverButton(btnStart, imgStart, ResourceLoader.loadImage(ImagePath.START), ResourceLoader.loadImage(ImagePath.START_HIGHLIGHTED));
        updateImageViewMute(imgViewMute);

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
        if (stage.getScene() != null)
            stage.getScene().setRoot(new Region());
        stage.setScene(new Scene(root));
        stage.setMinHeight(800.0);
        stage.setMinWidth(1200.0);
        stage.setHeight(800.0);
        stage.setWidth(1200.0);
        stage.setResizable(true);
    }

    /**
     * This method check the field in the start screen form.
     *
     * @return true if the form is valid, false otherwise.
     */
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

    /**
     * This method hide/show the play button.
     *
     * @param hide true if the button should be hidden, false otherwise.
     */
    private void hideCenter(boolean hide) {
        btnPlay.setVisible(hide);
        btnPlay.setDisable(!hide);
        grpConnect.setDisable(hide);
        grpConnect.setVisible(!hide);
    }

    /**
     * This method disable/enable the form.
     *
     * @param disable true if the form should be disabled, false otherwise.
     */
    public void disableCenter(boolean disable) {
        grpConnect.setDisable(disable);
    }

    /**
     * This method is used to show the form.
     */
    @FXML
    public void handlePlayButton() {
        hideCenter(false);
    }

    /**
     * This method handle the start button which is used to send to the GUI the information contained in the form.
     */
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

    /**
     * handles the mute toggle.
     */
    @FXML
    @Override
    public void handleMuteButton() {
        toggleMute(imgViewMute);
    }
}
