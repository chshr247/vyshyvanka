import javafx.scene.paint.Color;

/**
 * Service for duplicating and tiling grid patterns with symmetry support.
 * Handles fragment extraction and symmetric repetition.
 */
public class TilePatternService implements ITileService {
    
    private IGridModel gridModel;
    private SymmetryService symmetryService;
    private SymMode currentSymMode;
    
    public TilePatternService(IGridModel gridModel, SymmetryService symmetryService) {
        this.gridModel = gridModel;
        this.symmetryService = symmetryService;
        this.currentSymMode = SymMode.NONE;
    }
    
    public void setSymmetryMode(SymMode mode) {
        this.currentSymMode = mode;
    }
    
    @Override
    public void tilePattern(int tileWidth, int tileHeight) {
        Color[][] fragment = new Color[tileHeight][tileWidth];
        
        // Extract fragment from top-left corner
        for (int r = 0; r < tileHeight && r < gridModel.getRows(); r++) {
            for (int c = 0; c < tileWidth && c < gridModel.getCols(); c++) {
                fragment[r][c] = gridModel.getCell(r, c);
            }
        }
        
        // Apply tiling with symmetry
        for (int r = 0; r < gridModel.getRows(); r++) {
            int tileRow = r / tileHeight;
            int localR = r % tileHeight;
            
            for (int c = 0; c < gridModel.getCols(); c++) {
                int tileCol = c / tileWidth;
                int localC = c % tileWidth;
                
                boolean flipH = false;
                boolean flipV = false;
                
                switch (currentSymMode) {
                    case HORIZONTAL -> flipH = (tileCol % 2 == 1);
                    case VERTICAL -> flipV = (tileRow % 2 == 1);
                    case BOTH -> {
                        flipH = (tileCol % 2 == 1);
                        flipV = (tileRow % 2 == 1);
                    }
                    case NONE -> {}
                }
                
                int srcR = flipV ? (tileHeight - 1 - localR) : localR;
                int srcC = flipH ? (tileWidth - 1 - localC) : localC;
                
                gridModel.paintCell(r, c, fragment[srcR][srcC]);
            }
        }
    }
}

