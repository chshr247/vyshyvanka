import javafx.scene.paint.Color;

/**
 * Service for managing color palette and color operations.
 * Encapsulates color selection, palette definitions, and brightness calculations.
 */
public class ColorService {
    
    private static final Color[] PALETTE = {
            Color.web("#8B1A1A"),
            Color.web("#000000"),
            Color.web("#228B22"),
            Color.web("#1E90FF"),
            Color.web("#FFD700"),
            Color.web("#800080"),
            Color.web("#00CED1"),
            Color.web("#FF69B4")
    };
    
    private Color selectedColor;
    
    public ColorService() {
        this.selectedColor = PALETTE[0];
    }
    
    public Color[] getPalette() {
        return PALETTE;
    }
    
    public Color getSelectedColor() {
        return selectedColor;
    }
    
    public void setSelectedColor(Color color) {
        this.selectedColor = color;
    }
    
    /**
     * Calculate brightness of a color using standard formula.
     * Used for filtering dark colors when loading from images.
     */
    public double calculateBrightness(Color color) {
        return color.getRed() * 0.299
                + color.getGreen() * 0.587
                + color.getBlue() * 0.114;
    }
    
    /**
     * Returns a darker variant of the given color.
     */
    public Color getDarkerVariant(Color color) {
        return color.darker();
    }
}

