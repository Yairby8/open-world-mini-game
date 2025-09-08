package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

import static pepse.util.Constants.SUN_HALO_TAG;

/**
 * Represents a halo around the sun in the game.
 * The halo follows the sun and has a translucent yellow appearance.
 */
public class SunHalo {
    private static final Color HALO_COLOR =
            new Color(255, 255, 0, 20); // Yellow with transparency
    private static final Vector2 HALO_SIZE = new Vector2(250, 250);
    private static final OvalRenderable HALO_RENDER =
            new OvalRenderable(HALO_COLOR); // Translucent yellow circle

    /**
     * Creates a SunHalo GameObject that follows the sun.
     *
     * @param sun The sun GameObject that the halo will follow.
     * @return A new GameObject representing the sun's halo.
     */
    public static GameObject create(GameObject sun) {
        // Create a static halo GameObject
        GameObject sunHalo = new GameObject(
                Vector2.ZERO,
                HALO_SIZE,
                HALO_RENDER
        );

        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO_TAG);

        // Make the halo follow the sun
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));

        return sunHalo;
    }
}
