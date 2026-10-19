package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ServerWORK.ConnectDB;
import POJO.Auth;
import POJO.Users;

import java.io.IOException;

public class AuthorisationController {
    @FXML
    private VBox mainContainer;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    @FXML
    void handleLogin(ActionEvent event) throws IOException {
        ConnectDB.user.sendMessage("authorization");
        Auth auth = new Auth();
        auth.setLogin(usernameField.getText());
        auth.setPassword(passwordField.getText());
        ConnectDB.user.sendObject(auth);

        String mes = "";
        try {
            mes = ConnectDB.user.readMessage();
        } catch (IOException ex) {
            System.out.println("Error in reading");
        }

        if (mes.equals("There is no data!")) {
            showAlertWithNoLogin();
        } else {
            try {
                Users users = (Users) ConnectDB.user.readObject();
                ConnectDB.id = users.getId();
                ConnectDB.login = users.getLogin();
                ConnectDB.role = users.getRole();

                // Закрываем текущее окно авторизации
                ((Stage) loginButton.getScene().getWindow()).close();

                // Загружаем соответствующее окно в зависимости от роли
                switch (ConnectDB.role) {
                    case "client":
                        SceneLoader.loadScene(new Stage(), "/Client/windowClient.fxml", 1090, 720);
                        break;
                    case "employee":
                        SceneLoader.loadScene(new Stage(), "/Employee/menuEmployee.fxml", 1090, 720);
                        break;
                    default:
                        SceneLoader.loadScene(new Stage(), "/Admin/menuAdmin.fxml", 1090, 720);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlertWithError();
            }
        }
    }

    static public void showAlertWithNoLogin() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка авторизации");
        alert.setHeaderText("Неверные учетные данные");
        alert.setContentText("Пользователь с указанными логином и паролем не найден.");
        alert.showAndWait();
    }

    static public void showAlertWithError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка системы");
        alert.setHeaderText("Произошла ошибка");
        alert.setContentText("Невозможно выполнить вход. Пожалуйста, попробуйте позже.");
        alert.showAndWait();
    }

    // Обработчик для перехода на страницу регистрации
    @FXML
    private void handleRegisterRedirect(MouseEvent event) {
        SceneLoader.loadScene((Stage) ((Label) event.getSource()).getScene().getWindow(), "/register.fxml", 1090, 720);
    }

    // Эффект на кнопку при наведении
    @FXML
    private void handleMouseEnter() {
        loginButton.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-font-weight: bold; " +
                "-fx-min-width: 300; -fx-min-height: 45; -fx-pref-width: 300; -fx-pref-height: 45; -fx-max-width: 300; -fx-max-height: 45; " +
                "-fx-background-color: #0b5ed7; -fx-text-fill: white; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(11,94,215,0.5), 5, 0.5, 0, 0); -fx-cursor: hand;");
    }

    // Эффект на кнопку при выходе мыши
    @FXML
    private void handleMouseExit() {
        loginButton.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-font-weight: bold; " +
                "-fx-min-width: 300; -fx-min-height: 45; -fx-pref-width: 300; -fx-pref-height: 45; -fx-max-width: 300; -fx-max-height: 45; " +
                "-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8; " +
                "-fx-effect: null; -fx-cursor: hand;");
    }
}