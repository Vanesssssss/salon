package Controllers.Employee;

import Controllers.SceneLoader;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;

public class MenuEmployeeController {
    @FXML
    private VBox mainContainer;
    @FXML
    private Button personalInfo;
    @FXML
    private Button objects;
    @FXML
    private Button statistics;
    @FXML
    private Button backButton;

    @FXML
    private Button reviewsButton; // Новая кнопка

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
    public void initialize() {
        // Инициализация анимаций для кнопок
        initButtonAnimation(personalInfo, "#0ea5e9", "#38bdf8");
        initButtonAnimation(objects, "#8b5cf6", "#a78bfa");
        initButtonAnimation(statistics, "#10b981", "#34d399");
        initButtonAnimation(reviewsButton, "#f97316", "#fb923c"); // Анимация для новой кнопки
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
            DropShadow shadow = (DropShadow) button.getEffect();
            if (shadow != null) {
                shadow.setColor(javafx.scene.paint.Color.web(shadow.getColor().toString().replace("0x", "#").replace("0x", "#").replace("0.3", "0.5")));
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
            DropShadow shadow = (DropShadow) button.getEffect();
            if (shadow != null) {
                shadow.setColor(javafx.scene.paint.Color.web(shadow.getColor().toString().replace("0x", "#").replace("0x", "#").replace("0.5", "0.3")));
            }

            // Возврат цвета
            button.setStyle(button.getStyle().replace(hoverColor, baseColor));
        });
    }

    @FXML
    public void handlePersonalInfo() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Employee/emplInfo.fxml", 1090, 720);
    }

    @FXML
    public void handleObjects() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Employee/orderEmployee.fxml", 1090, 720);
    }

    @FXML
    public void handleStatistics() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Employee/staticsEmployee.fxml", 1090, 720);
    }

    @FXML
    private void handleBack() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/mainpage.fxml", 1090, 720);
    }

    @FXML
    public void handleReviews() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/reviewCL.fxml", 1090, 720);
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