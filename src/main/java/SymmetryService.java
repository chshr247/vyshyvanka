import javafx.scene.paint.Color;

/**
 * Service for applying symmetry transformations to the grid.
 * Applies color to cells according to the selected symmetry mode.
 */
public class SymmetryService {
    
    /**
     * Apply color to a cell and its symmetric counterparts based on symmetric mode.
     */
    public void applyWithSymmetry(IGridModel grid, int row, int col, Color color, SymMode mode) {
        grid.paintCell(row, col, color);
        
        int maxRow = grid.getRows() - 1;
        int maxCol = grid.getCols() - 1;
        
        switch (mode) {
            case HORIZONTAL -> grid.paintCell(row, maxCol - col, color);
            case VERTICAL -> grid.paintCell(maxRow - row, col, color);
            case BOTH -> {
                grid.paintCell(row, maxCol - col, color);
                grid.paintCell(maxRow - row, col, color);
                grid.paintCell(maxRow - row, maxCol - col, color);
            }
            case NONE -> {}
        }
    }
}

