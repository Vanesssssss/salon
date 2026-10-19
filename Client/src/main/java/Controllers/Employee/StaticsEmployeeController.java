package Controllers.Employee;

import Controllers.SceneLoader;
import POJO.AppointmentRecord;
import ServerWORK.ConnectDB;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticsEmployeeController {
    @FXML
    private VBox mainContainer;
    @FXML
    private PieChart pieChart;
    @FXML
    private Button backButton;

    // Стили для кнопки "Назад"
    private static final String HOVER_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 45; " +
                    "-fx-background-color: #475569; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10;";

    private static final String DEFAULT_STYLE =
            "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 180; " +
                    "-fx-min-height: 45; " +
                    "-fx-background-color: #64748b; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 10;";

    @FXML
    private void initialize() {
        List<AppointmentRecord> appointments = loadAppointmentsForStatistics();
        initializePieChart(appointments);
    }

    private List<AppointmentRecord> loadAppointmentsForStatistics() {
        try {
            int userId = ConnectDB.id;
            ConnectDB.user.sendMessage("viewAppointmentsForEmployee");
            ConnectDB.user.sendMessage(String.valueOf(userId));

            Object response = ConnectDB.user.readObject();
            if (response instanceof List<?>) {
                return (List<AppointmentRecord>) response;
            } else {
                showAlert("Ошибка", "Не удалось загрузить записи для статистики.");
                return List.of();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при загрузке данных.");
            return List.of();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void initializePieChart(List<AppointmentRecord> appointments) {
        Map<String, Integer> clientRecordsCount = new HashMap<>();

        for (AppointmentRecord record : appointments) {
            String clientName = record.getClientLastName() + " " + record.getClientFirstName();
            clientRecordsCount.put(clientName, clientRecordsCount.getOrDefault(clientName, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : clientRecordsCount.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChart.getData().add(slice);
        }

        pieChart.setTitle("Распределение записей по клиентам");
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
    }

    @FXML
    private void handleMouseEnterExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(HOVER_STYLE);
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(DEFAULT_STYLE);
    }

    @FXML
    private void handleBack() {
        SceneLoader.loadScene((Stage) mainContainer.getScene().getWindow(),
                "/Employee/menuEmployee.fxml", 1090, 720);
    }
}