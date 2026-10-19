package Controllers.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;

public class AdminOurInfoController {
    @FXML
    private VBox mainContainer;
    private final String filePath = "info.xml"; // Путь к XML-файлу
    @FXML
    private TextField nameField;
    @FXML
    private TextField textField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private void handleAddInfo() {
        try {
            String name = nameField.getText().trim();
            String text = textField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || text.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                showAlert("Ошибка", "Все поля должны быть заполнены!");
                return;
            }

            // Создаем XML-документ
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("Salon");
            doc.appendChild(rootElement);

            Element nameElement = doc.createElement("Name");
            nameElement.appendChild(doc.createTextNode(name));
            rootElement.appendChild(nameElement);

            Element textElement = doc.createElement("Text");
            textElement.appendChild(doc.createTextNode(text));
            rootElement.appendChild(textElement);

            Element phoneElement = doc.createElement("Phone");
            phoneElement.appendChild(doc.createTextNode(phone));
            rootElement.appendChild(phoneElement);

            Element addressElement = doc.createElement("Address");
            addressElement.appendChild(doc.createTextNode(address));
            rootElement.appendChild(addressElement);

            // Сохраняем XML в файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult file = new StreamResult(new File(filePath));
            transformer.transform(source, file);

            showAlert("Успех", "Информация добавлена!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось добавить информацию.");
        }
    }

    @FXML
    private void handleUpdInfo() {
        try {
            String name = nameField.getText().trim();
            String text = textField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || text.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                showAlert("Ошибка", "Все поля должны быть заполнены!");
                return;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                showAlert("Ошибка", "Файл с информацией не найден.");
                return;
            }

            // Обновляем XML-документ
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().getElementsByTagName("Name").item(0).setTextContent(name);
            doc.getDocumentElement().getElementsByTagName("Text").item(0).setTextContent(text);
            doc.getDocumentElement().getElementsByTagName("Phone").item(0).setTextContent(phone);
            doc.getDocumentElement().getElementsByTagName("Address").item(0).setTextContent(address);

            // Сохраняем изменения
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult resultFile = new StreamResult(file);
            transformer.transform(source, resultFile);

            showAlert("Успех", "Информация обновлена!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось обновить информацию.");
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleMouseEnter(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-radius: 15; -fx-font-family: 'Georgia'; -fx-font-size: 20; -fx-pref-width: 300; -fx-pref-height: 50; -fx-background-color: #333333; -fx-text-fill: #FFF8DC;"); // Темный фон при наведении
    }

    @FXML
    private void handleMouseExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-background-radius: 15; -fx-font-family: 'Georgia'; -fx-font-size: 20; -fx-pref-width: 300; -fx-pref-height: 50; -fx-background-color: #A0522D; -fx-text-fill: #FFF8DC;"); // Возврат к исходному черному фону
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/menuAdmin.fxml"));
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
