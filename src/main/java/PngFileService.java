import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Service for loading and saving PNG images to/from the grid.
 * Handles image I/O and color sampling.
 */
public class PngFileService implements IPngService {
    
    private static final int CELL_SIZE = 20;
    private IGridModel gridModel;
    private Canvas canvas;
    private ColorService colorService;
    
    public PngFileService(IGridModel gridModel, Canvas canvas, ColorService colorService) {
        this.gridModel = gridModel;
        this.canvas = canvas;
        this.colorService = colorService;
    }
    
    @Override
    public void loadPNG(String filePath) {
        try {
            BufferedImage bi = ImageIO.read(new File(filePath));
            if (bi == null) {
                return;
            }
            
            gridModel.clearGrid();
            
            for (int r = 0; r < gridModel.getRows(); r++) {
                for (int c = 0; c < gridModel.getCols(); c++) {
                    int px = c * CELL_SIZE + CELL_SIZE / 2;
                    int py = r * CELL_SIZE + CELL_SIZE / 2;
                    
                    if (px < bi.getWidth() && py < bi.getHeight()) {
                        int rgb = bi.getRGB(px, py);
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = (rgb) & 0xFF;
                        int alpha = (rgb >> 24) & 0xFF;
                        
                        if (alpha > 10) {
                            Color col = Color.rgb(red, green, blue);
                            double brightness = colorService.calculateBrightness(col);
                            if (brightness < 0.85) {
                                gridModel.paintCell(r, c, col);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading PNG: " + e.getMessage());
        }
    }
    
    @Override
    public void savePNG(String filePath) {
        try {
            WritableImage img = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, img);
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(filePath));
        } catch (IOException e) {
            System.out.println("Error saving image to PNG: " + e.getMessage());
        }
    }
}

