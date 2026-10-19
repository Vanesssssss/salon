package Controllers.Сlient;

import POJO.AppointmentRecord;
import ServerWORK.ConnectDB;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HandleMyOrderController implements Initializable {
    @FXML
    private TextField employeeLastNameSearchField;
    @FXML
    private TextField priceFromField;
    @FXML
    private TextField priceToField;
    @FXML
    private VBox mainContainer;
    @FXML
    private TableView<AppointmentRecord> appointmentsTable;
    @FXML
    private TableColumn<AppointmentRecord, String> employeeLastNameColumn;
    @FXML
    private TableColumn<AppointmentRecord, String> employeeFirstNameColumn;
    @FXML
    private TableColumn<AppointmentRecord, String> serviceNameColumn;
    @FXML
    private TableColumn<AppointmentRecord, BigDecimal> servicePriceColumn;
    @FXML
    private TableColumn<AppointmentRecord, String> appointmentDateColumn;
    @FXML
    private TableColumn<AppointmentRecord, String> appointmentTimeColumn;
    @FXML
    private TableColumn<AppointmentRecord, Void> cancelAppointmentColumn;
    @FXML
    private Button backButton;
    private List<AppointmentRecord> allAppointments = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureTableColumns();
        loadAppointments();
    }

    /**
     * Конфигурация столбцов таблицы.
     */
    private void configureTableColumns() {
        employeeLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeLastName"));
        employeeFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeFirstName"));
        serviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        servicePriceColumn.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));
        appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        appointmentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));

        // Настраиваем столбец для кнопок отмены записи
        cancelAppointmentColumn.setCellFactory(param -> new TableCell<>() {
            private final Button cancelButton = new Button("Отменить");
            private final Label completedLabel = new Label("Выполнено");

            {
                cancelButton.setOnAction(event -> {
                    AppointmentRecord record = getTableView().getItems().get(getIndex());
                    handleCancelAppointment(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    AppointmentRecord record = getTableView().getItems().get(getIndex());
                    String appointmentDate = record.getAppointmentDate();  // Получаем дату записи
                    String currentDate = java.time.LocalDate.now().toString();  // Текущая дата

                    if (appointmentDate.compareTo(currentDate) < 0) {
                        // Если дата записи прошла, показываем текст "Выполнено"
                        setGraphic(completedLabel);
                    } else {
                        // Если дата записи не прошла, показываем кнопку отмены
                        setGraphic(cancelButton);
                    }
                }
            }
        });

    }

    /**
     * Загрузка данных о записях с сервера.
     */
    private void loadAppointments() {
        try {
            int userId = ConnectDB.id; // ID клиента
            ConnectDB.user.sendMessage("viewAppointmentsForClient");
            ConnectDB.user.sendMessage(String.valueOf(userId));

            Object response = ConnectDB.user.readObject();
            if (response instanceof List<?>) {
                allAppointments = (List<AppointmentRecord>) response;
                appointmentsTable.getItems().setAll(allAppointments);
            } else {
                showAlert("Ошибка", "Не удалось загрузить записи.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить записи.");
        }
    }

    /**
     * Обработка отмены записи.
     *
     * @param record Запись, которую нужно отменить.
     */
    private void handleCancelAppointment(AppointmentRecord record) {
        try {
            int appointmentId = record.getAppointmentId();  // Получаем ID записи
            System.out.println("Попытка удалить запись с ID: " + appointmentId); // Логируем ID записи

            ConnectDB.user.sendMessage("deleteAppointment");
            ConnectDB.user.sendMessage(String.valueOf(appointmentId)); // Отправляем ID записи

            String response = (String) ConnectDB.user.readObject();
            if ("Appointment deleted successfully".equals(response)) {
                appointmentsTable.getItems().remove(record); // Удаляем запись из таблицы
                showAlert("Успех", "Запись успешно отменена.");
            } else {
                showAlert("Ошибка", "Не удалось отменить запись.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при отмене записи.");
        }
    }
    /**
     * Отображение сообщения об ошибке или успехе.
     *
     * @param title   Заголовок сообщения.
     * @param message Текст сообщения.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void searchByEmployeeLastName() {
        String employeeLastName = employeeLastNameSearchField.getText().trim().toLowerCase();
        List<AppointmentRecord> filteredAppointments = allAppointments.stream()
                .filter(record -> record.getEmployeeLastName().toLowerCase().startsWith(employeeLastName))
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
                    // Преобразуем строки в LocalDate
                    LocalDate date1 = LocalDate.parse(record1.getAppointmentDate()); // Используем только дату из строки
                    LocalDate date2 = LocalDate.parse(record2.getAppointmentDate()); // Используем только дату из строки
                    return date1.compareTo(date2); // Сравниваем только даты
                })
                .toList();

        // Обновляем таблицу
        appointmentsTable.getItems().setAll(sortedAppointments);
    }
    private static final String HOVER_STYLE = "-fx-background-radius: 15; " +
            "-fx-border-radius: 15; " +
            "-fx-font-family: 'Georgia'; " +
            "-fx-font-size: 16; " +
            "-fx-pref-width: 130; " +
            "-fx-pref-height: 40; " +
            "-fx-background-color: #333333; " + // Тёмный фон
            "-fx-text-fill: #FFF8DC; " +       // Светлый текст
            "-fx-border-color: black; " +
            "-fx-border-width: 2;";

    // Исходный стиль
    private static final String DEFAULT_STYLE = "-fx-background-radius: 15; " +
            "-fx-border-radius: 15; " +
            "-fx-font-family: 'Georgia'; " +
            "-fx-font-size: 16; " +
            "-fx-pref-width: 130; " +
            "-fx-pref-height: 40; " +
            "-fx-background-color: white; " + // Прозрачный фон
            "-fx-text-fill: black; " +              // Чёрный текст
            "-fx-border-color: black; " +
            "-fx-border-width: 2;";

    @FXML
    private void handleMouseEnterExit(javafx.scene.input.MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(HOVER_STYLE); // Устанавливаем стиль при наведении
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle(DEFAULT_STYLE); // Возвращаем исходный стиль
    }
    @FXML
    private void handleBack()
    {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Client/windowClient.fxml"));
            Parent registerPage = loader.load();
            Scene registerScene = new Scene(registerPage);

            // Получаем текущее окно и устанавливаем новую сцену
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setWidth(1090);
            stage.setHeight(720);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
