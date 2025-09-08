package pepse.world;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import static pepse.util.Constants.*;

/**
 * The Cloud class represents a cloud in the game world, capable of generating rain and animating
 * cloud blocks as they move across the screen. This class handles the creation and behavior
 * of clouds and their associated raindrops.
 */
public class Cloud {
    private static final float HALF_FACTOR = 0.5f; // Used for calculations based on halves
    private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255); // Default cloud color
    private static final Color DROP_COLOR = new Color(20, 180, 255); // Color of raindrops
    private static final Vector2 DROP_SIZE = new Vector2(10f, 10f); // Size of raindrops
    private static final float DROP_GRAVITY = 60f; // Acceleration due to gravity for raindrops
    private static final Float DROP_FINAL_OPACITY = 0f; // Final transparency of raindrops
    private static final Float DROP_START_OPACITY = 1f; // Initial opacity of raindrops
    private static final Float DROP_FALL_TIME = 5f; // Time it takes for raindrops to fall and fade out
    private static final List<List<Integer>> CLOUD_GRID = List.of(
            // Grid pattern that defines the structure of the cloud
            List.of(0, 1, 1, 0, 0, 0),
            List.of(1, 1, 1, 0, 1, 0),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(0, 1, 1, 1, 0, 0),
            List.of(0, 0, 0, 0, 0, 0)
    );
    private static final float CLOUD_POSITION_FACTOR = 0.1f; // Determines cloud's vertical position
    private static final float TRANSITION_TIME = 40f; // Time for a full movement of cloud blocks
                                                        // across the screen
    private static final int MAX_NUMBER_OF_DROPS = 5; // Maximum number of raindrops generated at once
    private static final int PUT_BLOCK_SIGN = 1; // Indicates where a block exists in the cloud grid

    private final Random random = new Random(); // Used to randomize rain generation
    private final Vector2 startTopLeftCorner; // Initial position of the cloud (top-left corner)
    private final List<List<Integer>> cloudGrid; // Copy of the cloud structure grid
    private final Vector2 windowDimensions; // Dimensions of the game window
    private final BiConsumer<GameObject, Integer> removeGameObject; // Function to remove objects
    private final BiConsumer<GameObject, Integer> addGameObject; // Function to add objects to the game
    private List<List<GameObject>> cloudBlocks; // List of all blocks that form the cloud

    /**
     * Initializes a Cloud object with a predefined shape, position, and behavior.
     * The cloud is composed of multiple blocks arranged according to a grid pattern.
     * Its position is set relative to the game window, starting off-screen, and
     * it moves continuously across the screen. The cloud can also generate raindrops
     * at random intervals.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param removeGameObject A function to remove objects from the game world.
     * @param addGameObject    A function to add objects to the game world.
     */
    public Cloud(Vector2 windowDimensions,
                 BiConsumer<GameObject, Integer> removeGameObject,
                 BiConsumer<GameObject, Integer> addGameObject) {
        this.cloudGrid = deepCopy(CLOUD_GRID); // Create a copy of the default cloud grid for upgrade reasons
        this.startTopLeftCorner = new Vector2(
                cloudGrid.get(0).size() * BLOCK_SIZE * -1,
                windowDimensions.y() * CLOUD_POSITION_FACTOR
        );
        this.addGameObject = addGameObject;
        this.removeGameObject = removeGameObject;
        this.windowDimensions = windowDimensions;
        this.cloudBlocks = null; // Cloud blocks will be initialized when `addCloud` is called
    }

    /**
     * Creates raindrops that fall from the cloud.
     * The raindrops are generated at random positions under the cloud's center.
     * They fall with gravity, fade out over time, and are removed once they disappear.
     */
    public void makeRain() {
        if (cloudBlocks == null) {
            return; // Exit if cloud blocks have not been initialized
        }

        // Find the middle row of the cloud to position raindrops
        int middleRowIndex = (int) (cloudBlocks.size() * HALF_FACTOR);
        Vector2 middleLeftCorner = cloudBlocks.get(middleRowIndex).get(0).getTopLeftCorner();
        float cloudWidth = cloudGrid.get(0).size() * BLOCK_SIZE;

        // Generate a few random raindrops
        for (int i = 0; i < random.nextInt(MAX_NUMBER_OF_DROPS); i++) {
            Vector2 dropStartPosition = new Vector2(
                    middleLeftCorner.x() + random.nextFloat(cloudWidth), // Random X within cloud width
                    middleLeftCorner.y() // Y starts just below the cloud
            );

            // Create a raindrop
            GameObject drop = new GameObject(
                    dropStartPosition,
                    DROP_SIZE,
                    new OvalRenderable(DROP_COLOR)
            );
            drop.transform().setAccelerationY(DROP_GRAVITY);
            drop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
            addGameObject.accept(drop, Layer.BACKGROUND);

            // Make the raindrop fade out and remove it once invisible
            new Transition<>(
                    drop,
                    drop.renderer()::setOpaqueness,
                    DROP_START_OPACITY,
                    DROP_FINAL_OPACITY,
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    DROP_FALL_TIME,
                    Transition.TransitionType.TRANSITION_ONCE,
                    () -> removeGameObject.accept(drop, Layer.BACKGROUND)
            );
        }
    }

    /**
     * Creates the blocks that form the cloud based on a predefined grid pattern.
     * Each block is positioned according to its row and column in the grid.
     * The blocks move across the screen in a loop, creating the appearance of
     * a drifting cloud.
     */
    public void addCloud() {
        if (cloudBlocks != null) {
            return; // Exit if the cloud has already been created
        }

        float cloudWidth = cloudGrid.get(0).size() * BLOCK_SIZE;
        cloudBlocks = new ArrayList<>();

        // Create cloud blocks based on the grid
        for (int row = 0; row < cloudGrid.size(); row++) {
            cloudBlocks.add(new ArrayList<>());
            for (int col = 0; col < cloudGrid.get(row).size(); col++) {
                if (cloudGrid.get(row).get(col) != PUT_BLOCK_SIGN) {
                    continue; // Skip cells without blocks
                }

                // Calculate the position of the block
                float blockX = startTopLeftCorner.x() + col * BLOCK_SIZE;
                float blockY = startTopLeftCorner.y() + row * BLOCK_SIZE;

                // Create a cloud block
                GameObject cloudBlock = new Block(
                        new Vector2(blockX, blockY),
                        new RectangleRenderable(ColorSupplier.approximateMonoColor(BASE_CLOUD_COLOR))
                );
                addGameObject.accept(cloudBlock, CLOUD_LAYER);
                cloudBlock.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                cloudBlocks.get(row).add(cloudBlock);

                // Animate the block to move across the screen
                new Transition<>(
                        cloudBlock,
                        (currentXPosition) -> cloudBlock.setCenter(new Vector2(currentXPosition, blockY)),
                        blockX,
                        blockX + windowDimensions.x() + cloudWidth + BLOCK_SIZE,
                        Transition.LINEAR_INTERPOLATOR_FLOAT,
                        TRANSITION_TIME,
                        Transition.TransitionType.TRANSITION_LOOP,
                        null
                );
            }
        }
    }

    /*
     * Creates a deep copy of a nested List of integers.
     */
    private List<List<Integer>> deepCopy(List<List<Integer>> original) {
        List<List<Integer>> copy = new ArrayList<>();
        for (List<Integer> row : original) {
            copy.add(new ArrayList<>(row)); // Create a new list for each inner list
        }
        return copy;
    }
}
