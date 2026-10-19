package Controllers;

import java.io.IOException;

import POJO.Users;
import ServerWORK.ConnectDB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button registerButton;

    @FXML
    void initialize() {
        registerButton.setOnAction(event -> registerUser());
    }

    @FXML
    void registerUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Проверка на совпадение паролей
        if (!password.equals(confirmPassword)) {
            showAlertWithPasswordMismatch();
            return;
        }

        // Проверка на минимальную длину пароля
        if (password.length() < 8) {
            showAlertWithShortPassword();
            return;
        }

        // Проверка на пустое имя пользователя
        if (username.isEmpty()) {
            showAlertWithEmptyUsername();
            return;
        }

        Users users = new Users();
        users.setLogin(username);
        users.setPassword(hashPassword(password)); // Хешируем пароль
        users.setRole("client");

        // Отправка данных на сервер
        ConnectDB.user.sendMessage("registrationClient");
        ConnectDB.user.sendObject(users);
        System.out.println("Запись отправлена");

        // Получение ответа от сервера
        String mes = "";
        try {
            mes = ConnectDB.user.readMessage();
        } catch (IOException ex) {
            System.out.println("Error in reading");
        }

        // Обработка ответа от сервера
        if (mes.equals("This user already exists")) {
            showAlertWithExistLogin();
        } else if (mes.equals("OK")) {
            SceneLoader.loadScene((Stage) registerButton.getScene().getWindow(), "/authorisation.fxml", 1090, 720);
        } else {
            showAlertWithError();
        }
    }

    // Метод для показа ошибки, если логин уже существует
    static public void showAlertWithExistLogin() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка регистрации");
        alert.setContentText("Пользователь с таким логином уже существует.");
        alert.showAndWait();
    }

    // Метод для показа ошибки при несоответствии паролей
    static public void showAlertWithPasswordMismatch() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Несовпадение паролей");
        alert.setContentText("Введенные пароли не совпадают. Пожалуйста, проверьте ввод.");
        alert.showAndWait();
    }

    // Метод для показа ошибки, если пароль слишком короткий
    static public void showAlertWithShortPassword() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ненадежный пароль");
        alert.setContentText("Пароль должен содержать не менее 8 символов для обеспечения безопасности.");
        alert.showAndWait();
    }

    // Метод для показа ошибки, если имя пользователя пустое
    static public void showAlertWithEmptyUsername() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Пустое поле логина");
        alert.setContentText("Поле логина не может быть пустым. Введите имя пользователя.");
        alert.showAndWait();
    }

    // Метод для показа ошибки, если произошла ошибка при регистрации
    static public void showAlertWithError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка системы");
        alert.setContentText("Произошла непредвиденная ошибка при регистрации. Пожалуйста, попробуйте позже.");
        alert.showAndWait();
    }

    // Метод для хеширования пароля с использованием BCrypt
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Обработчик для перехода на страницу входа
    @FXML
    private void handleLoginRedirect(MouseEvent event) {
        Label sourceLabel = (Label) event.getSource();
        SceneLoader.loadScene((Stage) sourceLabel.getScene().getWindow(), "/authorisation.fxml", 1090, 720);
    }

    // Эффект на кнопку при наведении
    @FXML
    private void handleMouseEnter() {
        registerButton.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-font-weight: bold; " +
                "-fx-min-width: 300; -fx-min-height: 45; -fx-pref-width: 300; -fx-pref-height: 45; -fx-max-width: 300; -fx-max-height: 45; " +
                "-fx-background-color: #0b5ed7; -fx-text-fill: white; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(11,94,215,0.5), 5, 0.5, 0, 0); -fx-cursor: hand;");
    }

    // Эффект на кнопку при выходе мыши
    @FXML
    private void handleMouseExit() {
        registerButton.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-font-weight: bold; " +
                "-fx-min-width: 300; -fx-min-height: 45; -fx-pref-width: 300; -fx-pref-height: 45; -fx-max-width: 300; -fx-max-height: 45; " +
                "-fx-background-color: #0d6efd; -fx-text-fill: white; -fx-background-radius: 8; " +
                "-fx-effect: null; -fx-cursor: hand;");
    }
}