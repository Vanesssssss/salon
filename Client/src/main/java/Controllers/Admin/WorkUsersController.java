package Controllers.Admin;

import Controllers.SceneLoader;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class WorkUsersController {

    @FXML
    private VBox mainContainer;
    @FXML
    private Button service;
    @FXML
    private Button schedule;
    @FXML
    private Button backButton;

    // Анимации для кнопок
    private ScaleTransition serviceScaleTransition;
    private ScaleTransition scheduleScaleTransition;
    private ScaleTransition backButtonScaleTransition;

    @FXML
    public void initialize() {
        // Инициализация анимаций
        initAnimations();
    }

    private void initAnimations() {
        // Анимация для кнопки "Пользователи"
        serviceScaleTransition = new ScaleTransition(Duration.millis(150), service);
        service.setScaleX(1.0);
        service.setScaleY(1.0);

        // Анимация для кнопки "Мастера"
        scheduleScaleTransition = new ScaleTransition(Duration.millis(150), schedule);
        schedule.setScaleX(1.0);
        schedule.setScaleY(1.0);

        // Анимация для кнопки "Назад"
        backButtonScaleTransition = new ScaleTransition(Duration.millis(150), backButton);
        backButton.setScaleX(1.0);
        backButton.setScaleY(1.0);
    }

    @FXML
    public void handleUsers() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/workUsersAdmin.fxml", 1090, 720);
    }

    @FXML
    public void handleMaster() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/workMasterAdmin.fxml"));
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
    private void handleMouseEnter(MouseEvent event) {
        Button button = (Button) event.getSource();

        // Определяем цвет при наведении в зависимости от кнопки
        String hoverColor = "";
        if (button == service) {
            hoverColor = "#4338ca"; // Более темный оттенок фиолетового
        } else if (button == schedule) {
            hoverColor = "#6d28d9"; // Более темный оттенок пурпурного
        }

        if (!hoverColor.isEmpty()) {
            button.setStyle(button.getStyle().replaceFirst("#[0-9a-fA-F]{6,8}", hoverColor));

            // Анимация для кнопки
            ScaleTransition st = (button == service) ? serviceScaleTransition : scheduleScaleTransition;
            st.stop();
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        }
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        Button button = (Button) event.getSource();

        // Определяем исходный цвет в зависимости от кнопки
        String defaultColor = "";
        if (button == service) {
            defaultColor = "#4f46e5"; // Исходный фиолетовый
        } else if (button == schedule) {
            defaultColor = "#7c3aed"; // Исходный пурпурный
        }

        if (!defaultColor.isEmpty()) {
            button.setStyle(button.getStyle().replaceFirst("#[0-9a-fA-F]{6,8}", defaultColor));

            // Анимация для кнопки
            ScaleTransition st = (button == service) ? serviceScaleTransition : scheduleScaleTransition;
            st.stop();
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        }
    }

    // Стили для кнопки "Назад"
    private static final String BACK_HOVER_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 45; " +
                    "-fx-background-color: #475569; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10;";

    private static final String BACK_DEFAULT_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 45; " +
                    "-fx-background-color: #64748b; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10;";

    @FXML
    private void handleMouseEnterExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(BACK_HOVER_STYLE);

            // Анимация для кнопки "Назад"
            backButtonScaleTransition.stop();
            backButtonScaleTransition.setToX(1.05);
            backButtonScaleTransition.setToY(1.05);
            backButtonScaleTransition.play();
        }
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(BACK_DEFAULT_STYLE);

            // Анимация для кнопки "Назад"
            backButtonScaleTransition.stop();
            backButtonScaleTransition.setToX(1.0);
            backButtonScaleTransition.setToY(1.0);
            backButtonScaleTransition.play();
        }
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
}