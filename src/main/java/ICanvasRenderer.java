import javafx.scene.canvas.Canvas;

/**
 * Interface for canvas rendering operations.
 * Defines contracts for drawing the grid on a Canvas.
 */
public interface ICanvasRenderer {
    
    void redraw();
    
    void setCanvas(Canvas canvas);
    
    Canvas getCanvas();
}

