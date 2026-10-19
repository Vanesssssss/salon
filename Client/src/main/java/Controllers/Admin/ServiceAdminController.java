package Controllers.Admin;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import Controllers.SceneLoader;

public class ServiceAdminController {

    @FXML
    private VBox mainContainer;
    @FXML
    private Button addService;
    @FXML
    private Button updService;
    @FXML
    private Button delService;
    @FXML
    private Button viewService;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Инициализация анимаций для кнопок
        initButtonAnimation(addService, "#8b5cf6", "#a78bfa");
        initButtonAnimation(updService, "#7c3aed", "#8b5cf6");
        initButtonAnimation(delService, "#6d28d9", "#7c3aed");
        initButtonAnimation(viewService, "#5b21b6", "#6d28d9");
    }

    /**
     * Инициализирует анимации для кнопки
     * @param button Кнопка для анимации
     * @param baseColor Основной цвет кнопки
     * @param hoverColor Цвет при наведении
     */
    private void initButtonAnimation(Button button, String baseColor, String hoverColor) {
        // Анимация масштабирования
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
        button.setScaleX(1.0);
        button.setScaleY(1.0);

        button.setOnMouseEntered(e -> {
            // Увеличение кнопки
            scaleTransition.stop();
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();

            // Усиление тени
            javafx.scene.effect.DropShadow shadow = (javafx.scene.effect.DropShadow) button.getEffect();
            if (shadow != null) {
                String colorStr = shadow.getColor().toString();
                colorStr = colorStr.replace("0x", "#").replace("0.3", "0.5");
                shadow.setColor(javafx.scene.paint.Color.web(colorStr));
            }

            // Изменение цвета
            button.setStyle(button.getStyle().replace(baseColor, hoverColor));
        });

        button.setOnMouseExited(e -> {
            // Возврат к исходному размеру
            scaleTransition.stop();
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();

            // Возврат тени
            javafx.scene.effect.DropShadow shadow = (javafx.scene.effect.DropShadow) button.getEffect();
            if (shadow != null) {
                String colorStr = shadow.getColor().toString();
                colorStr = colorStr.replace("0x", "#").replace("0.5", "0.3");
                shadow.setColor(javafx.scene.paint.Color.web(colorStr));
            }

            // Возврат цвета
            button.setStyle(button.getStyle().replace(hoverColor, baseColor));
        });
    }

    @FXML
    public void handleAddService() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Admin/addService.fxml", 1090, 720);
    }

    @FXML
    public void handleUpdService() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/updService.fxml", 1090, 720);
    }

    @FXML
    public void handleDelService() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Admin/delService.fxml", 1090, 720);
    }

    @FXML
    public void handleViewService() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/viewService.fxml", 1090, 720);
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
    public void handleBack() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Admin/menuAdmin.fxml", 1090, 720);
    }

    @FXML
    private void handleMouseEnterExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(BACK_HOVER_STYLE);

            // Анимация для кнопки "Назад"
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        }
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(BACK_DEFAULT_STYLE);

            // Анимация для кнопки "Назад"
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        }
    }
}