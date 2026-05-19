import javafx.scene.paint.Color;

/**
 * Interface for grid state management.
 * Handles cell painting, grid dimensions, and operations.
 */
public interface IGridModel {
    
    void paintCell(int row, int col, Color color);
    
    Color getCell(int row, int col);
    
    void clearGrid();
    
    void resizeGrid(int newRows, int newCols);
    
    int getRows();
    
    int getCols();
    
    Color[][] getGridData();
    
    void setGridData(Color[][] data);
}

