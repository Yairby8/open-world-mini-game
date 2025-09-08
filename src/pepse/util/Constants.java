package pepse.util;

import danogl.collisions.Layer;
import danogl.util.Vector2;

/**
 * The Constants class holds various global constants used throughout the game.
 * These constants include layer indices, chunk sizes, object tags, and other
 * configuration values required for terrain generation, performance, and gameplay.
 *
 * @author Yair Ben Yakar, Ofek Levy
 */
public class Constants {

    // Chunk Configuration
    /**
     * Size of one chunk in pixels.
     */
    public static final int CHUNK_SIZE = 820;

    // Layers
    /**
     * Layer index for the night effect.
     */
    public static final int NIGHT_LAYER = Layer.UI - 1;

    /**
     * Layer index for the tree trunks.
     */
    public static final int TRUNK_LAYER = Layer.STATIC_OBJECTS - 1;

    /**
     * Layer index for the leaves.
     */
    public static final int LEAF_LAYER = TRUNK_LAYER + 1;

    /**
     * Layer index for the fruits.
     */
    public static final int FRUIT_LAYER = LEAF_LAYER + 1;

    /**
     * Layer index for the clouds.
     */
    public static final int CLOUD_LAYER = Layer.BACKGROUND + 1;

    // Seed Range
    /**
     * Minimum value for random seed generation.
     */
    public static final int MIN_SEED = 1000;

    /**
     * Maximum value for random seed generation.
     */
    public static final int MAX_SEED = 10000;

    // Initial Chunk Configuration
    /**
     * Initial index of the leftmost chunk.
     */
    public static final int INITIAL_LEFT_CHUNK = -1;

    /**
     * Initial index of the rightmost chunk.
     */
    public static final int INITIAL_RIGHT_CHUNK = 2;

    /**
     * Initial minimum x-coordinate for terrain generation.
     */
    public static final int INITIAL_MIN_X = 0;

    /**
     * Initial maximum x-coordinate for terrain generation.
     */
    public static final int INITIAL_MAX_X = CHUNK_SIZE * 3;

    // Performance
    /**
     * Target framerate for the game.
     */
    public static final int TARGET_FRAMERATE = 45;

    // Energy Indicator
    /**
     * Position of the energy indicator on the screen.
     */
    public static final Vector2 ENERGY_INDICATOR_POSITION = new Vector2(20, 20);

    /**
     * Size of the energy indicator.
     */
    public static final Vector2 ENERGY_INDICATOR_SIZE = new Vector2(20, 20);

    /**
     * Amount of energy received from colliding with fruit.
     */
    public static final float FRUIT_ENERGY_BOOST = 10;


    // Object Tags
    /**
     * Tag used for identifying tree trunks.
     */
    public static final String TRUNK_TAG = "TreeTrunk";

    /**
     * Tag used for identifying leaves.
     */
    public static final String LEAF_TAG = "Leaf";

    /**
     * Tag used for identifying fruits.
     */
    public static final String FRUIT_TAG = "Fruit";

    /**
     * Tag used for identifying eaten fruits.
     */
    public static final String EATEN_FRUIT_TAG = "EatenFruit";

    /**
     * Tag used for identifying blocks.
     */
    public static final String BLOCK_TAG = "block";

    /**
     * Tag used for identifying the night effect.
     */
    public static final String NIGHT_TAG = "Night";

    /**
     * Tag used for identifying the sun.
     */
    public static final String SUN_TAG = "SUN";

    /**
     * Tag used for identifying the sun's halo.
     */
    public static final String SUN_HALO_TAG = "SunHalo";

    /**
     * Tag used for identifying the sky.
     */
    public static final String SKY_TAG = "skyTag";

    // Block Configuration
    /**
     * Size of a single block in pixels.
     */
    public static final int BLOCK_SIZE = 30;

    // Time Cycle
    /**
     * Length of a single day-night cycle in seconds.
     */
    public static final float CYCLE_LENGTH = 30;

}
