package Controllers.Сlient;

import POJO.Review;
import ServerWORK.ConnectDB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import Controllers.SceneLoader;

import java.io.IOException;

public class AddReviewClientController {

    @FXML
    private TextField ratingField;
    @FXML
    private TextArea reviewTextArea;
    @FXML
    private Button saveReviewButton;
    @FXML
    private Button backButton;

    // Стили для эффектов при наведении
    private static final String HOVER_STYLE = "-fx-background-color: #475569;";
    private static final String DEFAULT_STYLE_BACK = "-fx-background-color: #64748b;";
    private static final String DEFAULT_STYLE_SAVE = "-fx-background-color: #f97316;";

    @FXML
    public void initialize() {
        // Установка стилей для кнопок
        backButton.setStyle(DEFAULT_STYLE_BACK);
        saveReviewButton.setStyle(DEFAULT_STYLE_SAVE);
    }

    @FXML
    void saveReview() {
        String reviewText = reviewTextArea.getText();
        String ratingStr = ratingField.getText();

        // Проверка на пустые поля
        if (reviewText.isEmpty() || ratingStr.isEmpty()) {
            showAlert("Ошибка", "Все обязательные поля должны быть заполнены.");
            return;
        }

        // Проверка корректности рейтинга
        int rating;
        try {
            rating = Integer.parseInt(ratingStr);
            if (rating < 1 || rating > 5) {
                showAlert("Ошибка", "Рейтинг должен быть от 1 до 5.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Неверный формат рейтинга.");
            return;
        }

        // Получение id текущего пользователя
        int userId = ConnectDB.id;

        if (userId == 0) {
            showAlert("Ошибка", "Не удалось получить данные пользователя.");
            return;
        }

        // Создание объекта Review
        Review review = new Review();
        review.setUsersId(userId);
        review.setReviewText(reviewText);
        review.setRating(rating);

        // Отправка данных на сервер
        ConnectDB.user.sendMessage("addReview");
        ConnectDB.user.sendObject(review);
        System.out.println("Отзыв отправлен");

        // Получение ответа от сервера
        String response = "";
        try {
            response = ConnectDB.user.readMessage();
        } catch (IOException ex) {
            System.out.println("Ошибка при чтении сообщения");
        }

        // Обработка ответа от сервера
        if (response.equals("Review already exists")) {
            showAlert("Ошибка", "Отзыв уже существует.");
        } else if (response.equals("Review added successfully")) {
            showAlert("Успех", "Отзыв успешно добавлен.");
            // Очистка полей после успешного добавления
            reviewTextArea.clear();
            ratingField.clear();
        } else {
            showAlert("Ошибка", "Произошла ошибка при добавлении отзыва.");
        }
    }

    // Метод для показа сообщений
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneLoader.loadScene(stage, "/reviewCL.fxml", 1090, 720);
    }

    @FXML
    private void handleMouseEnterExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(HOVER_STYLE);
        }
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(DEFAULT_STYLE_BACK);
        }
    }
}