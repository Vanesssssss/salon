package Controllers.Сlient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;

public class ViewInfoController {
    private final String filePath = "info.xml"; // Путь к XML-файлу

    @FXML
    private VBox mainContainer;
    @FXML
    private TextArea infoTextArea;

    @FXML
    private void initialize() {
        loadInfo();
    }

    private void loadInfo() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                infoTextArea.setText("Информация не найдена. Добавьте данные в файл.");
                return;
            }

            // Чтение XML-файла
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            // Извлечение данных из XML
            String name = getTextContent(doc, "Name");
            String text = getTextContent(doc, "Text");
            String phone = getTextContent(doc, "Phone");
            String address = getTextContent(doc, "Address");

            // Формирование текста для отображения
            String info = String.format(
                    "📌 Название: %s%n%n%n" +
                            "📖 Описание: %s%n%n%n" +
                            "📞 Телефон: %s%n%n%n" +
                            "📍 Адрес: %s",
                    name, text, phone, address
            );
            infoTextArea.setText(info);

        } catch (Exception e) {
            infoTextArea.setText("Произошла ошибка при загрузке данных.");
            e.printStackTrace();
        }
    }

    private String getTextContent(Document doc, String tagName) {
        Element element = (Element) doc.getElementsByTagName(tagName).item(0);
        return element != null ? element.getTextContent() : "Не указано";
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
