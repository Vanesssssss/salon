package Controllers.Employee;

import POJO.Employee;
import ServerWORK.ConnectDB;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
import javafx.util.Duration;

import java.io.IOException;

public class EmplInfoController {

    @FXML
    private Label loginLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label experience_yearsLabel;
    @FXML
    private Label specializationLabel;
    @FXML
    private VBox mainContainer;
    @FXML
    private Button updInfoButton;
    @FXML
    private Button backButton;

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
        ConnectDB.user.sendMessage("viewEmployeeInfo");  // Отправляем команду на сервер
        ConnectDB.user.sendObject(userId);  // Отправляем ID пользователя

        // Получаем объект клиента с сервера
        Employee employee = (Employee) ConnectDB.user.readObject();

        if (employee != null) {
            // Обновляем UI элементами
            loginLabel.setText(ConnectDB.login);  // Логин, можно получить из ConnectDB, если он был сохранен
            lastNameLabel.setText(employee.getLastName());
            firstNameLabel.setText(employee.getFirstName());
            experience_yearsLabel.setText(String.valueOf(employee.getExperienceYears()));
            specializationLabel.setText(employee.getSpecialization());
        } else {
            showAlert("Ошибка", "Информация о работнике не найдена.");
        }

        // Анимация при загрузке
        animateElements();
    }

    // Анимация элементов при загрузке
    private void animateElements() {
        // Анимация для кнопки "Изменить информацию"
        ScaleTransition scaleUpdBtn = new ScaleTransition(Duration.millis(500), updInfoButton);
        scaleUpdBtn.setFromX(0.8);
        scaleUpdBtn.setFromY(0.8);
        scaleUpdBtn.setToX(1.0);
        scaleUpdBtn.setToY(1.0);
        scaleUpdBtn.setCycleCount(1);
        scaleUpdBtn.play();

        // Анимация для кнопки "Назад"
        FadeTransition fadeBackBtn = new FadeTransition(Duration.millis(800), backButton);
        fadeBackBtn.setFromValue(0.0);
        fadeBackBtn.setToValue(1.0);
        fadeBackBtn.play();

        // Анимация для заголовков
        for (Label label : new Label[]{loginLabel, lastNameLabel, firstNameLabel,
                experience_yearsLabel, specializationLabel}) {
            FadeTransition fade = new FadeTransition(Duration.millis(600), label);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.setDelay(Duration.millis(200));
            fade.play();
        }
    }

    @FXML
    private void handleUpdInfo() {
        // Анимация нажатия кнопки
        animateButtonClick(updInfoButton);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/emplUpdInfo.fxml"));
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
    private void handleBack() {
        // Анимация нажатия кнопки
        animateButtonClick(backButton);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Employee/menuEmployee.fxml"));
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

    // Анимация нажатия кнопки
    private void animateButtonClick(Button button) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(100), button);
        scaleIn.setToX(0.95);
        scaleIn.setToY(0.95);
        scaleIn.setCycleCount(1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(100), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);
        scaleOut.setCycleCount(1);

        scaleIn.setOnFinished(e -> scaleOut.play());
        scaleIn.play();
    }

    // Анимация при наведении на кнопку
    @FXML
    private void handleMouseEnterExit(MouseEvent event) {
        Button button = (Button) event.getSource();

        // Анимация увеличения
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.play();

        // Анимация поднятия
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(200), button);
        translateTransition.setToY(-3);
        translateTransition.play();

        // Стиль при наведении
        button.setStyle(HOVER_STYLE);
    }

    // Анимация при уходе с кнопки
    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();

        // Анимация уменьшения
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();

        // Анимация опускания
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(200), button);
        translateTransition.setToY(0);
        translateTransition.play();

        // Стиль при уходе
        button.setStyle(DEFAULT_STYLE);
    }

    private static final String HOVER_STYLE = "-fx-background-radius: 8; " +
            "-fx-font-family: 'Segoe UI Semibold'; " +
            "-fx-font-size: 16; " +
            "-fx-background-color: #1e40af; " + // Более темный оттенок
            "-fx-text-fill: white; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 3);";

    private static final String DEFAULT_STYLE = "-fx-background-radius: 8; " +
            "-fx-font-family: 'Segoe UI Semibold'; " +
            "-fx-font-size: 16; " +
            "-fx-min-width: 180; " +
            "-fx-min-height: 45; " +
            "-fx-background-color: #64748b; " +
            "-fx-text-fill: white;";

    // Метод для показа сообщений
    private void showAlert(String title, String content) {
        // Анимация появления alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);

        // Добавляем fade-in анимацию
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), alert.getDialogPane());
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        alert.showAndWait();
    }
}