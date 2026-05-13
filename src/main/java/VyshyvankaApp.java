import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class VyshyvankaApp extends Application {

    // size of single cell size; default amounts of rows and colons
    private static final int CELL_SIZE = 20;
    private static final int DEFAULT_COLS = 40;
    private static final int DEFAULT_ROWS = 30;

    // size of drawing window is changeable, so we need another variables
    private int cols = DEFAULT_COLS;
    private int rows = DEFAULT_ROWS;

    // Color of drawing
    public Color[][] grid;

    // Canvas drawing variables
    private Canvas canvas;
    private GraphicsContext gc;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Vyshyvanka | Author: Mokliak Vyacheslav");
        grid = new Color[rows][cols];
        stage.setWidth(1000);
        stage.setHeight(700);
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}