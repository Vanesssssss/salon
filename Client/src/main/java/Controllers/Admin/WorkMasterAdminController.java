package Controllers.Admin;

import POJO.AddMasterRequest;
import ServerWORK.ConnectDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class WorkMasterAdminController {

    @FXML
    private VBox mainContainer;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField experienceField;
    @FXML
    private TextField startWorkField;
    @FXML
    private TextField endWorkField;
    @FXML
    private TextField specializationField;
    @FXML
    private Button addMasterButton;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Инициализация обработчиков событий для кнопок
        addMasterButton.setOnMouseEntered(this::handleMouseEnter);
        addMasterButton.setOnMouseExited(this::handleMouseExit);
        backButton.setOnMouseEntered(this::handleMouseEnterExit);
        backButton.setOnMouseExited(this::handleMouseExitExit);
    }

    // Метод для отображения алертов
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void saveMasterInfo() {
        String login = loginField.getText();
        String password = passwordField.getText();
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();
        String experience = experienceField.getText();
        String startWork = startWorkField.getText();
        String endWork = endWorkField.getText();
        String specialization = specializationField.getText();

        if (login.isEmpty() || password.isEmpty() || lastName.isEmpty() || firstName.isEmpty()) {
            showAlert("Ошибка", "Все обязательные поля должны быть заполнены.");
            return;
        }

        AddMasterRequest request = new AddMasterRequest(
                login,
                hashPassword(password),
                lastName,
                firstName,
                experience,
                startWork,
                endWork,
                specialization
        );

        ConnectDB.user.sendMessage("addMaster");
        ConnectDB.user.sendObject(request);

        String response = "";
        try {
            response = ConnectDB.user.readMessage();
        } catch (IOException ex) {
            System.out.println("Ошибка при чтении сообщения");
        }

        if (response.equals("Master added successfully")) {
            showAlert("Успех", "Мастер успешно добавлен.");
            // Очистка полей после успешного добавления
            clearFields();
        } else {
            showAlert("Ошибка", "Произошла ошибка при добавлении мастера.");
        }
    }

    private void clearFields() {
        loginField.clear();
        passwordField.clear();
        lastNameField.clear();
        firstNameField.clear();
        experienceField.clear();
        startWorkField.clear();
        endWorkField.clear();
        specializationField.clear();
    }

    // Стили для кнопки "Добавить мастера"
    private static final String ADD_HOVER_STYLE = "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 220; " +
            "-fx-min-height: 45; " +
            "-fx-background-color: #0d9488; " + // Темный оттенок зеленого
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10;";

    private static final String ADD_DEFAULT_STYLE = "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 220; " +
            "-fx-min-height: 45; " +
            "-fx-background-color: #10b981; " + // Основной зеленый
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10;";

    // Стили для кнопки "Назад"
    private static final String BACK_HOVER_STYLE = "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 180; " +
            "-fx-min-height: 45; " +
            "-fx-background-color: #475569; " + // Темный оттенок серого
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10;";

    private static final String BACK_DEFAULT_STYLE = "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 180; " +
            "-fx-min-height: 45; " +
            "-fx-background-color: #64748b; " + // Основной серый
            "-fx-text-fill: white; " +
            "-fx-background-radius: 10;";

    @FXML
    private void handleMouseEnter(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == addMasterButton) {
            button.setStyle(ADD_HOVER_STYLE);
        } else if (button == backButton) {
            button.setStyle(BACK_HOVER_STYLE);
        }
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == addMasterButton) {
            button.setStyle(ADD_DEFAULT_STYLE);
        } else if (button == backButton) {
            button.setStyle(BACK_DEFAULT_STYLE);
        }
    }

    @FXML
    private void handleMouseEnterExit(MouseEvent event) {
        handleMouseEnter(event); // Используем общий обработчик
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        handleMouseExit(event); // Используем общий обработчик
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/menuAdmin.fxml"));
            Parent registerPage = loader.load();
            Scene registerScene = new Scene(registerPage);

            // Получаем текущее окно и устанавливаем новую сцену
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setWidth(1090);
            stage.setHeight(720);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());  // Генерация соли и хеширование пароля
    }
}