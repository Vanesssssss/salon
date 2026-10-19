package Controllers.Сlient;

import POJO.Clients;
import ServerWORK.ConnectDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AddInfoClientController {

    @FXML
    private VBox mainContainer;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField middleNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    // Стили для кнопки "Назад"
    private static final String HOVER_STYLE = "-fx-background-radius: 10; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #475569; " + // Темнее при наведении
            "-fx-text-fill: white;";

    private static final String DEFAULT_STYLE = "-fx-background-radius: 10; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: #64748b; " + // Исходный серый
            "-fx-text-fill: white;";

    @FXML
    void saveClientInfo() {
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();

        // Проверка на пустые поля
        if (lastName.isEmpty() || firstName.isEmpty() || phoneNumber.isEmpty()) {
            showAlert("Ошибка", "Поля Фамилия, Имя и Телефон должны быть заполнены.");
            return;
        }

        // Получение id текущего пользователя
        int userId = ConnectDB.id;

        if (userId == 0) {
            showAlert("Ошибка", "Не удалось получить данные пользователя.");
            return;
        }

        // Создание объекта Clients
        Clients client = new Clients();
        client.setUserId(userId);
        client.setLastName(lastName);
        client.setFirstName(firstName);
        client.setMiddleName(middleName.isEmpty() ? null : middleName);
        client.setPhoneNumber(phoneNumber);

        // Отправка данных на сервер
        try {
            ConnectDB.user.sendMessage("addClientInfo");
            ConnectDB.user.sendObject(client);
            System.out.println("Информация о клиенте отправлена");

            // Получение ответа от сервера
            String response = ConnectDB.user.readMessage();
            handleServerResponse(response);
        } catch (IOException ex) {
            System.out.println("Ошибка при чтении сообщения");
            showAlert("Ошибка", "Произошла ошибка при добавлении информации.");
        }
    }

    private void handleServerResponse(String response) {
        if (response.equals("Client information already exists")) {
            showAlert("Ошибка", "Информация уже существует.");
        } else if (response.equals("Client information added successfully")) {
            showAlert("Успех", "Информация успешно добавлена.");
        } else {
            showAlert("Ошибка", "Произошла ошибка при добавлении информации.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            // Загружаем FXML файл окна информации о клиенте
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/infoClient.fxml"));
            Parent root = loader.load();

            // Получаем текущую сцену и окно
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            Scene scene = new Scene(root, 1090, 720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить предыдущее окно.");
        }
    }

    @FXML
    private void handleMouseEnterExit(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(HOVER_STYLE);
        }
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(DEFAULT_STYLE);
        }
    }
}