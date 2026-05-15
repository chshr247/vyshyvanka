import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

public class VyshyvankaApp extends Application {

    // size of single cell size; default amounts of rows and colons
    private static final int CELL_SIZE = 20;
    private static final int DEFAULT_COLS = 41;
    private static final int DEFAULT_ROWS = 31;

    // size of drawing window is changeable, so we need another variables
    private int cols = DEFAULT_COLS;
    private int rows = DEFAULT_ROWS;

    // Color of drawing
    public Color[][] grid;
    private static final Color[] PALETTE = {
            Color.web("#8B1A1A"), // dark red
            Color.web("#FF4500"), // orange red
            Color.web("#228B22"), // forest green
            Color.web("#1E90FF"), // dodger blue
            Color.web("#FFD700"), // gold
            Color.web("#800080"), // purple
            Color.web("#00CED1"), // dark turquoise
            Color.web("#FF69B4")  // hot pink
    };
    private Color selectedColor = Color.web("#8B1A1A");
    private boolean eraseMode = false;
    // Canvas drawing variables
    private Canvas canvas;
    private GraphicsContext gc;

    private Stage primaryStage;

    private SymMode symMode = SymMode.NONE;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setTitle("Vyshyvanka | Author: Mokliak Vyacheslav");
        grid = new Color[rows][cols];
        stage.setWidth(1200);
        stage.setHeight(830);
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);


        root.setCenter(buildCanvasPane());
        root.setTop(buildToolbar());
        root.setCenter(buildCanvasPane());
        root.setTop(buildToolbar());
        root.setLeft(buildPalette());
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
        Color paint = eraseMode ? null : selectedColor;
        applyWithSymmetry(r, c, paint);
        redraw();
    }

    private void paintCell(int r, int c, Color color) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return;
        grid[r][c] = color;
    }

    private void applyWithSymmetry(int r, int c, Color color){
        paintCell(r, c, color);

        switch (symMode) {
            case HORIZONTAL -> paintCell(r, cols - 1 - c, color);
            case VERTICAL -> paintCell(rows - 1 - r, c, color);
            case BOTH -> {
                paintCell(r, cols - 1 - c, color);
                paintCell(rows - 1 - r, c, color);
                paintCell(rows - 1 - r, cols - 1 - c, color);
            }
            case NONE  -> {}
        }
    }

    private HBox buildToolbar() {
        HBox bar = new HBox(8);
        bar.setPadding(new Insets(8, 12, 8, 12));
        bar.setAlignment(Pos.CENTER_LEFT);

        Button btnNew = new Button("Новий");
        btnNew.setOnAction(e -> {
            grid = new Color[rows][cols];
            redraw();
        });

        Button btnGenName = new Button("Ім'я");
        btnGenName.setOnAction(e -> {
            grid = new Color[rows][cols];
            redraw();
            // generateName() — додамо пізніше
        });
        Button btnSave = new Button("Зберегти");
        btnSave.setOnAction(e -> savePNG());

        // симетрія
        ToggleGroup tgSym = new ToggleGroup();

        ToggleButton tbNone  = new ToggleButton("Немає");
        ToggleButton tbHoriz = new ToggleButton("↔");
        ToggleButton tbVert  = new ToggleButton("↕");
        ToggleButton tbBoth  = new ToggleButton("✦");

        tbNone.setToggleGroup(tgSym);
        tbHoriz.setToggleGroup(tgSym);
        tbVert.setToggleGroup(tgSym);
        tbBoth.setToggleGroup(tgSym);
        tbNone.setSelected(true);

        tbNone.setOnAction(e  -> symMode = SymMode.NONE);
        tbHoriz.setOnAction(e -> symMode = SymMode.HORIZONTAL);
        tbVert.setOnAction(e  -> symMode = SymMode.VERTICAL);
        tbBoth.setOnAction(e  -> symMode = SymMode.BOTH);

        ToggleButton tbErase = new ToggleButton("Гумка");
        tbErase.setOnAction(e -> eraseMode = tbErase.isSelected());

        bar.getChildren().addAll(
                btnNew, btnGenName,
                new Separator(),
                new Label("Симетрія:"),
                tbNone, tbHoriz, tbVert, tbBoth,
                new Separator(),
                tbErase,
                btnSave
        );

        return bar;
    }

    private VBox buildPalette() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(12, 10, 12, 10));
        panel.setAlignment(Pos.TOP_CENTER);

        Label lb1 = new Label("Colors");
        Rectangle preview = new Rectangle(36, 36, selectedColor);

        GridPane swatches  = new GridPane();
        swatches.setHgap(4);
        swatches.setVgap(4);

        for(int i = 0; i < PALETTE.length; i++){
            Color c = PALETTE[i];
            Rectangle r = new Rectangle(22, 22, c);
            r.setOnMouseClicked(e -> {
                selectedColor = c;
                preview.setFill(c);
                eraseMode = false;
            });

            swatches.add(r, i % 2, i / 2);
        }
        ColorPicker picker = new ColorPicker(selectedColor);
        picker.setOnAction(e -> {
            selectedColor = picker.getValue();
            preview.setFill(selectedColor);
        });

        panel.getChildren().addAll(lb1, preview, swatches, new Separator(), picker);
        return panel;
    }

    private void savePNG() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save as PNG");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        fc.setInitialFileName("vyshyvanka.png");
        File f = fc.showSaveDialog(primaryStage);
        if (f == null) return;

        WritableImage img = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, img);
        try{
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", f);
        } catch (IOException e) {
            System.out.println("Error saving image to PNG: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}