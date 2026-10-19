package Controllers.Admin;

import POJO.AssignServicesRequest;
import POJO.Employee;
import POJO.Services;
import ServerWORK.ConnectDB;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddSerMasController implements Initializable {

    @FXML
    private VBox mainContainer;
    @FXML
    private ComboBox<Employee> masterComboBox;
    @FXML
    private VBox servicesList;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    private List<Employee> allEmployees = new ArrayList<>();
    private List<Services> allServices = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Инициализация анимации для кнопок
        setupButtonAnimation(saveButton);
        setupButtonAnimation(backButton);

        loadEmployees();
        loadServices();
    }

    private void setupButtonAnimation(Button button) {
        // Анимация при наведении - увеличение
        button.setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();

            // Эффект тени при наведении
            button.setEffect(new javafx.scene.effect.DropShadow(
                    javafx.scene.effect.BlurType.THREE_PASS_BOX,
                    javafx.scene.paint.Color.rgb(0,0,0,0.3),
                    10, 0, 0, 3));
        });

        // Анимация при уходе курсора - возврат к исходному размеру
        button.setOnMouseExited(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

            // Убираем эффект тени
            button.setEffect(null);
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            showAlert("Ошибка", "Не удалось загрузить данные о мастерах. Попробуйте позже.");
        }
    }

    private void displayEmployees(List<Employee> employees) {
        masterComboBox.getItems().clear();
        masterComboBox.getItems().addAll(employees);

        masterComboBox.setCellFactory(new Callback<ListView<Employee>, ListCell<Employee>>() {
            @Override
            public ListCell<Employee> call(ListView<Employee> param) {
                return new ListCell<Employee>() {
                    @Override
                    protected void updateItem(Employee item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getLastName() + " " + item.getFirstName());
                            setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14;");
                        }
                    }
                };
            }
        });

        masterComboBox.setButtonCell(new ListCell<Employee>() {
            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getLastName() + " " + item.getFirstName());
                    setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16;");
                }
            }
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
            showAlert("Ошибка", "Не удалось загрузить данные об услугах. Попробуйте позже.");
        }
    }

    private void displayServices(List<Services> services) {
        servicesList.getChildren().clear();
        for (Services service : services) {
            CheckBox checkBox = new CheckBox(service.getName());
            checkBox.setUserData(service.getServiseId());

            // Стилизация чекбоксов
            checkBox.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16; -fx-text-fill: #475569; -fx-padding: 10;");
            checkBox.setWrapText(true);

            servicesList.getChildren().add(checkBox);
        }
    }

    @FXML
    private void saveMasterServices() {
        Employee selectedMaster = masterComboBox.getSelectionModel().getSelectedItem();
        if (selectedMaster == null) {
            showAlert("Ошибка", "Не выбран мастер.");
            return;
        }

        List<Integer> selectedServiceIds = new ArrayList<>();
        for (javafx.scene.Node node : servicesList.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                if (checkBox.isSelected()) {
                    selectedServiceIds.add((Integer) checkBox.getUserData());
                }
            }
        }

        if (selectedServiceIds.isEmpty()) {
            showAlert("Ошибка", "Не выбраны услуги.");
            return;
        }

        AssignServicesRequest request = new AssignServicesRequest(selectedMaster.getEmployeeId(), selectedServiceIds);

        ConnectDB.user.sendMessage("assignServicesToMaster");
        ConnectDB.user.sendObject(request);

        String response = "";
        try {
            response = ConnectDB.user.readMessage();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if ("Services assigned successfully".equals(response)) {
            showAlert("Успех", "Услуги успешно привязаны к мастеру.");
        } else {
            showAlert("Ошибка", "Ошибка при привязке услуг.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/sheduleMenu.fxml"));
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
}
