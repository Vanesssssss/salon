package Controllers.Admin;

import POJO.Services;
import ServerWORK.ConnectDB;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Добавьте этот импорт
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DelServiceHandler implements Initializable {

    @FXML
    private FlowPane servicesFlowPane;
    @FXML
    private Button backButton;

    private Services selectedService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtonAnimation(backButton);
        loadServices();
    }

    private void setupButtonAnimation(Button button) {
        button.setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();

            button.setEffect(new DropShadow(
                    javafx.scene.effect.BlurType.THREE_PASS_BOX,
                    javafx.scene.paint.Color.rgb(0,0,0,0.3),
                    10, 0, 0, 3));
        });

        button.setOnMouseExited(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

            button.setEffect(null);
        });
    }

    @FXML
    private void handleDeleteService(Services service) {
        selectedService = service;
        int serviceId = service.getServiseId();

        try {
            ConnectDB.user.sendMessage("deleteService");
            ConnectDB.user.sendObject(serviceId);

            Object response = ConnectDB.user.readObject();
            if (response instanceof String) {
                String message = (String) response;
                if (message.equals("Service deleted successfully")) {
                    loadServices();
                } else {
                    showAlert("Ошибка", "Ошибка при удалении услуги");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при удалении услуги");
        }
    }

    private void loadServices() {
        servicesFlowPane.getChildren().clear();
        try {
            ConnectDB.user.sendMessage("viewServices");
            Object response = ConnectDB.user.readObject();

            if (response instanceof List<?>) {
                List<?> servicesList = (List<?>) response;

                for (Object serviceObj : servicesList) {
                    if (serviceObj instanceof Services) {
                        Services service = (Services) serviceObj;
                        createServiceCard(service);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить список услуг");
        }
    }

    private void createServiceCard(Services service) {
        VBox serviceCard = new VBox(10);
        serviceCard.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 3);");
        serviceCard.setPrefWidth(350);

        // Название услуги
        Label nameLabel = new Label(service.getName());
        nameLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // Описание услуги
        Text description = new Text(service.getDescription());
        description.setWrappingWidth(320);
        description.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-fill: #475569;");

        // Цена и длительность
        HBox detailsBox = new HBox(20);
        Label priceLabel = new Label("Цена: " + service.getPrice() + " руб.");
        priceLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #475569;");

        Label durationLabel = new Label("Длит.: " + service.getDuration());
        durationLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-text-fill: #475569;");

        detailsBox.getChildren().addAll(priceLabel, durationLabel);

        // Кнопка удаления
        Button deleteButton = new Button("Удалить");
        deleteButton.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-font-weight: bold; " +
                "-fx-min-width: 100; -fx-min-height: 35; -fx-background-color: #ef4444; " +
                "-fx-text-fill: white; -fx-background-radius: 8;");
        deleteButton.setEffect(new DropShadow(javafx.scene.effect.BlurType.THREE_PASS_BOX,
                javafx.scene.paint.Color.rgb(239,68,68,0.3),
                8, 0, 0, 3));

        // Настройка анимации для кнопки удаления
        setupButtonAnimation(deleteButton);

        deleteButton.setOnAction(e -> handleDeleteService(service));

        serviceCard.getChildren().addAll(nameLabel, description, detailsBox, deleteButton);
        servicesFlowPane.getChildren().add(serviceCard);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/serviceAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setWidth(1090);
            stage.setHeight(720);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}