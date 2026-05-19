import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.Optional;

/**
 * Builds and manages the sidebar/palette panel UI component.
 * Encapsulates color palette, drawing tools, symmetry modes, and grid dimensions.
 */
public class PalettePanel {
    
    private VBox panel;
    private Rectangle preview;
    private ColorPicker picker;
    private TextField tfCols;
    private TextField tfRows;
    
    private ToggleButton tbDraw;
    private ToggleButton tbErase;
    private ToggleButton tbNone;
    private ToggleButton tbHoriz;
    private ToggleButton tbVert;
    private ToggleButton tbBoth;
    
    private ColorService colorService;
    private IGridModel gridModel;
    
    private Runnable onColorChanged;
    private Runnable onDrawModeChanged;
    private Runnable onEraseModeChanged;
    private Runnable onSymmetryModeChanged;
    private Runnable onResizeGrid;
    
    private final int MAX_COLS;
    private final int MAX_ROWS;
    
    public PalettePanel(ColorService colorService, IGridModel gridModel, int maxCols, int maxRows) {
        this.colorService = colorService;
        this.gridModel = gridModel;
        this.MAX_COLS = maxCols;
        this.MAX_ROWS = maxRows;
        buildPanel();
    }
    
    private void buildPanel() {
        panel = new VBox(12);
        panel.setPadding(new Insets(24, 16, 24, 16));
        panel.setPrefWidth(240);
        panel.getStyleClass().add("sidebar");
        
        // Workspace section
        Label wsLabel = new Label("WORKSPACE");
        wsLabel.getStyleClass().add("section-label");
        Label wsTitle = new Label("Embroidery Grid v1.0");
        wsTitle.getStyleClass().add("sidebar-title");
        
        // Drawing tools section
        Label drawLabel = new Label("DRAWING TOOLS");
        drawLabel.getStyleClass().add("section-label");
        
        ToggleGroup tgDraw = new ToggleGroup();
        tbDraw = new ToggleButton("✏ Draw");
        tbErase = new ToggleButton("⌫ Erase");
        tbDraw.setToggleGroup(tgDraw);
        tbErase.setToggleGroup(tgDraw);
        tbDraw.setSelected(true);
        tbDraw.getStyleClass().add("tool-btn");
        tbErase.getStyleClass().add("tool-btn");
        tbDraw.setMaxWidth(Double.MAX_VALUE);
        tbErase.setMaxWidth(Double.MAX_VALUE);
        
        tbDraw.setOnAction(e -> {
            if (onDrawModeChanged != null) {
                onDrawModeChanged.run();
            }
        });
        tbErase.setOnAction(e -> {
            if (onEraseModeChanged != null) {
                onEraseModeChanged.run();
            }
        });
        
        HBox toolBox = new HBox(4, tbDraw, tbErase);
        HBox.setHgrow(tbDraw, Priority.ALWAYS);
        HBox.setHgrow(tbErase, Priority.ALWAYS);
        toolBox.getStyleClass().add("tool-group");
        toolBox.setPadding(new Insets(4));
        
        // Color palette section
        Label colorLabel = new Label("COLOR PALETTE");
        colorLabel.getStyleClass().add("section-label");
        
        preview = new Rectangle(36, 36, colorService.getSelectedColor());
        preview.setArcWidth(8);
        preview.setArcHeight(8);
        
        GridPane swatches = new GridPane();
        swatches.setHgap(6);
        swatches.setVgap(6);
        for (int i = 0; i < colorService.getPalette().length; i++) {
            Color c = colorService.getPalette()[i];
            Rectangle r = new Rectangle(28, 28, c);
            r.setArcWidth(6);
            r.setArcHeight(6);
            r.getStyleClass().add("color-swatch");
            r.setOnMouseClicked(e -> {
                colorService.setSelectedColor(c);
                preview.setFill(c);
                picker.setValue(c);
                tbDraw.setSelected(true);
                if (onColorChanged != null) {
                    onColorChanged.run();
                }
            });
            swatches.add(r, i % 4, i / 4);
        }
        
        picker = new ColorPicker(colorService.getSelectedColor());
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.getStyleClass().add("color-picker");
        picker.setOnAction(e -> {
            Color newColor = picker.getValue();
            colorService.setSelectedColor(newColor);
            preview.setFill(newColor);
            if (onColorChanged != null) {
                onColorChanged.run();
            }
        });
        
        // Symmetry mode section
        Label symLabel = new Label("SYMMETRY MODE");
        symLabel.getStyleClass().add("section-label");
        
        ToggleGroup tgSym = new ToggleGroup();
        tbNone = new ToggleButton("⊘ None");
        tbHoriz = new ToggleButton("↔ Horizontal");
        tbVert = new ToggleButton("↕ Vertical");
        tbBoth = new ToggleButton("✦ Both");
        
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(50);
        
        for (ToggleButton tb : new ToggleButton[]{tbNone, tbHoriz, tbVert, tbBoth}) {
            tb.setToggleGroup(tgSym);
            tb.getStyleClass().add("sym-btn");
            tb.setMaxWidth(Double.MAX_VALUE);
            tb.setOnAction(e -> {
                if (onSymmetryModeChanged != null) {
                    onSymmetryModeChanged.run();
                }
            });
        }
        tbNone.setSelected(true);
        
        GridPane symGrid = new GridPane();
        symGrid.setHgap(6);
        symGrid.setVgap(6);
        symGrid.add(tbNone, 0, 0);
        symGrid.add(tbHoriz, 1, 0);
        symGrid.add(tbVert, 0, 1);
        symGrid.add(tbBoth, 1, 1);
        symGrid.getColumnConstraints().addAll(cc, cc);
        
        // Grid dimensions section
        Label dimLabel = new Label("GRID DIMENSIONS");
        dimLabel.getStyleClass().add("section-label");
        
        Label wLabel = new Label("WIDTH (CELLS)");
        wLabel.getStyleClass().add("dim-label");
        Label hLabel = new Label("HEIGHT (CELLS)");
        hLabel.getStyleClass().add("dim-label");
        
        tfCols = new TextField(String.valueOf(gridModel.getCols()));
        tfRows = new TextField(String.valueOf(gridModel.getRows()));
        tfCols.getStyleClass().add("dim-field");
        tfRows.getStyleClass().add("dim-field");
        clampFieldMax(tfCols, MAX_COLS);
        clampFieldMax(tfRows, MAX_ROWS);
        
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setPercentWidth(50);
        
        GridPane dimGrid = new GridPane();
        dimGrid.setHgap(8);
        dimGrid.setVgap(4);
        dimGrid.add(wLabel, 0, 0);
        dimGrid.add(hLabel, 1, 0);
        dimGrid.add(tfCols, 0, 1);
        dimGrid.add(tfRows, 1, 1);
        dimGrid.getColumnConstraints().addAll(cc2, cc2);
        
        Button btnResize = new Button("⊡ Resize Grid");
        btnResize.setMaxWidth(Double.MAX_VALUE);
        btnResize.getStyleClass().add("btn-ghost");
        btnResize.setOnAction(e -> {
            if (onResizeGrid != null) {
                onResizeGrid.run();
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
    }
    
    private void clampFieldMax(TextField field, int max) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
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
    
    public VBox getPanel() {
        return panel;
    }
    
    public boolean isEraseMode() {
        return tbErase.isSelected();
    }
    
    public SymMode getSelectedSymmetryMode() {
        if (tbHoriz.isSelected()) {
            return SymMode.HORIZONTAL;
        } else if (tbVert.isSelected()) {
            return SymMode.VERTICAL;
        } else if (tbBoth.isSelected()) {
            return SymMode.BOTH;
        }
        return SymMode.NONE;
    }
    
    public int getGridCols() {
        try {
            return Integer.parseInt(tfCols.getText().trim());
        } catch (NumberFormatException e) {
            return gridModel.getCols();
        }
    }
    
    public int getGridRows() {
        try {
            return Integer.parseInt(tfRows.getText().trim());
        } catch (NumberFormatException e) {
            return gridModel.getRows();
        }
    }
    
    public void updateGridDimensions() {
        tfCols.setText(String.valueOf(gridModel.getCols()));
        tfRows.setText(String.valueOf(gridModel.getRows()));
    }
    
    // Event handler setters
    public void setOnColorChanged(Runnable handler) {
        this.onColorChanged = handler;
    }
    
    public void setOnDrawModeChanged(Runnable handler) {
        this.onDrawModeChanged = handler;
    }
    
    public void setOnEraseModeChanged(Runnable handler) {
        this.onEraseModeChanged = handler;
    }
    
    public void setOnSymmetryModeChanged(Runnable handler) {
        this.onSymmetryModeChanged = handler;
    }
    
    public void setOnResizeGrid(Runnable handler) {
        this.onResizeGrid = handler;
    }
}

