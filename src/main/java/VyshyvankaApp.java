import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class VyshyvankaApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
        stage.setTitle("Vyshyvanka | Author: Mokliak Vyacheslav");
        stage.setWidth(1080);
        stage.setHeight(720);
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);

    }

    public static void main(String[] args) {
        launch(args);
    }
}