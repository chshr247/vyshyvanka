import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renders the grid onto a Canvas.
 * Responsible for all visual drawing operations.
 */
public class CanvasRenderer implements ICanvasRenderer {
    
    private static final int CELL_SIZE = 20;
    
    private Canvas canvas;
    private GraphicsContext gc;
    private IGridModel gridModel;
    private ColorService colorService;
    
    public CanvasRenderer(IGridModel gridModel, ColorService colorService) {
        this.gridModel = gridModel;
        this.colorService = colorService;
    }
    
    @Override
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }
    
    @Override
    public Canvas getCanvas() {
        return canvas;
    }
    
    @Override
    public void redraw() {
        if (gc == null || canvas == null) {
            return;
        }
        
        // Clear background
        gc.setFill(Color.web("#ffffff"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Draw filled cells with diagonals
        Color[][] gridData = gridModel.getGridData();
        for (int i = 0; i < gridModel.getRows(); i++) {
            for (int j = 0; j < gridModel.getCols(); j++) {
                if (gridData[i][j] != null) {
                    drawCell(i, j, gridData[i][j]);
                }
            }
        }
        
        // Draw grid lines
        drawGridLines();
    }
    
    private void drawCell(int row, int col, Color color) {
        double x = col * CELL_SIZE;
        double y = row * CELL_SIZE;
        
        gc.setFill(color);
        gc.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
        
        gc.setStroke(colorService.getDarkerVariant(color));
        gc.setLineWidth(0.5);
        gc.strokeLine(x + 2, y + 2, x + CELL_SIZE - 2, y + CELL_SIZE - 2);
        gc.strokeLine(x + CELL_SIZE - 2, y + 2, x + 2, y + CELL_SIZE - 2);
    }
    
    private void drawGridLines() {
        gc.setStroke(Color.web("#cccccc"));
        gc.setLineWidth(0.5);
        
        int rows = gridModel.getRows();
        int cols = gridModel.getCols();
        
        for (int r = 0; r <= rows; r++) {
            gc.strokeLine(0, r * CELL_SIZE, cols * CELL_SIZE, r * CELL_SIZE);
        }
        for (int c = 0; c <= cols; c++) {
            gc.strokeLine(c * CELL_SIZE, 0, c * CELL_SIZE, rows * CELL_SIZE);
        }
    }
    
    public int getCellSize() {
        return CELL_SIZE;
    }
}

