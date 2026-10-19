package Controllers.Admin;

import Controllers.SceneLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class MenuAdminController {
    @FXML
    private Button service;
    @FXML
    private Button reviews;
    @FXML
    private Button user;
    @FXML
    private Button schedule;
    @FXML
    private Button backButton;
    @FXML
    private Button order;

    // Стили для основных кнопок
    private static final String DEFAULT_SERVICE_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #4f46e5; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10;";

    private static final String HOVER_SERVICE_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #4338ca; " + // Более темный оттенок
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(79,70,229,0.4), 10, 0, 0, 4);";

    private static final String DEFAULT_SCHEDULE_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #7c3aed; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10;";

    private static final String HOVER_SCHEDULE_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #6d28d9; " + // Более темный оттенок
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(124,58,237,0.4), 10, 0, 0, 4);";

    private static final String DEFAULT_USER_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #0ea5e9; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10;";

    private static final String HOVER_USER_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #0284c7; " + // Более темный оттенок
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(14,165,233,0.4), 10, 0, 0, 4);";

    private static final String DEFAULT_REVIEWS_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #8b5cf6; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10;";

    private static final String HOVER_REVIEWS_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #7c3aed; " + // Более темный оттенок
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(139,92,246,0.4), 10, 0, 0, 4);";

    private static final String DEFAULT_ORDER_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 380; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #6366f1; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10;";

    private static final String HOVER_ORDER_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 380; " +
                    "-fx-min-height: 50; " +
                    "-fx-background-color: #4f46e5; " + // Более темный оттенок
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.4), 10, 0, 0, 4);";

    // Стили для кнопки "Назад"
    private static final String DEFAULT_BACK_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 45; " +
                    "-fx-background-color: #64748b; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(100,116,139,0.3), 6, 0, 0, 3);";

    private static final String HOVER_BACK_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 45; " +
                    "-fx-background-color: #475569; " + // Более темный серый
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(71,85,105,0.4), 8, 0, 0, 4);";

    @FXML
    public void handleService() {
        SceneLoader.loadScene((Stage) service.getScene().getWindow(), "/Admin/serviceAdmin.fxml", 1090, 720);
    }

    @FXML
    public void handleOrder() {
        SceneLoader.loadScene((Stage) order.getScene().getWindow(), "/Admin/adminviewOrder.fxml", 1090, 720);
    }

    @FXML
    public void handleSchedule() {
        loadScene("/Admin/sheduleMenu.fxml");
    }

    @FXML
    public void handleUser() {
        SceneLoader.loadScene((Stage) user.getScene().getWindow(), "/workUsers.fxml", 1090, 720);
    }

    @FXML
    public void handleReviews() {
        loadScene("/Admin/reviewAdmin.fxml");
    }

    @FXML
    private void handleMouseEnter(MouseEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();

        switch (id) {
            case "service":
                button.setStyle(HOVER_SERVICE_STYLE);
                break;
            case "schedule":
                button.setStyle(HOVER_SCHEDULE_STYLE);
                break;
            case "user":
                button.setStyle(HOVER_USER_STYLE);
                break;
            case "reviews":
                button.setStyle(HOVER_REVIEWS_STYLE);
                break;
            case "order":
                button.setStyle(HOVER_ORDER_STYLE);
                break;
        }
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();

        switch (id) {
            case "service":
                button.setStyle(DEFAULT_SERVICE_STYLE);
                break;
            case "schedule":
                button.setStyle(DEFAULT_SCHEDULE_STYLE);
                break;
            case "user":
                button.setStyle(DEFAULT_USER_STYLE);
                break;
            case "reviews":
                button.setStyle(DEFAULT_REVIEWS_STYLE);
                break;
            case "order":
                button.setStyle(DEFAULT_ORDER_STYLE);
                break;
        }
    }

    @FXML
    private void handleMouseEnterExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(HOVER_BACK_STYLE);
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(DEFAULT_BACK_STYLE);
    }

    @FXML
    private void handleBack() {
        loadScene("/mainpage.fxml");
    }

    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(1090);
            stage.setHeight(720);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}