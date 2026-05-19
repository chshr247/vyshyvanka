import javafx.scene.paint.Color;

/**
 * Manages the grid state and cell operations.
 * Responsible for storing and manipulating the Color[][] grid.
 */
public class GridModel implements IGridModel {
    
    private Color[][] grid;
    private int rows;
    private int cols;
    
    public GridModel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Color[rows][cols];
    }
    
    @Override
    public void paintCell(int row, int col, Color color) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }
        grid[row][col] = color;
    }
    
    @Override
    public Color getCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }
        return grid[row][col];
    }
    
    @Override
    public void clearGrid() {
        grid = new Color[rows][cols];
    }
    
    @Override
    public void resizeGrid(int newRows, int newCols) {
        this.rows = Math.max(5, newRows);
        this.cols = Math.max(5, newCols);
        this.grid = new Color[this.rows][this.cols];
    }
    
    @Override
    public int getRows() {
        return rows;
    }
    
    @Override
    public int getCols() {
        return cols;
    }
    
    @Override
    public Color[][] getGridData() {
        return grid;
    }
    
    @Override
    public void setGridData(Color[][] data) {
        if (data != null && data.length > 0 && data[0].length > 0) {
            this.rows = data.length;
            this.cols = data[0].length;
            this.grid = data;
        }
    }
}

