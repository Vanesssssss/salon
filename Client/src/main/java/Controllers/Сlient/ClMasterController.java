package Controllers.Сlient;

import POJO.Employee;
import ServerWORK.ConnectDB;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import Controllers.SceneLoader;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClMasterController {
    @FXML
    private FlowPane mastersFlowPane; // Изменено имя в соответствии с FXML
    @FXML
    private TextField searchField;
    @FXML
    private TextField minExpField; // Переименовано для ясности
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private TextField maxExpField; // Переименовано для ясности
    @FXML
    private Button filterButton;
    @FXML
    private Button sortButton;
    @FXML
    private Button backButton;

    private List<Employee> allEmployees;

    // Стили для кнопок
    private static final String HOVER_STYLE = "-fx-background-color: #475569;";
    private static final String DEFAULT_STYLE_BACK = "-fx-background-color: #64748b;";
    private static final String DEFAULT_STYLE_FILTER = "-fx-background-color: #8b5cf6;";
    private static final String DEFAULT_STYLE_SORT = "-fx-background-color: #0ea5e9;";

    @FXML
    public void initialize() {
        setupButtonStyles();
        setupSortComboBox();
        loadEmployees();
    }

    private void setupButtonStyles() {
        backButton.setStyle(DEFAULT_STYLE_BACK);
        filterButton.setStyle(DEFAULT_STYLE_FILTER);
        sortButton.setStyle(DEFAULT_STYLE_SORT);

        addShadowEffect(backButton, "#64748b");
        addShadowEffect(filterButton, "#8b5cf6");
        addShadowEffect(sortButton, "#0ea5e9");
    }

    private void setupSortComboBox() {
        sortComboBox.getItems().addAll("От А до Я", "От Я до А");
        sortComboBox.setValue("От А до Я");
    }

    private void addShadowEffect(Button button, String color) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web(color + "80"));
        shadow.setRadius(6);
        shadow.setOffsetX(0);
        shadow.setOffsetY(3);
        button.setEffect(shadow);
    }

    private void loadEmployees() {
        try {
            ConnectDB.user.sendMessage("viewEmployees");
            Object response = ConnectDB.user.readObject();

            if (response instanceof List) {
                allEmployees = (List<Employee>) response;
                displayEmployees(allEmployees);
            } else {
                showAlert("Ошибка данных", "Получен неверный формат данных о мастерах");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка соединения", "Не удалось загрузить данные. Попробуйте позже.");
        }
    }

    private void displayEmployees(List<Employee> employees) {
        mastersFlowPane.getChildren().clear();
        employees.forEach(employee -> {
            VBox masterCard = createMasterCard(employee);
            mastersFlowPane.getChildren().add(masterCard);
        });
    }

    private VBox createMasterCard(Employee employee) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 15; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 3);");
        card.setPrefWidth(350);
        card.setMinWidth(350);

        Label nameLabel = createStyledLabel(
                employee.getLastName() + " " + employee.getFirstName(),
                "#1e293b", FontWeight.BOLD, 18
        );

        Label specializationLabel = createStyledLabel(
                employee.getSpecialization(),
                "#0ea5e9", FontWeight.BOLD, 16
        );

        Label experienceLabel = createStyledLabel(
                "Стаж: " + employee.getExperienceYears() + " лет",
                "#64748b", FontWeight.NORMAL, 14
        );

        Label workHoursLabel = createStyledLabel(
                "Часы работы: " + employee.getWorkStartTime() + " - " + employee.getWorkEndTime(),
                "#64748b", FontWeight.NORMAL, 14
        );

        card.getChildren().addAll(nameLabel, specializationLabel, experienceLabel, workHoursLabel);
        card.setPadding(new Insets(15));
        return card;
    }

    private Label createStyledLabel(String text, String colorHex, FontWeight weight, int size) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", weight, size));
        label.setTextFill(Color.web(colorHex));
        return label;
    }

    @FXML
    private void applySearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            displayEmployees(allEmployees);
            return;
        }

        List<Employee> filtered = allEmployees.stream()
                .filter(employee -> employee.getSpecialization().toLowerCase().contains(query))
                .collect(Collectors.toList());

        displayEmployees(filtered);
    }

    @FXML
    private void applyFilter() {
        try {
            double minExp = minExpField.getText().isEmpty() ? 0 : Double.parseDouble(minExpField.getText());
            double maxExp = maxExpField.getText().isEmpty() ? 100 : Double.parseDouble(maxExpField.getText());

            if (minExp < 0 || maxExp < 0) {
                showAlert("Ошибка", "Стаж работы не может быть отрицательным.");
                return;
            }

            if (minExp > maxExp) {
                showAlert("Ошибка", "Минимальный стаж не может быть больше максимального.");
                return;
            }

            List<Employee> filtered = allEmployees.stream()
                    .filter(employee -> employee.getExperienceYears() >= minExp &&
                            employee.getExperienceYears() <= maxExp)
                    .collect(Collectors.toList());

            displayEmployees(filtered);
        } catch (NumberFormatException e) {
            showAlert("Ошибка ввода", "Пожалуйста, введите числовые значения для стажа работы.");
        }
    }

    @FXML
    private void applySort() {
        String sortOrder = sortComboBox.getValue();
        if (sortOrder == null) return;

        Comparator<Employee> comparator = Comparator.comparing(Employee::getLastName);
        if (sortOrder.equals("От Я до А")) {
            comparator = comparator.reversed();
        }

        List<Employee> sorted = allEmployees.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        displayEmployees(sorted);
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneLoader.loadScene(stage, "/Client/windowClient.fxml", 1090, 720);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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