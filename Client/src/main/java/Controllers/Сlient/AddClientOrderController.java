package Controllers.Сlient;

import POJO.AppointmentRequest;
import POJO.Employee;
import POJO.Services;
import ServerWORK.ConnectDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.time.format.DateTimeFormatter;

public class AddClientOrderController implements Initializable {
    @FXML
    private ComboBox<Employee> masterComboBox; // ComboBox для выбора мастера
    @FXML
    private ComboBox<Services> serviceComboBox; // ComboBox для выбора услуги
    @FXML
    private DatePicker datePicker; // Выбор даты
    @FXML
    private ComboBox<String> timeComboBox; // Выбор времени
    @FXML
    private VBox mainContainer;

    private List<Employee> allEmployees = new ArrayList<>();
    private List<Services> allServices = new ArrayList<>();

    // Метод для отображения алертов
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Загрузка всех мастеров в ComboBox
    private void loadEmployees() {
        try {
            ConnectDB.user.sendMessage("viewEmployees");
            Object response = ConnectDB.user.readObject();

            if (response instanceof List<?>) {
                allEmployees = (List<Employee>) response;
                masterComboBox.getItems().addAll(allEmployees);
            } else {
                System.out.println("Received response is not a List.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить данные о мастерах. Попробуйте позже.");
        }
    }
    private void configureMasterComboBox() {
        masterComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (empty || employee == null) {
                    setText(null);
                } else {
                    setText(employee.getLastName() + " " + employee.getFirstName());
                }
            }
        });

        masterComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (empty || employee == null) {
                    setText(null);
                } else {
                    setText(employee.getLastName() + " " + employee.getFirstName());
                }
            }
        });
    }
    private void configureServiceComboBox() {
        serviceComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Services service, boolean empty) {
                super.updateItem(service, empty);
                if (empty || service == null) {
                    setText(null);
                } else {
                    setText(service.getName() + " - " + service.getPrice() + " руб.");
                }
            }
        });

        serviceComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Services service, boolean empty) {
                super.updateItem(service, empty);
                if (empty || service == null) {
                    setText(null);
                } else {
                    setText(service.getName() + " - " + service.getPrice() + " руб.");
                }
            }
        });
    }
    // Загрузка услуг для выбранного мастера
    private void loadServicesByMaster(Employee selectedMaster) {
        if (selectedMaster == null) {
            serviceComboBox.getItems().clear();
            return;
        }

        try {
            ConnectDB.user.sendMessage("getServicesByEmployeeId");
            ConnectDB.user.sendObject(selectedMaster.getEmployeeId());
            Object response = ConnectDB.user.readObject();

            if (response instanceof List<?>) {
                List<Services> servicesList = (List<Services>) response;
                serviceComboBox.getItems().clear();
                serviceComboBox.getItems().addAll(servicesList);
            } else {
                showAlert("Ошибка", "Не удалось загрузить услуги для выбранного мастера.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить услуги для выбранного мастера.");
        }
    }

    // Загрузка доступных временных слотов для мастера и услуги
    private void loadAvailableTimes() {
        Employee selectedMaster = masterComboBox.getSelectionModel().getSelectedItem();
        Services selectedService = serviceComboBox.getSelectionModel().getSelectedItem();
        if (selectedMaster != null && selectedService != null && datePicker.getValue() != null) {
            String selectedDate = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            try {
                ConnectDB.user.sendMessage("getAvailableTimes");
                ConnectDB.user.sendObject(selectedMaster.getEmployeeId());
                ConnectDB.user.sendObject(selectedService.getServiseId());
                ConnectDB.user.sendObject(selectedDate);

                Object response = ConnectDB.user.readObject();
                if (response instanceof List<?>) {
                    List<String> availableTimes = (List<String>) response;
                    timeComboBox.getItems().clear();

                    if (availableTimes.isEmpty()) {
                        showAlert("Информация", "Нет доступных временных слотов на выбранную дату.");
                    } else {
                        timeComboBox.getItems().addAll(availableTimes);
                    }
                } else {
                    showAlert("Ошибка", "Получены некорректные данные от сервера.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось загрузить временные слоты.");
            }
        }
    }

    // Сохранение записи на услугу
    @FXML
    private void saveAppointment() {
        Employee selectedMaster = masterComboBox.getSelectionModel().getSelectedItem();
        Services selectedService = serviceComboBox.getSelectionModel().getSelectedItem();
        String selectedDate = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
        String selectedTime = timeComboBox.getSelectionModel().getSelectedItem();
        String timeOnly = selectedTime.split(" ")[1];
        if (selectedMaster == null || selectedService == null || selectedDate.isEmpty() || selectedTime == null) {
            showAlert("Ошибка", "Пожалуйста, выберите все необходимые данные.");
            return;
        }

        // Отправляем запрос на сервер для получения client_id по users_id
        int clientId = getClientIdByUserId(ConnectDB.id);
        if (clientId == -1) {
            showAlert("Ошибка", "Не удалось найти информацию о клиенте.");
            return;
        }

        // Создаем объект AppointmentRequest
        AppointmentRequest appointmentRequest = new AppointmentRequest(
                clientId,
                selectedMaster.getEmployeeId(),
                selectedService.getServiseId(),
                selectedDate,
                timeOnly
        );

        // Отправляем запрос на создание записи на услугу
        try {
            ConnectDB.user.sendMessage("createAppointment");
            ConnectDB.user.sendObject(appointmentRequest);

            String response = ConnectDB.user.readMessage();
            if ("Appointment created successfully".equals(response)) {
                showAlert("Успех", "Запись на услугу успешно создана.");
            } else {
                showAlert("Ошибка", "Не удалось создать запись на услугу.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Ошибка при создании записи.");
        }
    }

    // Запрос на получение client_id по users_id
    private int getClientIdByUserId(int usersId) {
        ConnectDB.user.sendMessage("getClientIdByUserId");
        ConnectDB.user.sendObject(usersId);

        Object response = ConnectDB.user.readObject();  // Получаем объект, а не строку
        if (response instanceof Integer) {
            return (Integer) response;  // Если это Integer, возвращаем его
        } else {
            System.out.println("Invalid response type: " + response.getClass().getName());
            return -1;  // Если тип данных не Integer, возвращаем -1
        }
    }

    // Инициализация контроллера
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureMasterComboBox();
        configureServiceComboBox();
        loadEmployees();

        masterComboBox.setOnAction(event -> {
            Employee selectedMaster = masterComboBox.getSelectionModel().getSelectedItem();
            loadServicesByMaster(selectedMaster);
        });

        serviceComboBox.setOnAction(event -> loadAvailableTimes());

        datePicker.setOnAction(event -> loadAvailableTimes());
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
    private void handleBack() {
        try {
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
    @FXML
    private void handleMyOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Employee/handleMyOrder.fxml"));
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
