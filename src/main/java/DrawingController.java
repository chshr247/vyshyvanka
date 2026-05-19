import javafx.scene.paint.Color;

/**
 * Controller for drawing operations.
 * Handles mouse input translation to grid painting with symmetry support.
 */
public class DrawingController {
    
    private static final int CELL_SIZE = 20;
    
    private IGridModel gridModel;
    private ColorService colorService;
    private SymmetryService symmetryService;
    private CanvasRenderer canvasRenderer;
    
    private SymMode currentSymMode = SymMode.NONE;
    private boolean eraseMode = false;
    
    public DrawingController(IGridModel gridModel, ColorService colorService,
                              SymmetryService symmetryService, CanvasRenderer canvasRenderer) {
        this.gridModel = gridModel;
        this.colorService = colorService;
        this.symmetryService = symmetryService;
        this.canvasRenderer = canvasRenderer;
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

