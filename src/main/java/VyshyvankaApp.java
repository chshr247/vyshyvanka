import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class VyshyvankaApp extends Application {

    private static final int DEFAULT_COLS = 41;
    private static final int DEFAULT_ROWS = 31;
    private static final int MAX_COLS = 45;
    private static final int MAX_ROWS = 34;
    private static final int CELL_SIZE = 20;

    // Core model and services
    private IGridModel gridModel;
    private ColorService colorService;
    private SymmetryService symmetryService;
    private TilePatternService tileService;
    private DrawingController drawingController;

    // UI components
    private CanvasRenderer canvasRenderer;
    private PngFileService pngService;
    private DrawingToolsUI toolbarUI;
    private PalettePanel palettePanel;

    // Canvas reference
    private Canvas canvas;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        stage.setTitle("Vyshyvanka | Author: Mokliak Vyacheslav");
        stage.setWidth(1200);
        stage.setHeight(830);
        stage.setResizable(false);

        // Initialize all services and models
        initializeServices();

        // Build UI
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
                drawingController::undo
        );
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                drawingController::redo
        );

        root.setTop(toolbarUI.getToolbar());
        root.setLeft(palettePanel.getPanel());
        root.setCenter(buildCanvasPane());

        setupEventHandlers();

        stage.setScene(scene);
        stage.show();
        canvasRenderer.redraw();
        pngService.loadPNG("images/viacheslav.png");
        canvasRenderer.redraw();
    }

    private void initializeServices() {
        // Initialize models and core services
        gridModel = new GridModel(DEFAULT_ROWS, DEFAULT_COLS);
        colorService = new ColorService();
        symmetryService = new SymmetryService();
        tileService = new TilePatternService(gridModel, symmetryService);

        // Initialize canvas renderer (will set canvas later)
        canvasRenderer = new CanvasRenderer(gridModel, colorService);

        // Initialize file service
        pngService = new PngFileService(gridModel, null, colorService);

        // Initialize drawing controller
        drawingController = new DrawingController(gridModel, colorService, symmetryService, canvasRenderer);

        // Initialize UI builders
        toolbarUI = new DrawingToolsUI();
        palettePanel = new PalettePanel(colorService, gridModel, MAX_COLS, MAX_ROWS);
    }

    private void setupEventHandlers() {
        // Toolbar event handlers
        toolbarUI.setOnUndoPressed(drawingController::undo);
        toolbarUI.setOnRedoPressed(drawingController::redo);
        toolbarUI.setOnNewPressed(() -> {
            drawingController.captureStateForUndo();
            gridModel.clearGrid();
            canvasRenderer.redraw();
        });

        toolbarUI.setOnOpenPressed(this::openPNG);
        toolbarUI.setOnTilePressed(this::showTileDialog);
        toolbarUI.setOnGenerateNamePressed(this::generateFromName);
        toolbarUI.setOnSavePressed(this::savePNG);

        // Palette event handlers
        palettePanel.setOnDrawModeChanged(() -> drawingController.setEraseMode(false));
        palettePanel.setOnEraseModeChanged(() -> drawingController.setEraseMode(palettePanel.isEraseMode()));
        palettePanel.setOnSymmetryModeChanged(() -> {
            SymMode mode = palettePanel.getSelectedSymmetryMode();
            drawingController.setSymmetryMode(mode);
            tileService.setSymmetryMode(mode);
        });
        palettePanel.setOnResizeGrid(this::resizeGrid);
    }

    private StackPane buildCanvasPane() {
        canvas = new Canvas(DEFAULT_COLS * CELL_SIZE + 1, DEFAULT_ROWS * CELL_SIZE + 1);
        canvasRenderer.setCanvas(canvas);
        pngService = new PngFileService(gridModel, canvas, colorService);

        canvas.setOnMousePressed((MouseEvent e) -> {
            drawingController.beginStroke();
            drawingController.handleDraw(e.getX(), e.getY());
        });
        canvas.setOnMouseDragged((MouseEvent e) -> drawingController.handleDraw(e.getX(), e.getY()));
        canvas.setOnMouseReleased((MouseEvent e) -> drawingController.endStroke());

        StackPane pane = new StackPane(canvas);
        pane.setStyle("-fx-background-color: #f7f6f9; -fx-background-radius: 16;");
        pane.setPadding(new Insets(20));
        return pane;
    }

    private void showTileDialog() {
        Optional<int[]> result = DialogFactory.showTileDialog(drawingController.getSymmetryMode());
        if (result.isEmpty()) {
            return;
        }

        int[] dimensions = result.get();
        drawingController.captureStateForUndo();
        tileService.tilePattern(dimensions[0], dimensions[1]);
        canvasRenderer.redraw();
    }

    private void generateFromName() {
        Optional<String> selected = DialogFactory.showGenerateFromNameDialog();
        if (selected.isEmpty()) {
            return;
        }

        drawingController.captureStateForUndo();
        gridModel.clearGrid();
        canvasRenderer.redraw();
        pngService.loadPNG("images/" + selected.get() + ".png");
        canvasRenderer.redraw();
    }

    private void openPNG() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open PNG");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        File f = fc.showOpenDialog(primaryStage);
        if (f == null) {
            return;
        }

        drawingController.captureStateForUndo();
        gridModel.clearGrid();
        pngService.loadPNG(f.getAbsolutePath());
        canvasRenderer.redraw();
    }

    private void savePNG() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save as PNG");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image", "*.png"));
        fc.setInitialFileName("vyshyvanka.png");
        File f = fc.showSaveDialog(primaryStage);
        if (f == null) {
            return;
        }

        pngService.savePNG(f.getAbsolutePath());
    }

    private void resizeGrid() {
        try {
            int newCols = Math.max(5, Math.min(MAX_COLS, palettePanel.getGridCols()));
            int newRows = Math.max(5, Math.min(MAX_ROWS, palettePanel.getGridRows()));

            drawingController.captureStateForUndo();
            gridModel.resizeGrid(newRows, newCols);
            canvas.setWidth(newCols * CELL_SIZE + 1);
            canvas.setHeight(newRows * CELL_SIZE + 1);

            palettePanel.updateGridDimensions();
            canvasRenderer.redraw();
        } catch (NumberFormatException ex) {
            DialogFactory.showErrorAlert("Error", "Please enter valid numbers");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}