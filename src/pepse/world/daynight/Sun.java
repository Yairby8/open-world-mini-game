package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

import static pepse.util.Constants.SUN_TAG;

/**
 * Represents the sun in the game world. The sun is a GameObject that moves in a circular path to simulate
 * the passage of time in the game.
 */
public class Sun {
    private static final Renderable YELLOW_OVAL_RENDERABLE = new OvalRenderable(Color.YELLOW);
    private static final float HALF_FACTOR = 0.5f;
    private static final float SUN_RELATIVE_HEIGHT_FACTOR = 0.25f;
    private static final float START_ANGLE = 0.0f;
    private static final float LAST_ANGLE = 360.0f;
    private static final Vector2 SUN_SIZE = new Vector2(100, 100);

    /**
     * Creates a sun GameObject that moves in a circular path to simulate the day-night cycle.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The duration of a full day-night cycle in seconds.
     * @return A new GameObject representing the sun.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        // Create the sun GameObject
        GameObject sun = new GameObject(Vector2.ZERO, SUN_SIZE, YELLOW_OVAL_RENDERABLE);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        // Set the initial position of the sun
        Vector2 initialSunCenter = new Vector2(
                windowDimensions.x() * HALF_FACTOR,
                windowDimensions.y() * SUN_RELATIVE_HEIGHT_FACTOR
        );
        sun.setCenter(initialSunCenter);

        // Add circular movement transition to the sun
        addTransition(sun, initialSunCenter, windowDimensions, cycleLength);

        return sun;
    }

    /**
     * Adds a transition to the sun GameObject to move it in a circular path.
     *
     * @param sun The sun GameObject to which the transition is applied.
     * @param initialSunCenter The initial center position of the sun.
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The duration of a full day-night cycle in seconds.
     */
    private static void addTransition(GameObject sun, Vector2 initialSunCenter,
                                      Vector2 windowDimensions, float cycleLength) {
        // Define the center of the circular path (horizon line at ground height)
        Vector2 cycleCenter = new Vector2(
                windowDimensions.x() * HALF_FACTOR,
                windowDimensions.y() * HALF_FACTOR
        );

        new Transition<Float>(
                sun,
                (Float angle) -> sun.setCenter(
                        initialSunCenter.subtract(cycleCenter)
                                .rotated(angle)
                                .add(cycleCenter)
                ),
                START_ANGLE, // Start angle (0 degrees)
                LAST_ANGLE,  // End angle (360 degrees)
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );
    }
}
