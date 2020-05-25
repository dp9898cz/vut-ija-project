package ija_project;

import javafx.scene.shape.Shape;
import java.util.List;

/**
 * Interface for drawable objects
 * method getGui()
 * @author Daniel Pátek (xpatek08)
 * @author Daniel Čechák (xcecha06)
 * @version 1.0
 */
public interface Drawable {
    /**
     * Get GUI elements
     * @return List of shapes to draw
     */
    List<Shape> getGui();
}
