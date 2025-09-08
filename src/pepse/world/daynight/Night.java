package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

import static pepse.util.Constants.NIGHT_TAG;

/**
 * Represents the night effect in the game by creating a translucent black overlay.
 * The overlay gradually transitions between transparency levels to simulate day-night cycles.
 * This class provides methods to create a night GameObject and add transitions
 * to simulate the changing light conditions over time.
 */
public class Night {
    private static final Float NOON_OPACITY = 0.0f;
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    private static final float HALF_FACTOR = 0.5f;

    private static final Renderable BLACK_SQUARE_READABLE = new RectangleRenderable(Color.BLACK);

    /**
     * Creates a night overlay GameObject that transitions between transparency levels
     * to simulate day and night cycles.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength      The total length of a day-night cycle, in seconds.
     * @return GameObject representing the night overlay.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength){
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, BLACK_SQUARE_READABLE);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        addTransition(night, cycleLength);
        return night;
    }

    /*
     * Adds a transition to the given night GameObject to simulate day-night transparency changes.
     * The transition alternates the opacity of the overlay between noon and midnight values.
     */
    private static void addTransition(GameObject night, float cycleLength){
        float transitionTime = cycleLength * HALF_FACTOR; // Half a cycle
        new Transition<Float>(
                    night,
                    night.renderer()::setOpaqueness,
                    NOON_OPACITY,
                    MIDNIGHT_OPACITY,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    transitionTime,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null
        );
    }
}
