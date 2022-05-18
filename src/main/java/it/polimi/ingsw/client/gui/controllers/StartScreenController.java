package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;

public class StartScreenController implements GUIController {
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

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            imgBackground.setFitHeight(newVal.doubleValue());
            imgTitle.setFitWidth((root.getWidth() / 3 + root.getHeight() / 1.5) / 2);
        });

        root.widthProperty().addListener((obs, oldVal, newVal) -> imgTitle.setFitWidth((root.getWidth() / 3 + root.getHeight() / 1.5) / 2));

        grpConnect.setDisable(true);
        grpConnect.setVisible(false);

        txtFieldPort.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtFieldPort.setText(newValue.replaceAll("\\D", ""));
            }
        });

        txtFieldNickname.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() > 20)
                txtFieldNickname.setText(oldValue);
        });

        btnStart.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> imgStart.setImage(GUI.imagesByPath.get(ImagePath.START_HIGHLIGHTED)));
        btnStart.addEventHandler(MouseEvent.MOUSE_EXITED, e -> imgStart.setImage(GUI.imagesByPath.get(ImagePath.START)));

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

    @FXML
    public void handlePlayButton(ActionEvent actionEvent) {
        btnPlay.setVisible(false);
        btnPlay.setDisable(true);
        grpConnect.setDisable(false);
        grpConnect.setVisible(true);
    }

    @FXML
    public void handleStartButton(ActionEvent actionEvent) {
        if (checkFields()) {
            String server = txtFieldServer.getText().isEmpty() ? "localhost" : txtFieldServer.getText();
            int port = txtFieldPort.getText().isEmpty() ? 1234 : Integer.parseInt(txtFieldPort.getText());
            String nickname = txtFieldNickname.getText();
            gui.getClient().setServerAddress(server);
            gui.getClient().setServerPort(String.valueOf(port));
            gui.getClient().setNickname(nickname);
            gui.getClient().sendLogin();
        }
    }




}
