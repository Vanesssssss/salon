import ServerWORK.ConnectDB;
import ServerWORK.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class BarberShopClient extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/mainpage.fxml"));
            primaryStage.setTitle("Barbershop Client");
            primaryStage.setScene(new Scene(root, 1090, 720));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        ConnectDB.user = new User("127.0.0.1", "9090");
        launch(args);
    }
}
