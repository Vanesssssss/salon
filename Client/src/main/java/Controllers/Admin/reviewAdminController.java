package Controllers.Admin;

import POJO.Review;
import ServerWORK.ConnectDB;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class reviewAdminController {
    @FXML private VBox mainContainer;
    @FXML private VBox reviewsListContainer;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button sortButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;

    private List<Review> reviews;

    @FXML
    private void refreshReviews() {
        // Анимация нажатия для кнопки обновления
        animateButtonPress(refreshButton);
        loadReviews();
    }

    private void loadReviews() {
        ConnectDB.user.sendMessage("viewReviews");
        reviews = (List<Review>) ConnectDB.user.readObject();

        if (reviews != null) {
            reviewsListContainer.getChildren().clear();
            for (Review review : reviews) {
                reviewsListContainer.getChildren().add(createReviewBox(review));
            }
        }
    }

    private HBox createReviewBox(Review review) {
        HBox reviewBox = new HBox(15);
        reviewBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        reviewBox.setPrefWidth(750);
        reviewBox.setAlignment(Pos.CENTER_LEFT);

        // Контейнер для текста отзыва
        VBox textContainer = new VBox(5);
        textContainer.setPrefWidth(500);

        Label reviewText = new Label(review.getReviewText());
        reviewText.setWrapText(true);
        reviewText.setFont(new Font(14));
        reviewText.setStyle("-fx-text-fill: #334155;");

        textContainer.getChildren().add(reviewText);

        // Контейнер для рейтинга и кнопки
        VBox ratingContainer = new VBox(15);
        ratingContainer.setAlignment(Pos.CENTER_RIGHT);

        // Рейтинг в звездах
        HBox ratingBox = new HBox(3);
        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView("/images/star" + (i <= review.getRating() ? "filled" : "empty") + ".png");
            star.setFitWidth(20);
            star.setFitHeight(20);
            ratingBox.getChildren().add(star);
        }

        // Кнопка удаления с анимацией
        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(e -> deleteReview(review));
        deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        deleteButton.setPrefWidth(100);

        // Анимации для кнопки удаления
        setupButtonAnimations(deleteButton, "#dc2626", "#b91c1c");

        ratingContainer.getChildren().addAll(ratingBox, deleteButton);
        reviewBox.getChildren().addAll(textContainer, ratingContainer);

        return reviewBox;
    }

    private void deleteReview(Review review) {
        ConnectDB.user.sendMessage("deleteReview");
        ConnectDB.user.sendObject(review.getReviewId());
        String response = (String) ConnectDB.user.readObject();

        if ("Review deleted successfully".equals(response)) {
            showAlert("Успех", "Отзыв успешно удален.");
            loadReviews();
        } else {
            showAlert("Ошибка", "Не удалось удалить отзыв.");
        }
    }

    @FXML
    private void handleSortButtonClick() {
        // Анимация нажатия для кнопки сортировки
        animateButtonPress(sortButton);

        String selectedSort = sortComboBox.getValue();
        if (reviews == null) return;

        if ("От низкого к высокому".equals(selectedSort)) {
            reviews.sort(Comparator.comparingInt(Review::getRating));
        } else if ("От высокого к низкому".equals(selectedSort)) {
            reviews.sort((r1, r2) -> Integer.compare(r2.getRating(), r1.getRating()));
        }

        reviewsListContainer.getChildren().clear();
        for (Review review : reviews) {
            reviewsListContainer.getChildren().add(createReviewBox(review));
        }
    }

    @FXML
    private void initialize() {
        loadReviews();
        sortComboBox.setValue("От низкого к высокому");

        // Настройка анимаций для кнопок в FXML
        setupButtonAnimations(sortButton, "#4f46e5", "#4338ca");
        setupButtonAnimations(refreshButton, "#0da271", "#0c8a61");
        setupButtonAnimations(backButton, "#475569", "#334155");
    }

    @FXML
    private void handleBack() {
        // Анимация нажатия для кнопки возврата
        animateButtonPress(backButton);

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Admin/menuAdmin.fxml"));
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(new Scene(root, 1090, 720));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Настройка анимаций для кнопок
    private void setupButtonAnimations(Button button, String hoverColor, String pressedColor) {
        final String originalStyle = button.getStyle();

        // Эффект при наведении
        button.setOnMouseEntered(e -> {
            button.setStyle(originalStyle + " -fx-background-color: " + hoverColor + ";");
            button.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.3)));
        });

        // Эффект при выходе
        button.setOnMouseExited(e -> {
            button.setStyle(originalStyle);
            button.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.2)));
        });

        // Эффект при нажатии
        button.setOnMousePressed(e -> {
            button.setStyle(originalStyle + " -fx-background-color: " + pressedColor + ";");
            button.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.3)));
        });

        // Возврат после нажатия
        button.setOnMouseReleased(e -> {
            button.setStyle(originalStyle + " -fx-background-color: " + hoverColor + ";");
            button.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.3)));
        });
    }

    // Анимация "нажатия" кнопки
    private void animateButtonPress(Button button) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(100), button);
        tt.setFromY(0);
        tt.setToY(2);
        tt.setAutoReverse(true);
        tt.setCycleCount(2);
        tt.play();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}