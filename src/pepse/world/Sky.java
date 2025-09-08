package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import danogl.gui.rendering.Renderable;

import java.awt.*;

import static pepse.util.Constants.SKY_TAG;

/**
 * The Sky class is responsible for creating a visual representation of the sky
 * in the game.
 */
public class Sky {
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * Creates a GameObject representing the sky.
     *
     * @param windowDimensions The dimensions of the window.
     * @return A GameObject representing the sky.
     */
    public static GameObject create(Vector2 windowDimensions) {
        Renderable skyRenderable = new RectangleRenderable(BASIC_SKY_COLOR);
        GameObject sky = new GameObject(
                Vector2.ZERO,
                windowDimensions,
                skyRenderable
        );

        // Set the sky to move with the camera
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY_TAG);
        return sky;
    }
}
