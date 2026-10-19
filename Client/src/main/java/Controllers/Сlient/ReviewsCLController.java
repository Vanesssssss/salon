package Controllers.Сlient;

import POJO.Review;
import ServerWORK.ConnectDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewsCLController {

    @FXML
    private VBox mainContainer;
    @FXML
    private VBox reviewsListContainer;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private Button sortButton;
    @FXML
    private Button addReviewButton;
    @FXML
    private Button backButton;

    private List<Review> reviews;

    // Стили для эффектов при наведении
    private static final String HOVER_STYLE = "-fx-background-color: #475569;";
    private static final String DEFAULT_STYLE_BACK = "-fx-background-color: #64748b;";
    private static final String DEFAULT_STYLE_SORT = "-fx-background-color: #8b5cf6;";
    private static final String DEFAULT_STYLE_ADD = "-fx-background-color: #f97316;";

    @FXML
    private void initialize() {
        // Установка стилей для кнопок
        backButton.setStyle(DEFAULT_STYLE_BACK);
        sortButton.setStyle(DEFAULT_STYLE_SORT);
        addReviewButton.setStyle(DEFAULT_STYLE_ADD);

        // Добавление эффектов теней
        addShadowEffect(backButton, "#64748b");
        addShadowEffect(sortButton, "#8b5cf6");
        addShadowEffect(addReviewButton, "#f97316");

        sortComboBox.setValue("От 1 до 5");
        loadReviews();
    }

    // Метод для добавления эффекта тени к кнопкам
    private void addShadowEffect(Button button, String color) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(color + "80")); // 50% прозрачность
        shadow.setRadius(6);
        shadow.setOffsetX(0);
        shadow.setOffsetY(3);
        button.setEffect(shadow);
    }

    private void loadReviews() {
        ConnectDB.user.sendMessage("viewReviews");
        reviews = (List<Review>) ConnectDB.user.readObject();

        if (reviews != null) {
            reviewsListContainer.getChildren().clear();
            for (Review review : reviews) {
                VBox reviewCard = createReviewCard(review);
                reviewsListContainer.getChildren().add(reviewCard);
            }
        }
    }

    // Создание карточки отзыва в современном стиле
    private VBox createReviewCard(Review review) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 15; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 3);");
        card.setMaxWidth(600);
        card.setMinWidth(600);

        // Получаем имя пользователя по ID
        String userName = getUserNameById(review.getUsersId());

        // Заголовок с информацией о пользователе
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label userLabel = new Label(userName != null ? userName : "Анонимный пользователь");
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        userLabel.setTextFill(Color.web("#1e293b"));

        // Форматируем дату с проверкой на null
        String formattedDate = "Дата не указана";
        if (review.getCreatedAt() != null) {
            formattedDate = review.getCreatedAt().format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            );
        }

        Label dateLabel = new Label(formattedDate);
        dateLabel.setFont(Font.font("Segoe UI", 12));
        dateLabel.setTextFill(Color.web("#64748b"));

        // Разделитель
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(userLabel, spacer, dateLabel);

        // Отображение рейтинга звездами
        HBox ratingBox = new HBox(5);
        for (int i = 1; i <= 5; i++) {
            String starType = i <= review.getRating() ? "filled" : "empty";
            ImageView star = new ImageView("/images/star" + starType + ".png");
            star.setFitWidth(24);
            star.setFitHeight(24);
            ratingBox.getChildren().add(star);
        }

        // Текст отзыва
        Label reviewText = new Label(review.getReviewText());
        reviewText.setFont(Font.font("Segoe UI", 14));
        reviewText.setTextFill(Color.web("#334155"));
        reviewText.setWrapText(true);
        reviewText.setMaxWidth(550);

        card.getChildren().addAll(header, ratingBox, reviewText);
        card.setPadding(new Insets(15));
        return card;
    }

    // Метод для получения имени пользователя по ID (заглушка)
    private String getUserNameById(int userId) {
        // Здесь должна быть реализация получения имени пользователя по ID
        // Например, запрос к серверу или локальная база данных
        // Временно возвращаем заглушку
        return "Пользователь #" + userId;
    }

    @FXML
    private void handleSortButtonClick() {
        String selectedSort = sortComboBox.getValue();

        if (reviews != null && selectedSort != null) {
            if ("От 1 до 5".equals(selectedSort)) {
                reviews.sort(Comparator.comparingInt(Review::getRating));
            } else if ("От 5 до 1".equals(selectedSort)) {
                reviews.sort((r1, r2) -> Integer.compare(r2.getRating(), r1.getRating()));
            }

            reviewsListContainer.getChildren().clear();
            for (Review review : reviews) {
                VBox reviewCard = createReviewCard(review);
                reviewsListContainer.getChildren().add(reviewCard);
            }
        }
    }

    @FXML
    private void addReview() {
        try {
            navigateTo("/Client/addReviewsCL.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть форму добавления отзыва");
        }
    }

    @FXML
    private void handleBack() {
        try {
            navigateTo("/Employee/menuEmployee.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось вернуться в главное меню");
        }
    }

    // Общий метод для навигации
    private void navigateTo(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) mainContainer.getScene().getWindow();
        stage.setScene(scene);
        stage.setWidth(1090);
        stage.setHeight(720);
        stage.show();
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}