package Controllers.Admin;

import POJO.AppointmentRecord;
import ServerWORK.ConnectDB;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminviewOrderController implements Initializable {
    // Константы для анимаций
    private static final Duration HOVER_DURATION = Duration.millis(200);
    private static final Duration PRESS_DURATION = Duration.millis(100);
    private static final double HOVER_SCALE = 1.05;
    private static final double PRESS_SCALE = 0.98;
    private static final double HOVER_OPACITY = 0.9;

    @FXML private TextField clientLastNameSearchField;
    @FXML private TextField employeeLastNameSearchField;
    @FXML private TextField priceFromField;
    @FXML private TextField priceToField;
    @FXML private VBox mainContainer;
    @FXML private TableView<AppointmentRecord> appointmentsTable;
    @FXML private TableColumn<AppointmentRecord, String> clientLastNameColumn;
    @FXML private TableColumn<AppointmentRecord, String> clientFirstNameColumn;
    @FXML private TableColumn<AppointmentRecord, String> clientPhoneNumberColumn;
    @FXML private TableColumn<AppointmentRecord, String> employeeLastNameColumn;
    @FXML private TableColumn<AppointmentRecord, String> employeeFirstNameColumn;
    @FXML private TableColumn<AppointmentRecord, String> serviceNameColumn;
    @FXML private TableColumn<AppointmentRecord, BigDecimal> servicePriceColumn;
    @FXML private TableColumn<AppointmentRecord, String> appointmentDateColumn;
    @FXML private TableColumn<AppointmentRecord, String> appointmentTimeColumn;
    @FXML private Button staticButton;
    @FXML private Button backButton;

    private List<AppointmentRecord> allAppointments = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Настройка таблицы
        clientLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientLastName"));
        clientFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientFirstName"));
        clientPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("clientPhoneNumber"));
        employeeLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeFirstName"));
        employeeFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeLastName"));
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        servicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        appointmentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));

        // Загрузка данных
        loadAppointments();
    }

    private void loadAppointments() {
        try {
            ConnectDB.user.sendMessage("viewAppointments");
            Object response = ConnectDB.user.readObject();

            if (response instanceof List<?>) {
                allAppointments = (List<AppointmentRecord>) response;
                appointmentsTable.getItems().setAll(allAppointments);
            } else {
                showAlert("Ошибка", "Не удалось загрузить данные о записях.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить данные о записях.");
        }
    }

    @FXML
    private void searchByClientLastName() {
        String clientLastName = clientLastNameSearchField.getText().trim().toLowerCase();
        List<AppointmentRecord> filteredAppointments = allAppointments.stream()
                .filter(record -> record.getClientLastName().toLowerCase().startsWith(clientLastName))
                .toList();
        appointmentsTable.getItems().setAll(filteredAppointments);
    }

    @FXML
    private void searchByEmployeeLastName() {
        String employeeLastName = employeeLastNameSearchField.getText().trim().toLowerCase();
        List<AppointmentRecord> filteredAppointments = allAppointments.stream()
                .filter(record -> record.getEmployeeFirstName().toLowerCase().startsWith(employeeLastName))
                .toList();
        appointmentsTable.getItems().setAll(filteredAppointments);
    }

    @FXML
    private void applyFilters() {
        try {
            BigDecimal priceFrom = priceFromField.getText().isEmpty() ? null : new BigDecimal(priceFromField.getText());
            BigDecimal priceTo = priceToField.getText().isEmpty() ? null : new BigDecimal(priceToField.getText());

            List<AppointmentRecord> filteredAppointments = allAppointments.stream()
                    .filter(record -> {
                        BigDecimal servicePrice = BigDecimal.valueOf(record.getServicePrice());
                        return (priceFrom == null || servicePrice.compareTo(priceFrom) >= 0) &&
                                (priceTo == null || servicePrice.compareTo(priceTo) <= 0);
                    })
                    .toList();

            appointmentsTable.getItems().setAll(filteredAppointments);
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректные значения для цены.");
        }
    }

    @FXML
    private void sortAppointmentsByDate() {
        List<AppointmentRecord> sortedAppointments = appointmentsTable.getItems().stream()
                .sorted((record1, record2) -> {
                    LocalDate date1 = LocalDate.parse(record1.getAppointmentDate());
                    LocalDate date2 = LocalDate.parse(record2.getAppointmentDate());
                    return date1.compareTo(date2);
                })
                .toList();

        appointmentsTable.getItems().setAll(sortedAppointments);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ================== АНИМАЦИИ КНОПОК ================== //

    @FXML
    private void handleMouseEnter(MouseEvent event) {
        Button button = (Button) event.getSource();
        animateButtonHover(button, HOVER_SCALE, HOVER_OPACITY);
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        animateButtonHover(button, 1.0, 1.0);
    }

    @FXML
    private void handleMousePress(MouseEvent event) {
        Button button = (Button) event.getSource();
        animateButtonPress(button, PRESS_SCALE);
    }

    @FXML
    private void handleMouseRelease(MouseEvent event) {
        Button button = (Button) event.getSource();
        animateButtonPress(button, 1.0);
    }

    @FXML
    private void handleMouseEnterExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        animateButtonHover(button, HOVER_SCALE, HOVER_OPACITY);
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        animateButtonHover(button, 1.0, 1.0);
    }

    private void animateButtonHover(Button button, double scale, double opacity) {
        ScaleTransition scaleTransition = new ScaleTransition(HOVER_DURATION, button);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);

        FadeTransition fadeTransition = new FadeTransition(HOVER_DURATION, button);
        fadeTransition.setToValue(opacity);

        ParallelTransition parallel = new ParallelTransition(button, scaleTransition, fadeTransition);
        parallel.play();
    }

    private void animateButtonPress(Button button, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(PRESS_DURATION, button);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }

    // ================== ОБРАБОТЧИКИ ДЕЙСТВИЙ ================== //

    @FXML
    private void handleBack() {
        animateBackButton();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/menuAdmin.fxml"));
            Parent registerPage = loader.load();
            Scene registerScene = new Scene(registerPage);

            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setWidth(1090);
            stage.setHeight(720);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void animateBackButton() {
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), backButton);
        scaleOut.setToX(0.9);
        scaleOut.setToY(0.9);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), backButton);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        scaleOut.setOnFinished(e -> scaleIn.play());
        scaleOut.play();
    }

    @FXML
    private void handleStatic() {
        animateStatisticsButton();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/statisticsView.fxml"));
            Parent statisticsPage = loader.load();

            StatisticsController controller = loader.getController();
            controller.setData(appointmentsTable.getItems());

            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(new Scene(statisticsPage));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно статистики.");
        }
    }

    private void animateStatisticsButton() {
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), staticButton);
        scaleOut.setToX(0.95);
        scaleOut.setToY(0.95);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), staticButton);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        scaleOut.setOnFinished(e -> scaleIn.play());
        scaleOut.play();
    }
}