package Controllers.Admin;

import Controllers.SceneLoader;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SheduleMenuController {

    @FXML
    private VBox mainContainer;
    @FXML
    private Button AddSerMas;
    @FXML
    private Button schedule;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Инициализация анимаций для кнопок
        initButtonAnimation(AddSerMas, "#4f46e5", "#6366f1");
        initButtonAnimation(schedule, "#7c3aed", "#8b5cf6");
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
    public void handleAddSerMas() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Admin/addSerMas.fxml", 1090, 720);
    }

    @FXML
    public void handleSchedule() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Admin/addShedule.fxml", 1090, 720);
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
    private void handleBack() {
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