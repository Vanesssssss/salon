package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;


public class MainpageController {

    @FXML
    private VBox mainContainer;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    public void handleLogin() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/authorisation.fxml", 1090, 720);
    }

    @FXML
    public void handleRegister() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/register.fxml", 1090, 720);
    }

    @FXML
    private void handleMouseEntered(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == loginButton) {
            button.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-font-weight: bold; " +
                    "-fx-min-width: 180; -fx-min-height: 45; -fx-pref-width: 180; -fx-pref-height: 45; -fx-max-width: 180; -fx-max-height: 45; " +
                    "-fx-background-color: #0b5ed7; -fx-text-fill: white; -fx-background-radius: 8; " +
                    "-fx-effect: dropshadow(gaussian, rgba(11,94,215,0.5), 5, 0.5, 0, 0); -fx-cursor: hand;");
        } else if (button == registerButton) {
            button.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-font-weight: bold; " +
                    "-fx-min-width: 180; -fx-min-height: 45; -fx-pref-width: 180; -fx-pref-height: 45; -fx-max-width: 180; -fx-max-height: 45; " +
                    "-fx-background-color: #5c636a; -fx-text-fill: white; -fx-background-radius: 8; " +
                    "-fx-effect: dropshadow(gaussian, rgba(92,99,106,0.5), 5, 0.5, 0, 0); -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleMouseExited(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == loginButton) {
            button.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-font-weight: bold; " +
                    "-fx-min-width: 180; -fx-min-height: 45; -fx-pref-width: 180; -fx-pref-height: 45; -fx-max-width: 180; -fx-max-height: 45; " +
                    "-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8; " +
                    "-fx-effect: null; -fx-cursor: hand;");
        } else if (button == registerButton) {
            button.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-font-weight: bold; " +
                    "-fx-min-width: 180; -fx-min-height: 45; -fx-pref-width: 180; -fx-pref-height: 45; -fx-max-width: 180; -fx-max-height: 45; " +
                    "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 8; " +
                    "-fx-effect: null; -fx-cursor: hand;");
        }
    }
}