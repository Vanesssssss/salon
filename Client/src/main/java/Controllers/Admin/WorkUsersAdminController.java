package Controllers.Admin;

import POJO.Users;
import ServerWORK.ConnectDB;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WorkUsersAdminController {

    @FXML
    private VBox mainContainer;
    @FXML
    private TextField loginToDeleteField;
    @FXML
    private TextField loginToUpdateField;
    @FXML
    private TextField newRoleField;
    @FXML
    private TextField loginTosearchField1;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private ListView<String> usersListView;
    @FXML
    private Button viewAllUsersButton;
    @FXML
    private Button deleteUserButton;
    @FXML
    private Button updateRoleButton;
    @FXML
    private Button backButton;

    private List<Users> usersList = new ArrayList<>();

    // Анимации для кнопок
    private ScaleTransition viewAllUsersScale;
    private ScaleTransition deleteUserScale;
    private ScaleTransition updateRoleScale;
    private ScaleTransition backButtonScale;

    @FXML
    public void initialize() {
        // Инициализация анимаций
        initAnimations();
    }

    private void initAnimations() {
        // Анимация для кнопки "Показать всех пользователей"
        viewAllUsersScale = new ScaleTransition(Duration.millis(150), viewAllUsersButton);
        viewAllUsersButton.setScaleX(1.0);
        viewAllUsersButton.setScaleY(1.0);

        // Анимация для кнопки "Удалить"
        deleteUserScale = new ScaleTransition(Duration.millis(150), deleteUserButton);
        deleteUserButton.setScaleX(1.0);
        deleteUserButton.setScaleY(1.0);

        // Анимация для кнопки "Изменить"
        updateRoleScale = new ScaleTransition(Duration.millis(150), updateRoleButton);
        updateRoleButton.setScaleX(1.0);
        updateRoleButton.setScaleY(1.0);

        // Анимация для кнопки "Назад"
        backButtonScale = new ScaleTransition(Duration.millis(150), backButton);
        backButton.setScaleX(1.0);
        backButton.setScaleY(1.0);
    }

    @FXML
    private void deleteUser() {
        String login = loginToDeleteField.getText().trim();

        if (login.isEmpty()) {
            showAlert("Ошибка", "Введите логин пользователя.");
            return;
        }

        try {
            ConnectDB.user.sendMessage("deleteUser");
            ConnectDB.user.sendObject(login);

            Object response = ConnectDB.user.readObject();
            if ("User deleted successfully".equals(response)) {
                showAlert("Успех", "Пользователь удален.");
                viewAllUsers();
            } else {
                showAlert("Ошибка", "Не удалось удалить пользователя.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при удалении пользователя.");
        }
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        String searchText = loginTosearchField1.getText().trim();

        List<Users> filteredUsers = usersList.stream()
                .filter(user -> user.getLogin().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        updateUsersListView(filteredUsers);
    }

    @FXML
    private void handleSort() {
        String selectedSortOrder = sortComboBox.getValue();

        List<Users> sortedUsers = new ArrayList<>(usersList);

        if ("A-Z".equals(selectedSortOrder)) {
            sortedUsers.sort(Comparator.comparing(Users::getLogin));
        } else if ("Z-A".equals(selectedSortOrder)) {
            sortedUsers.sort((u1, u2) -> u2.getLogin().compareTo(u1.getLogin()));
        }

        updateUsersListView(sortedUsers);
    }

    @FXML
    private void handleFilter() {
        String selectedRole = roleComboBox.getValue();

        List<Users> filteredUsers = usersList.stream()
                .filter(user -> user.getRole().equalsIgnoreCase(selectedRole))
                .collect(Collectors.toList());

        updateUsersListView(filteredUsers);
    }

    @FXML
    private void updateUserRole() {
        String login = loginToUpdateField.getText().trim();
        String newRole = newRoleField.getText().trim();

        if (login.isEmpty() || newRole.isEmpty()) {
            showAlert("Ошибка", "Введите логин и новую роль пользователя.");
            return;
        }

        try {
            ConnectDB.user.sendMessage("updateUserRole");
            ConnectDB.user.sendObject(login);
            ConnectDB.user.sendObject(newRole);

            Object response = ConnectDB.user.readObject();
            if ("User role updated successfully".equals(response)) {
                showAlert("Успех", "Роль пользователя обновлена.");
                viewAllUsers();
            } else {
                showAlert("Ошибка", "Не удалось обновить роль пользователя.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при изменении роли пользователя.");
        }
    }

    @FXML
    private void viewAllUsers() {
        try {
            ConnectDB.user.sendMessage("viewAllUsers");

            Object response = ConnectDB.user.readObject();
            if (response instanceof List<?>) {
                usersList = (List<Users>) response;
                updateUsersListView(usersList);
            } else {
                showAlert("Ошибка", "Не удалось получить список пользователей.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при получении списка пользователей.");
        }
    }

    private void updateUsersListView(List<Users> users) {
        usersListView.getItems().clear();
        for (Users user : users) {
            usersListView.getItems().add("Логин: " + user.getLogin() + "    Роль: " + user.getRole());
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
    private void handleMouseEnter(MouseEvent event) {
        Button button = (Button) event.getSource();
        String hoverColor = "";

        if (button == viewAllUsersButton) {
            hoverColor = "#0284c7"; // Более темный синий
        } else if (button == deleteUserButton) {
            hoverColor = "#dc2626"; // Более темный красный
        } else if (button == updateRoleButton) {
            hoverColor = "#059669"; // Более темный зеленый
        }

        if (!hoverColor.isEmpty()) {
            button.setStyle(button.getStyle().replaceFirst("#[0-9a-fA-F]{6,8}", hoverColor));

            // Анимация для кнопки
            ScaleTransition st = null;
            if (button == viewAllUsersButton) st = viewAllUsersScale;
            else if (button == deleteUserButton) st = deleteUserScale;
            else if (button == updateRoleButton) st = updateRoleScale;

            if (st != null) {
                st.stop();
                st.setToX(1.05);
                st.setToY(1.05);
                st.play();
            }
        }
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        String defaultColor = "";

        if (button == viewAllUsersButton) {
            defaultColor = "#0ea5e9"; // Исходный синий
        } else if (button == deleteUserButton) {
            defaultColor = "#ef4444"; // Исходный красный
        } else if (button == updateRoleButton) {
            defaultColor = "#10b981"; // Исходный зеленый
        }

        if (!defaultColor.isEmpty()) {
            button.setStyle(button.getStyle().replaceFirst("#[0-9a-fA-F]{6,8}", defaultColor));

            // Анимация для кнопки
            ScaleTransition st = null;
            if (button == viewAllUsersButton) st = viewAllUsersScale;
            else if (button == deleteUserButton) st = deleteUserScale;
            else if (button == updateRoleButton) st = updateRoleScale;

            if (st != null) {
                st.stop();
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
        }
    }

    @FXML
    private void handleMouseEnterExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(BACK_HOVER_STYLE);

            // Анимация для кнопки "Назад"
            backButtonScale.stop();
            backButtonScale.setToX(1.05);
            backButtonScale.setToY(1.05);
            backButtonScale.play();
        }
    }

    @FXML
    private void handleMouseExitExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        if (button == backButton) {
            button.setStyle(BACK_DEFAULT_STYLE);

            // Анимация для кнопки "Назад"
            backButtonScale.stop();
            backButtonScale.setToX(1.0);
            backButtonScale.setToY(1.0);
            backButtonScale.play();
        }
    }

    @FXML
    private void handleBack() {
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
}