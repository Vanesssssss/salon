package Controllers.Admin;

import POJO.Services;
import ServerWORK.ConnectDB;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

import java.util.*;
import java.util.stream.Collectors;

public class ViewServiceController {

    @FXML
    private FlowPane servicesFlowPane;
    @FXML
    private TextField searchField;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button filterButton;
    @FXML
    private Button sortButton;

    private List<Services> allServices; // Хранит все услуги для обработки

    @FXML
    void initialize() {
        // Инициализация анимаций для кнопок
        initButtonAnimation(searchButton, "#8b5cf6", "#a78bfa");
        initButtonAnimation(filterButton, "#7c3aed", "#8b5cf6");
        initButtonAnimation(sortButton, "#6d28d9", "#7c3aed");

        loadServices();
    }

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

    private void loadServices() {
        try {
            ConnectDB.user.sendMessage("viewServices");
            Object response = ConnectDB.user.readObject();

            if (response instanceof List<?>) {
                allServices = (List<Services>) response;
                displayServices(allServices);
            } else {
                System.out.println("Received response is not a List.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить данные. Попробуйте позже.");
        }
    }

    private void displayServices(List<Services> services) {
        servicesFlowPane.getChildren().clear();
        for (Services service : services) {
            VBox serviceBox = new VBox(10);
            serviceBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 3);");
            serviceBox.setPrefWidth(250);

            Label serviceName = new Label(service.getName());
            serviceName.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #4f46e5;");

            Text description = new Text(service.getDescription());
            description.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-fill: #64748b;");
            description.setWrappingWidth(220);

            Text price = new Text("Цена: " + service.getPrice() + " руб.");
            price.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-font-weight: bold; -fx-fill: #10b981;");

            Text duration = new Text("Продолжительность: " + service.getDuration());
            duration.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14; -fx-fill: #6b7280;");

            serviceBox.getChildren().addAll(serviceName, description, price, duration);
            servicesFlowPane.getChildren().add(serviceBox);
        }
    }

    @FXML
    private void applySearch() {
        String query = searchField.getText().toLowerCase();
        List<Services> filteredServices = allServices.stream()
                .filter(service -> service.getName().toLowerCase().contains(query))
                .collect(Collectors.toList());
        displayServices(filteredServices);
    }

    @FXML
    private void applyFilter() {
        try {
            double minPrice = minPriceField.getText().isEmpty() ? Double.MIN_VALUE : Double.parseDouble(minPriceField.getText());
            double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());

            List<Services> filteredServices = allServices.stream()
                    .filter(service -> service.getPrice() >= minPrice && service.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
            displayServices(filteredServices);
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректные значения для фильтрации.");
        }
    }

    @FXML
    private void applySort() {
        String sortOrder = sortComboBox.getValue();
        if (sortOrder != null) {
            List<Services> sortedServices = new ArrayList<>(allServices);
            if (sortOrder.equals("От А до Я")) {
                sortedServices.sort(Comparator.comparing(Services::getName));
            } else if (sortOrder.equals("От Я до А")) {
                sortedServices.sort(Comparator.comparing(Services::getName).reversed());
            }
            displayServices(sortedServices);
        }
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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