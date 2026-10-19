package Controllers.Employee;

import POJO.Employee;
import ServerWORK.ConnectDB;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
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
import javafx.util.Duration;

import java.io.IOException;

public class EmplUpdInfoController {
    @FXML
    private VBox mainContainer;
    @FXML
    private TextField loginField1;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField middleNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private Button updButton;
    @FXML
    private Button backButton;

    @FXML
    void initialize() {
        // Анимация при загрузке
        animateElements();
    }

    // Анимация элементов при загрузке
    private void animateElements() {
        // Анимация для кнопки "Сохранить изменения"
        ScaleTransition scaleUpdBtn = new ScaleTransition(Duration.millis(500), updButton);
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

        // Анимация для полей ввода
        for (TextField field : new TextField[]{loginField1, lastNameField, firstNameField,
                middleNameField, phoneNumberField}) {
            FadeTransition fade = new FadeTransition(Duration.millis(600), field);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.setDelay(Duration.millis(100));
            fade.play();
        }
    }

    // Метод, который будет вызываться при нажатии на кнопку "Изменить"
    @FXML
    private void handleUpdateInfo(ActionEvent event) {
        // Анимация нажатия кнопки
        animateButtonClick(updButton);

        // Получаем данные из полей
        String newLogin = loginField1.getText();
        String lastName = lastNameField.getText();
        String firstName = firstNameField.getText();
        String experienceYearsStr = middleNameField.getText();
        String specialization = phoneNumberField.getText();

        // Проверка ввода для стажа
        if (!experienceYearsStr.matches("\\d+")) {
            showAlert("Ошибка", "Поле 'Стаж' должно содержать только цифры.");
            return;
        }

        int experienceYears = Integer.parseInt(experienceYearsStr);

        Employee employee = new Employee();
        int userId = ConnectDB.id;
        employee.setUserId(userId);
        employee.setLastName(lastName);
        employee.setFirstName(firstName);
        employee.setExperienceYears(experienceYears);
        employee.setSpecialization(specialization);

        try {
            ConnectDB.user.sendMessage("updaEmplInfo");
            ConnectDB.user.sendObject(newLogin);
            ConnectDB.user.sendObject(employee);

            String response = (String) ConnectDB.user.readObject();
            showAlert("Результат обновления", response);

            // Очистка полей после успешного обновления
            loginField1.clear();
            lastNameField.clear();
            firstNameField.clear();
            middleNameField.clear();
            phoneNumberField.clear();
        } catch (Exception e) {
            showAlert("Ошибка", "Произошла ошибка при обновлении данных.");
            e.printStackTrace();
        }
    }

    // Метод, который будет вызываться при нажатии на кнопку "Назад"
    @FXML
    private void handleBack() {
        // Анимация нажатия кнопки
        animateButtonClick(backButton);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Employee/emplInfo.fxml"));
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
        if (button == updButton) {
            button.setStyle(UPD_HOVER_STYLE);
        } else {
            button.setStyle(BACK_HOVER_STYLE);
        }
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
        if (button == updButton) {
            button.setStyle(UPD_DEFAULT_STYLE);
        } else {
            button.setStyle(BACK_DEFAULT_STYLE);
        }
    }

    // Стили для кнопок
    private static final String UPD_HOVER_STYLE = "-fx-background-radius: 8; " +
            "-fx-font-family: 'Segoe UI Semibold'; " +
            "-fx-font-size: 16; " +
            "-fx-min-width: 250; " +
            "-fx-min-height: 45; " +
            "-fx-background-color: #1e40af; " + // Более темный синий
            "-fx-text-fill: white; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 3);";

    private static final String UPD_DEFAULT_STYLE = "-fx-background-radius: 8; " +
            "-fx-font-family: 'Segoe UI Semibold'; " +
            "-fx-font-size: 16; " +
            "-fx-min-width: 250; " +
            "-fx-min-height: 45; " +
            "-fx-background-color: #0ea5e9; " +
            "-fx-text-fill: white;";

    private static final String BACK_HOVER_STYLE = "-fx-background-radius: 8; " +
            "-fx-font-family: 'Segoe UI Semibold'; " +
            "-fx-font-size: 16; " +
            "-fx-min-width: 180; " +
            "-fx-min-height: 45; " +
            "-fx-background-color: #475569; " + // Более темный серый
            "-fx-text-fill: white; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 3);";

    private static final String BACK_DEFAULT_STYLE = "-fx-background-radius: 8; " +
            "-fx-font-family: 'Segoe UI Semibold'; " +
            "-fx-font-size: 16; " +
            "-fx-min-width: 180; " +
            "-fx-min-height: 45; " +
            "-fx-background-color: #64748b; " +
            "-fx-text-fill: white;";

    // Метод для отображения сообщений
    private void showAlert(String title, String message) {
        // Анимация появления alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Добавляем fade-in анимацию
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), alert.getDialogPane());
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        alert.showAndWait();
    }
}
