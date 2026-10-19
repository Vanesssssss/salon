package Controllers.Admin;

import POJO.Services;
import ServerWORK.ConnectDB;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class UpdateServiceController {

    @FXML
    private ComboBox<Services> serviceComboBox;
    @FXML
    private TextField serviceName;
    @FXML
    private TextArea serviceDescription;
    @FXML
    private TextField servicePrice;
    @FXML
    private ComboBox<Integer> durationValue;
    @FXML
    private ComboBox<String> durationUnit;
    @FXML
    private Button updateServiceButton;
    @FXML
    private Button backButton;

    @FXML
    void initialize() {
        // Инициализация анимации кнопок
        setupButtonAnimation(updateServiceButton);
        setupButtonAnimation(backButton);

        // Загрузка списка услуг
        loadServicesList();

        // Настройка кнопки обновления услуги
        updateServiceButton.setOnAction(event -> updateService());

        // Настройка кнопки "Назад"
        backButton.setOnAction(event -> handleBack());

        // Заполнение ComboBox для длительности
        fillDurationComboBoxes();
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

    // Загрузка списка услуг в ComboBox
    private void loadServicesList() {
        try {
            ConnectDB.user.sendMessage("viewServices");
            List<Services> servicesList = (List<Services>) ConnectDB.user.readObject();
            serviceComboBox.getItems().setAll(servicesList);

            // Использование кастомной ячейки для отображения
            serviceComboBox.setCellFactory(param -> new ListCell<Services>() {
                @Override
                protected void updateItem(Services item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getServiseId() + ", " + item.getName() + ", " + item.getDescription() + ", " + item.getPrice() + ", " + item.getDuration());
                        setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14;");
                    }
                }
            });

            // Устанавливаем текст для отображаемого элемента
            serviceComboBox.setButtonCell(new ListCell<Services>() {
                @Override
                protected void updateItem(Services item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getServiseId() + ", " + item.getName() + ", " + item.getDescription() + ", " + item.getPrice() + ", " + item.getDuration());
                        setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14;");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlertWithError();
        }
    }

    // Метод для обновления услуги
    @FXML
    void updateService() {
        Services selectedService = serviceComboBox.getValue();
        if (selectedService == null) {
            showAlertWithEmptyFields();
            return;
        }

        String name = serviceName.getText();
        String description = serviceDescription.getText();
        String priceText = servicePrice.getText();
        Integer duration = durationValue.getValue();
        String unit = durationUnit.getValue();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || duration == null || unit == null) {
            showAlertWithEmptyFields();
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            selectedService.setName(name);
            selectedService.setDescription(description);
            selectedService.setPrice(price);
            selectedService.setDuration(duration + " " + unit);
            sendServiceToServer(selectedService);
        } catch (NumberFormatException e) {
            showAlertWithInvalidPrice();
        }
    }

    // Метод для отправки измененной услуги на сервер
    private void sendServiceToServer(Services service) {
        try {
            ConnectDB.user.sendMessage("updateService");
            ConnectDB.user.sendObject(service);
            String response = ConnectDB.user.readMessage();
            if (response.equals("Service updated successfully")) {
                showAlertWithSuccess();
            } else {
                showAlertWithError();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertWithServerError();
        }
    }

    // Показать ошибку при пустых полях
    static void showAlertWithEmptyFields() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Пустые поля");
        alert.setContentText("Пожалуйста, заполните все поля.");
        alert.showAndWait();
    }

    // Показать ошибку при неправильной цене
    static void showAlertWithInvalidPrice() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Неверная цена");
        alert.setContentText("Пожалуйста, введите корректную цену.");
        alert.showAndWait();
    }

    // Показать успешное сообщение
    static void showAlertWithSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText("Услуга обновлена");
        alert.setContentText("Услуга была успешно обновлена.");
        alert.showAndWait();
    }

    // Показать ошибку при добавлении услуги
    static void showAlertWithError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка: Обновление услуги");
        alert.setContentText("Произошла ошибка при обновлении услуги. Попробуйте позже.");
        alert.showAndWait();
    }

    // Показать ошибку сервера
    static void showAlertWithServerError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка сервера");
        alert.setContentText("Не удалось подключиться к серверу. Попробуйте позже.");
        alert.showAndWait();
    }

    // Обработчик кнопки "Назад"
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/serviceAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Админ панель");
            stage.setScene(new Scene(root));
            stage.setWidth(1090);
            stage.setHeight(720);
            stage.show();

            // Закрытие текущего окна добавления услуги
            ((Stage) backButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillDurationComboBoxes() {
        for (int i = 1; i <= 60; i++) {
            durationValue.getItems().add(i);
        }
        durationUnit.getItems().addAll("минуты", "часы");
    }
}