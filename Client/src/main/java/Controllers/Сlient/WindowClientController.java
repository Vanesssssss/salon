package Controllers.Сlient;

import Controllers.SceneLoader;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import ServerWORK.ConnectDB;
import java.io.IOException;

public class WindowClientController {

    @FXML
    private VBox mainContainer;
    @FXML
    private Button infoButton;
    @FXML
    private Button serviceButton;
    @FXML
    private Button masterButton;
    @FXML
    private Button reviewsButton;
    @FXML
    private Button orderButton;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Инициализация анимаций для кнопок
        initButtonAnimation(infoButton, "#f59e0b", "#fbbf24");
        initButtonAnimation(serviceButton, "#fbbf24", "#fcd34d");
        initButtonAnimation(masterButton, "#f59e0b", "#fbbf24");
        initButtonAnimation(reviewsButton, "#fbbf24", "#fcd34d");
        initButtonAnimation(orderButton, "#d97706", "#f59e0b");
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
    public void handleInfo() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Client/infoClient.fxml", 1090, 720);
    }

    @FXML
    public void handleService() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/viewServiceCL.fxml", 1090, 720);
    }

    @FXML
    public void handleMaster() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Client/clMaster.fxml", 1090, 720);
    }

    @FXML
    public void handleReviews() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Client/addServiceCl.fxml", 1090, 720);
    }

    @FXML
    public void handleOrder() {
        // Отправляем запрос на сервер для проверки клиента
        ConnectDB.user.sendMessage("checkClientExists");
        ConnectDB.user.sendObject(ConnectDB.id); // Используем ID, хранящийся в ConnectDB

        String response = "";
        try {
            response = ConnectDB.user.readMessage(); // Читаем ответ от сервера
        } catch (IOException ex) {
            System.out.println("Ошибка при чтении ответа от сервера");
        }

        // Обработка ответа
        if (response.equals("Client exists")) {
            // Если клиент найден, переходим на сцену Order
            SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/Client/addClientOrder.fxml", 1090, 720);
        } else if (response.equals("Client does not exist")) {
            // Если клиента нет, показываем сообщение об ошибке
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Клиент не найден");
            alert.setContentText("Пожалуйста, проверьте данные клиента.");
            alert.showAndWait();
        } else {
            // Обработка неожиданного ответа
            System.out.println("Неожиданный ответ: " + response);
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
    private void handleBack() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(), "/mainpage.fxml", 1090, 720);
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