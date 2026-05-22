import javafx.scene.paint.Color;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Controller for drawing operations.
 * Handles mouse input translation to grid painting with symmetry support.
 */
public class DrawingController {
    
    private static final int CELL_SIZE = 20;
    private static final int MAX_HISTORY = 50;

    private IGridModel gridModel;
    private ColorService colorService;
    private SymmetryService symmetryService;
    private CanvasRenderer canvasRenderer;
    private final Deque<Color[][]> undoStack = new ArrayDeque<>();
    private final Deque<Color[][]> redoStack = new ArrayDeque<>();
    private boolean strokeInProgress = false;

    private SymMode currentSymMode = SymMode.NONE;
    private boolean eraseMode = false;
    
    public DrawingController(IGridModel gridModel, ColorService colorService,
                              SymmetryService symmetryService, CanvasRenderer canvasRenderer) {
        this.gridModel = gridModel;
        this.colorService = colorService;
        this.symmetryService = symmetryService;
        this.canvasRenderer = canvasRenderer;
    }
    
    public void beginStroke() {
        if (strokeInProgress) {
            return;
        }
        captureStateForUndo();
        strokeInProgress = true;
    }

    public void endStroke() {
        strokeInProgress = false;
    }

    /**
     * Handle mouse click/drag at canvas coordinates.
     * Translates mouse position to grid cell and applies drawing with symmetry.
     */
    public void handleDraw(double mouseX, double mouseY) {
        int col = (int) (mouseX / CELL_SIZE);
        int row = (int) (mouseY / CELL_SIZE);
        
        if (row < 0 || row >= gridModel.getRows() || col < 0 || col >= gridModel.getCols()) {
            return;
        }
        
        Color paintColor = eraseMode ? null : colorService.getSelectedColor();
        symmetryService.applyWithSymmetry(gridModel, row, col, paintColor, currentSymMode);
        canvasRenderer.redraw();
    }
    
    public void captureStateForUndo() {
        undoStack.push(copyGrid(gridModel.getGridData()));
        if (undoStack.size() > MAX_HISTORY) {
            undoStack.removeLast();
        }
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        redoStack.push(copyGrid(gridModel.getGridData()));
        gridModel.setGridData(undoStack.pop());
        canvasRenderer.redraw();
    }

    public void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        undoStack.push(copyGrid(gridModel.getGridData()));
        gridModel.setGridData(redoStack.pop());
        canvasRenderer.redraw();
    }

    private Color[][] copyGrid(Color[][] source) {
        if (source == null || source.length == 0 || source[0].length == 0) {
            return new Color[0][0];
        }
        Color[][] copy = new Color[source.length][source[0].length];
        for (int r = 0; r < source.length; r++) {
            System.arraycopy(source[r], 0, copy[r], 0, source[r].length);
        }
        return copy;
    }

    public void setSymmetryMode(SymMode mode) {
        this.currentSymMode = mode;
    }
    
    public void setEraseMode(boolean erase) {
        this.eraseMode = erase;
    }
    
    public SymMode getSymmetryMode() {
        return currentSymMode;
    }
    
    public boolean isEraseMode() {
        return eraseMode;
    }
}
