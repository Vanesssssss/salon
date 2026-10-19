package Controllers.Admin;

import POJO.Employee;
import POJO.CreateScheduleRequest;
import ServerWORK.ConnectDB;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AddScheduleController {

    @FXML
    private Button backButton;
    @FXML
    private ComboBox<String> masterComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<Integer> startHourComboBox;
    @FXML
    private ComboBox<Integer> startMinuteComboBox;
    @FXML
    private ComboBox<Integer> endHourComboBox;
    @FXML
    private ComboBox<Integer> endMinuteComboBox;

    private List<Employee> allEmployees;

    // Константы для анимаций
    private static final Duration HOVER_DURATION = Duration.millis(200);
    private static final Duration PRESS_DURATION = Duration.millis(100);
    private static final double HOVER_SCALE = 1.05;
    private static final double PRESS_SCALE = 0.98;
    private static final double HOVER_OPACITY = 0.9;

    @FXML
    private void initialize() {
        ObservableList<Integer> hours = FXCollections.observableArrayList(
                9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
        );
        ObservableList<Integer> minutes = FXCollections.observableArrayList(0, 30);

        startHourComboBox.setItems(hours);
        endHourComboBox.setItems(hours);
        startMinuteComboBox.setItems(minutes);
        endMinuteComboBox.setItems(minutes);

        loadEmployees();
    }

    private void loadEmployees() {
        try {
            ConnectDB.user.sendMessage("viewEmployees");
            Object response = ConnectDB.user.readObject();

            if (response instanceof List<?>) {
                allEmployees = (List<Employee>) response;
                displayEmployees(allEmployees);
            } else {
                System.out.println("Received response is not a List.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить данные. Попробуйте позже.");
        }
    }

    private void displayEmployees(List<Employee> employees) {
        ObservableList<String> employeeNames = FXCollections.observableArrayList();
        for (Employee employee : employees) {
            employeeNames.add(employee.getLastName());
        }
        masterComboBox.setItems(employeeNames);
    }

    @FXML
    private void createSchedule(ActionEvent event) {
        String selectedMaster = masterComboBox.getSelectionModel().getSelectedItem();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        Integer startHour = startHourComboBox.getValue();
        Integer startMinute = startMinuteComboBox.getValue();
        Integer endHour = endHourComboBox.getValue();
        Integer endMinute = endMinuteComboBox.getValue();

        if (selectedMaster == null || startDate == null || endDate == null ||
                startHour == null || startMinute == null || endHour == null || endMinute == null) {
            showAlert("Ошибка", "Пожалуйста, заполните все поля.");
            return;
        }

        LocalTime startTime = LocalTime.of(startHour, startMinute);
        LocalTime endTime = LocalTime.of(endHour, endMinute);

        int employeeId = getEmployeeIdByName(selectedMaster);
        if (employeeId == -1) {
            showAlert("Ошибка", "Мастер не найден.");
            return;
        }

        CreateScheduleRequest request = new CreateScheduleRequest();
        request.setEmployeeId(employeeId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setStartTime(startTime);
        request.setEndTime(endTime);

        try {
            ConnectDB.user.sendMessage("createSchedule");
            ConnectDB.user.sendObject(request);

            String response = (String) ConnectDB.user.readObject();
            if ("Расписание успешно создано".equals(response)) {
                showSuccessAnimation();
                showAlert("Успех", "Расписание для мастера создано успешно.");
            } else {
                showAlert("Ошибка", response);
            }
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось установить связь с сервером. Попробуйте позже.");
            e.printStackTrace();
        }
    }

    private int getEmployeeIdByName(String masterName) {
        for (Employee employee : allEmployees) {
            if (employee.getLastName().equals(masterName)) {
                return employee.getEmployeeId();
            }
        }
        return -1;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Анимация при наведении на кнопки
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

    // Анимация при нажатии кнопок
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

    // Анимация для кнопки "Назад"
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

    // Анимация успешного создания расписания
    private void showSuccessAnimation() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(300));
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        FadeTransition fade = new FadeTransition(Duration.millis(600));
        fade.setFromValue(1.0);
        fade.setToValue(0.7);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);

        ParallelTransition parallel = new ParallelTransition(masterComboBox, scale, fade);
        parallel.play();
    }

    // Общие методы анимации
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

    @FXML
    private void handleBack() {
        animateBackButton();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/sheduleMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Админ панель");
            stage.setScene(new Scene(root));
            stage.setWidth(1090);
            stage.setHeight(720);
            stage.show();

            ((Stage) backButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Анимация кнопки "Назад" при клике
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
}