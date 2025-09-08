package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.Color;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * An energy level indicator that displays the current energy of an avatar
 * as both text and a color-coded bar.
 */
public class EnergyLevelIndicator extends GameObject {

    private static final Color HIGH_ENERGY_COLOR = new Color(0, 200, 0);
    private static final Color MID_ENERGY_COLOR = new Color(230, 230, 0);
    private static final Color LOW_ENERGY_COLOR = new Color(230, 0, 0);
    private static final float HIGH_THRESHOLD = 0.6f;
    private static final float LOW_THRESHOLD = 0.3f;
    private static final float BAR_HEIGHT = 10f;
    private static final float BAR_MARGIN = 5f;
    private static final float MIN_BAR_WIDTH = 2f;

    private final TextRenderable textObject;
    private final Supplier<Integer> energyProvider;
    private final int maxEnergy = Avatar.MAX_ENERGY;
    private final GameObject energyBar;
    private final float barMaxWidth;
    private final Vector2 barAnchorPosition;

    /**
     * Constructs an EnergyLevelIndicator that displays the current energy level.
     *
     * @param topLeftCorner   The position of the indicator on the screen.
     * @param dimensions      The dimensions of the indicator (width, height).
     * @param textObject      The TextRenderable object used to display the energy level.
     * @param energyProvider  The EnergyProvider that provides the current energy level.
     * @param gameObjects     Function to add objects to the game.
     * @param layer           The layer to render the indicator on.
     */
    public EnergyLevelIndicator(Vector2 topLeftCorner,
                                Vector2 dimensions,
                                TextRenderable textObject,
                                Supplier<Integer> energyProvider,
                                BiConsumer<GameObject, Integer> gameObjects,
                                int layer) {
        super(topLeftCorner, dimensions, textObject);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.energyProvider = energyProvider;
        this.textObject = textObject;
        this.barMaxWidth = dimensions.x() * 6.5f;

        // Create bar position
        Vector2 barPos = new Vector2(topLeftCorner.x(), topLeftCorner.y() + dimensions.y() + BAR_MARGIN);
        this.barAnchorPosition = barPos; // Store the initial left position
        Vector2 barSize = new Vector2(dimensions.x(), BAR_HEIGHT);

        // Create energy bar
        this.energyBar = new GameObject(
                barPos,
                barSize,
                new RectangleRenderable(HIGH_ENERGY_COLOR)
        );
        this.energyBar.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.accept(energyBar, layer);
    }

    /**
     * Updates the displayed energy level and energy bar based on the current energy level.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // Get current energy and calculate percentage
        int currentEnergy = energyProvider.get();
        float percentage = (float) currentEnergy / maxEnergy;

        // Update text display
        this.textObject.setString("ENERGY:" + currentEnergy + "%");

        // Update energy bar width while keeping left edge anchored
        float newWidth = Math.max(MIN_BAR_WIDTH, barMaxWidth * percentage);

        // Important: Set position to maintain left edge anchor, then update dimensions
        energyBar.setCenter(new Vector2(
                barAnchorPosition.x() + newWidth/2, // Position center of bar at left edge + half width
                energyBar.getCenter().y()
        ));
        energyBar.setDimensions(new Vector2(newWidth, BAR_HEIGHT));

        // Update energy bar color based on level
        Color barColor;
        if (percentage > HIGH_THRESHOLD) {
            barColor = HIGH_ENERGY_COLOR;
        } else if (percentage > LOW_THRESHOLD) {
            barColor = MID_ENERGY_COLOR;
        } else {
            barColor = LOW_ENERGY_COLOR;
        }
        energyBar.renderer().setRenderable(new RectangleRenderable(barColor));
    }
}