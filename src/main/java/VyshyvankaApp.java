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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

public class VyshyvankaApp extends Application {

    private static final int CELL_SIZE = 20;
    private static final int DEFAULT_COLS = 41;
    private static final int DEFAULT_ROWS = 31;
    private static final int MAX_COLS = 45;
    private static final int MAX_ROWS = 34;

    private int cols = DEFAULT_COLS;
    private int rows = DEFAULT_ROWS;

    private int tileW = 10;
    private int tileH = 10;

    public Color[][] grid;
    private static final Color[] PALETTE = {
            Color.web("#8B1A1A"),
            Color.web("#FF4500"),
            Color.web("#228B22"),
            Color.web("#1E90FF"),
            Color.web("#FFD700"),
            Color.web("#800080"),
            Color.web("#00CED1"),
            Color.web("#FF69B4")
    };
    private Color selectedColor = Color.web("#8B1A1A");
    private boolean eraseMode = false;

    private Canvas canvas;
    private GraphicsContext gc;
    private Stage primaryStage;
    private SymMode symMode = SymMode.NONE;

    private Rectangle preview;
    private ColorPicker picker;

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
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        root.setTop(buildToolbar());
        root.setLeft(buildPalette());
        root.setCenter(buildCanvasPane());

        stage.setScene(scene);
        stage.show();
        redraw();
        loadPNG("images/viacheslav.png");
    }

    private StackPane buildCanvasPane() {
        canvas = new Canvas(cols * CELL_SIZE + 1, rows * CELL_SIZE + 1);
        gc = canvas.getGraphicsContext2D();
        canvas.setOnMousePressed((MouseEvent e) -> handleDraw(e.getX(), e.getY()));
        canvas.setOnMouseDragged((MouseEvent e) -> handleDraw(e.getX(), e.getY()));
        StackPane pane = new StackPane(canvas);
        pane.setStyle("-fx-background-color: #f7f6f9; -fx-background-radius: 16;");
        pane.setPadding(new Insets(20));
        return pane;
    }

    private void redraw() {
        gc.setFill(Color.web("#ffffff"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != null) {
                    double x = j * CELL_SIZE;
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

        gc.setStroke(Color.web("#cccccc"));
        gc.setLineWidth(0.5);
        for (int r = 0; r <= rows; r++)
            gc.strokeLine(0, r * CELL_SIZE, cols * CELL_SIZE, r * CELL_SIZE);
        for (int c = 0; c <= cols; c++)
            gc.strokeLine(c * CELL_SIZE, 0, c * CELL_SIZE, rows * CELL_SIZE);
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

    private void applyWithSymmetry(int r, int c, Color color) {
        paintCell(r, c, color);
        switch (symMode) {
            case HORIZONTAL -> paintCell(r, cols - 1 - c, color);
            case VERTICAL   -> paintCell(rows - 1 - r, c, color);
            case BOTH -> {
                paintCell(r, cols - 1 - c, color);
                paintCell(rows - 1 - r, c, color);
                paintCell(rows - 1 - r, cols - 1 - c, color);
            }
            case NONE -> {}
        }
    }

    private HBox buildToolbar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(0, 16, 0, 16));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPrefHeight(64);
        bar.getStyleClass().add("toolbar");

        Label logo = new Label("⊞");
        logo.getStyleClass().add("toolbar-logo");

        Label title = new Label("Vyshyvanka Editor - Mokliak Viacheslav");
        title.getStyleClass().add("toolbar-title");

        HBox left = new HBox(10, logo, title);
        left.setAlignment(Pos.CENTER_LEFT);

        Button btnNew     = new Button("New");
        Button btnOpen    = new Button("Open File");
        Button btnTile    = new Button("Tile");
        Button btnGenName = new Button("Generate Name");
        Button btnSave    = new Button("Save as PNG");

        btnNew.setOnAction(e -> { grid = new Color[rows][cols]; redraw(); });
        btnOpen.setOnAction(e -> openPNG());
        btnTile.setOnAction(e -> showTileDialog());
        btnGenName.setOnAction(e -> { grid = new Color[rows][cols]; redraw(); loadPNG("images/viacheslav.png"); });
        btnSave.setOnAction(e -> savePNG());

        btnNew.getStyleClass().add("btn-ghost");
        btnOpen.getStyleClass().add("btn-ghost");
        btnTile.getStyleClass().add("btn-ghost");
        btnGenName.getStyleClass().add("btn-ghost");
        btnSave.getStyleClass().add("btn-primary");

        HBox right = new HBox(4, btnNew, btnOpen, btnTile, btnGenName, btnSave);
        right.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(left, spacer, right);
        return bar;
    }

    private VBox buildPalette() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(24, 16, 24, 16));
        panel.setPrefWidth(240);
        panel.getStyleClass().add("sidebar");

        Label wsLabel = new Label("WORKSPACE");
        wsLabel.getStyleClass().add("section-label");
        Label wsTitle = new Label("Embroidery Grid  v1.0");
        wsTitle.getStyleClass().add("sidebar-title");

        Label drawLabel = new Label("DRAWING TOOLS");
        drawLabel.getStyleClass().add("section-label");

        ToggleGroup tgDraw = new ToggleGroup();
        ToggleButton tbDraw  = new ToggleButton("✏ Draw");
        ToggleButton tbErase = new ToggleButton("⌫ Erase");
        tbDraw.setToggleGroup(tgDraw);
        tbErase.setToggleGroup(tgDraw);
        tbDraw.setSelected(true);
        tbDraw.getStyleClass().add("tool-btn");
        tbErase.getStyleClass().add("tool-btn");
        tbDraw.setMaxWidth(Double.MAX_VALUE);
        tbErase.setMaxWidth(Double.MAX_VALUE);
        tbDraw.setOnAction(e -> eraseMode = false);
        tbErase.setOnAction(e -> eraseMode = tbErase.isSelected());

        HBox toolBox = new HBox(4, tbDraw, tbErase);
        HBox.setHgrow(tbDraw, Priority.ALWAYS);
        HBox.setHgrow(tbErase, Priority.ALWAYS);
        toolBox.getStyleClass().add("tool-group");
        toolBox.setPadding(new Insets(4));

        Label colorLabel = new Label("COLOR PALETTE");
        colorLabel.getStyleClass().add("section-label");

        preview = new Rectangle(36, 36, selectedColor);
        preview.setArcWidth(8);
        preview.setArcHeight(8);

        GridPane swatches = new GridPane();
        swatches.setHgap(6);
        swatches.setVgap(6);
        for (int i = 0; i < PALETTE.length; i++) {
            Color c = PALETTE[i];
            Rectangle r = new Rectangle(28, 28, c);
            r.setArcWidth(6);
            r.setArcHeight(6);
            r.setOnMouseClicked(e -> {
                selectedColor = c;
                preview.setFill(c);
                picker.setValue(c);
                eraseMode = false;
                tbDraw.setSelected(true);
            });
            swatches.add(r, i % 4, i / 4);
        }

        picker = new ColorPicker(selectedColor);
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.getStyleClass().add("color-picker");
        picker.setOnAction(e -> {
            selectedColor = picker.getValue();
            preview.setFill(selectedColor);
        });

        Label symLabel = new Label("SYMMETRY MODE");
        symLabel.getStyleClass().add("section-label");

        ToggleGroup tgSym = new ToggleGroup();
        ToggleButton tbNone  = new ToggleButton("⊘ None");
        ToggleButton tbHoriz = new ToggleButton("↔ Horiz");
        ToggleButton tbVert  = new ToggleButton("↕ Vertical");
        ToggleButton tbBoth  = new ToggleButton("✦ Both");

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);

        for (ToggleButton tb : new ToggleButton[]{tbNone, tbHoriz, tbVert, tbBoth}) {
            tb.setToggleGroup(tgSym);
            tb.getStyleClass().add("sym-btn");
            tb.setMaxWidth(Double.MAX_VALUE);
        }
        tbNone.setSelected(true);
        tbNone.setOnAction(e  -> symMode = SymMode.NONE);
        tbHoriz.setOnAction(e -> symMode = SymMode.HORIZONTAL);
        tbVert.setOnAction(e  -> symMode = SymMode.VERTICAL);
        tbBoth.setOnAction(e  -> symMode = SymMode.BOTH);

        GridPane symGrid = new GridPane();
        symGrid.setHgap(6);
        symGrid.setVgap(6);
        symGrid.add(tbNone,  0, 0); symGrid.add(tbHoriz, 1, 0);
        symGrid.add(tbVert,  0, 1); symGrid.add(tbBoth,  1, 1);
        symGrid.getColumnConstraints().addAll(cc, cc);

        Label dimLabel = new Label("GRID DIMENSIONS");
        dimLabel.getStyleClass().add("section-label");

        Label wLabel = new Label("WIDTH (CELLS)");
        wLabel.getStyleClass().add("dim-label");
        Label hLabel = new Label("HEIGHT (CELLS)");
        hLabel.getStyleClass().add("dim-label");

        TextField tfCols = new TextField(String.valueOf(cols));
        TextField tfRows = new TextField(String.valueOf(rows));
        tfCols.getStyleClass().add("dim-field");
        tfRows.getStyleClass().add("dim-field");
        clampFieldMax(tfCols, MAX_COLS);
        clampFieldMax(tfRows, MAX_ROWS);

        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(50);

        GridPane dimGrid = new GridPane();
        dimGrid.setHgap(8);
        dimGrid.setVgap(4);
        dimGrid.add(wLabel, 0, 0); dimGrid.add(hLabel, 1, 0);
        dimGrid.add(tfCols, 0, 1); dimGrid.add(tfRows, 1, 1);
        dimGrid.getColumnConstraints().addAll(cc2, cc2);

        Button btnResize = new Button("⊡  Resize Grid");
        btnResize.setMaxWidth(Double.MAX_VALUE);
        btnResize.getStyleClass().add("btn-ghost");
        btnResize.setOnAction(e -> {
            try {
                int newCols = Math.max(5, Math.min(MAX_COLS, Integer.parseInt(tfCols.getText().trim())));
                int newRows = Math.max(5, Math.min(MAX_ROWS, Integer.parseInt(tfRows.getText().trim())));
                cols = newCols;
                rows = newRows;
                grid = new Color[rows][cols];
                canvas.setWidth(cols * CELL_SIZE + 1);
                canvas.setHeight(rows * CELL_SIZE + 1);
                gc = canvas.getGraphicsContext2D();
                tfCols.setText(String.valueOf(cols));
                tfRows.setText(String.valueOf(rows));
                redraw();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Введіть коректні числа").showAndWait();
            }
        });

        panel.getChildren().addAll(
                wsLabel, wsTitle,
                new Separator(),
                drawLabel, toolBox,
                new Separator(),
                colorLabel, preview, swatches, picker,
                new Separator(),
                symLabel, symGrid,
                new Separator(),
                dimLabel, dimGrid, btnResize
        );

        return panel;
    }

    private void showTileDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Дублювання фрагменту");
        dialog.setHeaderText("Розмір базового фрагменту (лівий верхній кут)");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField tfW = new TextField(String.valueOf(tileW));
        TextField tfH = new TextField(String.valueOf(tileH));

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.setPadding(new Insets(12));
        gp.add(new Label("Ширина фрагменту:"), 0, 0);
        gp.add(tfW, 1, 0);
        gp.add(new Label("Висота фрагменту:"), 0, 1);
        gp.add(tfH, 1, 1);

        dialog.getDialogPane().setContent(gp);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        try {
            tileW = Math.max(1, Math.min(cols, Integer.parseInt(tfW.getText().trim())));
            tileH = Math.max(1, Math.min(rows, Integer.parseInt(tfH.getText().trim())));
            tilePattern();
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Введіть коректні числа").showAndWait();
        }
    }

    private void openPNG() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open PNG");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        File f = fc.showOpenDialog(primaryStage);
        if (f == null) return;
        grid = new Color[rows][cols];
        loadPNG(f.getAbsolutePath());
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
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", f);
        } catch (IOException e) {
            System.out.println("Error saving image to PNG: " + e.getMessage());
        }
    }

    private void loadPNG(String path) {
        try {
            BufferedImage bi = ImageIO.read(new File(path));
            if (bi == null) return;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int px = c * CELL_SIZE + CELL_SIZE / 2;
                    int py = r * CELL_SIZE + CELL_SIZE / 2;

                    if (px < bi.getWidth() && py < bi.getHeight()) {
                        int rgb = bi.getRGB(px, py);
                        int red   = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8)  & 0xFF;
                        int blue  = (rgb)       & 0xFF;
                        int alpha = (rgb >> 24) & 0xFF;

                        if (alpha > 10) {
                            Color col = Color.rgb(red, green, blue);
                            double brightness = col.getRed() * 0.299
                                    + col.getGreen() * 0.587
                                    + col.getBlue()  * 0.114;
                            if (brightness < 0.85) grid[r][c] = col;
                        }
                    }
                }
            }
            redraw();
        } catch (IOException e) {
            System.out.println("Error loading PNG: " + e.getMessage());
        }
    }

    private void tilePattern() {
        Color[][] fragment = new Color[tileH][tileW];
        for (int r = 0; r < tileH && r < rows; r++)
            for (int c = 0; c < tileW && c < cols; c++)
                fragment[r][c] = grid[r][c];

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = fragment[r % tileH][c % tileW];

        redraw();
    }

    private void clampFieldMax(TextField field, int max) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;
            String digitsOnly = newValue.replaceAll("\\D", "");
            if (!digitsOnly.equals(newValue)) {
                field.setText(digitsOnly);
                return;
            }
            try {
                int value = Integer.parseInt(digitsOnly);
                if (value > max) {
                    field.setText(String.valueOf(max));
                }
            } catch (NumberFormatException ignored) {
                field.setText(String.valueOf(max));
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}