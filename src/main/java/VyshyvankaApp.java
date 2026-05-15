import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

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
    private Color selectedColor = Color.web("#8B1A1A");
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


        root.setCenter(buildCanvasPane());
        stage.setScene(scene);
        stage.show();

        redraw();
    }

    private StackPane buildCanvasPane() {
        canvas = new Canvas(cols * CELL_SIZE + 1, rows * CELL_SIZE + 1);
        gc = canvas.getGraphicsContext2D();

        canvas.setOnMousePressed((MouseEvent e) -> handleDraw(e.getX(), e.getY()));
        canvas.setOnMouseDragged((MouseEvent e) -> handleDraw(e.getX(), e.getY()));
        return new StackPane(canvas);
    }

    private void redraw(){
        // background
        gc.setFill(Color.web("#ffffff"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(grid[i][j] != null){
                    double x =  j * CELL_SIZE;
                    double y = i * CELL_SIZE;

                    gc.setFill(grid[i][j]);
                    gc.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

                    gc.setStroke(grid[i][j].darker());
                    gc.setLineWidth(0.5);
                    gc.strokeLine(x + 2, y + 2, x + CELL_SIZE - 2, y + CELL_SIZE - 2);
                    gc.strokeLine(x + CELL_SIZE - 2, y + 2, x + 2, y + CELL_SIZE - 2);
                }
            }
        }

        // grid lines
        gc.setStroke(Color.web("#cccccc"));
        gc.setLineWidth(0.5);
        for(int r = 0; r <= rows; r++){
            gc.strokeLine(0, r * CELL_SIZE, cols * CELL_SIZE, r * CELL_SIZE);
        }
        for(int c = 0; c <= cols; c++){
            gc.strokeLine(c * CELL_SIZE, 0, c * CELL_SIZE, rows * CELL_SIZE);
        }
    }

    private void handleDraw(double mx, double my) {
        int c = (int)(mx / CELL_SIZE);
        int r = (int)(my / CELL_SIZE);

        if (c < 0 || c >= cols || r < 0 || r >= rows) return;

        grid[r][c] = selectedColor;
        redraw();
    }

    public static void main(String[] args) {
        launch(args);
    }
}