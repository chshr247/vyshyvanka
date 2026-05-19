import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.File;
import java.util.Optional;

/**
 * Factory for creating and managing dialog windows.
 * Centralizes all dialog creation logic.
 */
public class DialogFactory {
    
    /**
     * Shows a "Duplicate Fragment" dialog for tiling operations.
     * Returns array {width, height} or null if cancelled.
     */
    public static Optional<int[]> showTileDialog(SymMode currentSymMode) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Duplicate Fragment");
        dialog.setHeaderText("Symmetry: " + currentSymMode.name().toLowerCase()
                + "\nFragment (top-left corner) will be duplicated with current symmetry");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        TextField tfW = new TextField("10");
        TextField tfH = new TextField("10");
        
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(12));
        gp.add(new Label("Fragment Width:"), 0, 0);
        gp.add(tfW, 1, 0);
        gp.add(new Label("Fragment Height:"), 0, 1);
        gp.add(tfH, 1, 1);
        
        dialog.getDialogPane().setContent(gp);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return Optional.empty();
        }
        
        try {
            int width = Integer.parseInt(tfW.getText().trim());
            int height = Integer.parseInt(tfH.getText().trim());
            return Optional.of(new int[]{width, height});
        } catch (NumberFormatException ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Enter valid numbers");
            errorAlert.showAndWait();
            return Optional.empty();
        }
    }
    
    /**
     * Shows a "Generate from Name" dialog with file selection.
     * Returns selected file name (without .png) or empty Optional if cancelled.
     */
    public static Optional<String> showGenerateFromNameDialog() {
        File dir = new File("images");
        File[] pngs = dir.listFiles((d, n) -> n.endsWith(".png"));
        
        if (pngs == null || pngs.length == 0) {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, "No files in images folder");
            infoAlert.showAndWait();
            return Optional.empty();
        }
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>();
        for (File f : pngs) {
            dialog.getItems().add(f.getName().replace(".png", ""));
        }
        dialog.setSelectedItem(dialog.getItems().get(0));
        dialog.setTitle("Generate from Name");
        dialog.setHeaderText("Select Name");
        dialog.setContentText("Name:");
        
        return dialog.showAndWait();
    }
    
    /**
     * Shows an error alert with custom message.
     */
    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

