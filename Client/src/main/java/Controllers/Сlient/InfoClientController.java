package Controllers.Сlient;

import POJO.Clients;
import ServerWORK.ConnectDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class InfoClientController {

    @FXML
    private Label loginLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label middleNameLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private VBox mainContainer;

    // Метод для получения информации о клиенте
    @FXML
    void initialize() {
        // Получение данных клиента по userId
        int userId = ConnectDB.id;  // Получаем ID текущего пользователя из ConnectDB
        if (userId == 0) {
            showAlert("Ошибка", "Не удалось получить данные пользователя.");
            return;
        }

        // Отправляем запрос на сервер для получения информации о клиенте
        ConnectDB.user.sendMessage("viewClientInfo");  // Отправляем команду на сервер
        ConnectDB.user.sendObject(userId);  // Отправляем ID пользователя

        // Получаем объект клиента с сервера
        Clients client = (Clients) ConnectDB.user.readObject();

        if (client != null) {
            // Обновляем UI элементами
            loginLabel.setText(ConnectDB.login);  // Логин, можно получить из ConnectDB, если он был сохранен
            lastNameLabel.setText(client.getLastName());
            firstNameLabel.setText(client.getFirstName());
            middleNameLabel.setText(client.getMiddleName() != null ? client.getMiddleName() : "-");
            phoneNumberLabel.setText(client.getPhoneNumber());
        } else {
            showAlert("Ошибка", "Информация о клиенте не найдена.");
        }
    }
    @FXML
    private void handleAddInfo()
    {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/AddInfoClient.fxml"));
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
    @FXML
    private void handleUpdInfo()
    {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updInfoClient.fxml"));
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
    @FXML
    private void handleBack()
    {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/windowClient.fxml"));
            Parent registerPage = loader.load();
            Scene registerScene = new Scene(registerPage);

            // Получаем текущее окно и устанавливаем новую сцену
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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
    private void handleMouseEnterExit(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(HOVER_STYLE); // Устанавливаем стиль при наведении
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(DEFAULT_STYLE); // Возвращаем исходный стиль
    }
    // Метод для показа сообщений
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
