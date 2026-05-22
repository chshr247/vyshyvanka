import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Builds and manages the toolbar UI component.
 * Encapsulates toolbar layout, buttons, and event handlers.
 */
public class DrawingToolsUI {
    
    private HBox toolbar;
    private Button btnUndo;
    private Button btnRedo;
    private Button btnNew;
    private Button btnOpen;
    private Button btnTile;
    private Button btnGenerateName;
    private Button btnSave;
    
    private Runnable onUndoPressed;
    private Runnable onRedoPressed;
    private Runnable onNewPressed;
    private Runnable onOpenPressed;
    private Runnable onTilePressed;
    private Runnable onGenerateNamePressed;
    private Runnable onSavePressed;
    
    public DrawingToolsUI() {
        buildToolbar();
    }
    
    private void buildToolbar() {
        toolbar = new HBox();
        toolbar.setPadding(new Insets(0, 16, 0, 16));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPrefHeight(64);
        toolbar.getStyleClass().add("toolbar");
        
        Label logo = new Label("⊞");
        logo.getStyleClass().add("toolbar-logo");
        
        Label title = new Label("Vyshyvanka Editor - Mokliak Viacheslav");
        title.getStyleClass().add("toolbar-title");
        
        HBox left = new HBox(10, logo, title);
        left.setAlignment(Pos.CENTER_LEFT);
        
        btnUndo = new Button("Undo");
        btnRedo = new Button("Redo");
        btnNew = new Button("New");
        btnOpen = new Button("Open File");
        btnTile = new Button("Tile");
        btnGenerateName = new Button("Generate Name");
        btnSave = new Button("Save as PNG");
        
        btnUndo.getStyleClass().add("btn-ghost");
        btnRedo.getStyleClass().add("btn-ghost");
        btnNew.getStyleClass().add("btn-ghost");
        btnOpen.getStyleClass().add("btn-ghost");
        btnTile.getStyleClass().add("btn-ghost");
        btnGenerateName.getStyleClass().add("btn-ghost");
        btnSave.getStyleClass().add("btn-primary");
        
        btnUndo.setOnMousePressed(e -> {
            if (onUndoPressed != null) {
                onUndoPressed.run();
            }
        });
        btnRedo.setOnMousePressed(e -> {
            if (onRedoPressed != null) {
                onRedoPressed.run();
            }
        });
        btnNew.setOnAction(e -> {
            if (onNewPressed != null) {
                onNewPressed.run();
            }
        });
        btnOpen.setOnAction(e -> {
            if (onOpenPressed != null) {
                onOpenPressed.run();
            }
        });
        btnTile.setOnAction(e -> {
            if (onTilePressed != null) {
                onTilePressed.run();
            }
        });
        btnGenerateName.setOnAction(e -> {
            if (onGenerateNamePressed != null) {
                onGenerateNamePressed.run();
            }
        });
        btnSave.setOnAction(e -> {
            if (onSavePressed != null) {
                onSavePressed.run();
            }
        });
        
        HBox right = new HBox(4, btnUndo, btnRedo, btnNew, btnOpen, btnTile, btnGenerateName, btnSave);
        right.setAlignment(Pos.CENTER_RIGHT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        toolbar.getChildren().addAll(left, spacer, right);
    }
    
    public HBox getToolbar() {
        return toolbar;
    }
    
    public void setOnNewPressed(Runnable handler) {
        this.onNewPressed = handler;
    }
    
    public void setOnOpenPressed(Runnable handler) {
        this.onOpenPressed = handler;
    }
    
    public void setOnTilePressed(Runnable handler) {
        this.onTilePressed = handler;
    }
    
    public void setOnGenerateNamePressed(Runnable handler) {
        this.onGenerateNamePressed = handler;
    }
    
    public void setOnSavePressed(Runnable handler) {
        this.onSavePressed = handler;
    }

    public void setOnUndoPressed(Runnable handler) {
        this.onUndoPressed = handler;
    }

    public void setOnRedoPressed(Runnable handler) {
        this.onRedoPressed = handler;
    }
}
