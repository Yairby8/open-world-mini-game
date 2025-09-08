package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static pepse.util.Constants.*;


/**
 * Represents a tree object in the game, including its trunk, leaves, and fruits.
 * The tree is composed of multiple game objects (trunk, leaves, and fruits),
 * each of which has specific properties and behaviors such as swaying leaves
 * and fruits that can regenerate after being "eaten."
 */
public class Tree {
    private final static float HALF_FACTOR = 0.5f; // Constant to calculate half-size values
    private final static float TRUNK_Y_POSITION_FACTOR = 20f; // Adjustment factor for the trunk's
    // vertical positioning
    private final static float TOP_POSITION_Y_FACTOR = 20f; // Adjustment factor for objects on the
    // tree's top

    // Constants defining the size and behavior of the tree's components
    private static final int LEAF_SIZE = 20; // Size (width and height) of a leaf
    private static final int FRUIT_SIZE = 10; // Size (width and height) of a fruit
    private static final int TREE_TOP_SIZE = 90; // Width of the tree's top area for placing
    // leaves and fruits
    private static final float LEAF_SWAY_TIME = 0.5f; // Time (in seconds) for a leaf to complete a sway
    private static final float LEAF_SWAY_MIN_ROTATION = -10f; // Minimum rotation angle for swaying leaves
    private static final float LEAF_SWAY_MAX_ROTATION = 10f; // Maximum rotation angle for swaying leaves
    private static final float LEAF_SWAY_MIN_WIDTH_FACTOR = 0.8f; // Minimum width factor for swaying leaves
    private static final float LEAF_SWAY_MAX_WIDTH_FACTOR = 1.2f; // Maximum width factor for swaying leaves

    // Constants for randomizing tree properties
    private static final float MIN_TRUNK_HEIGHT = 70f; // Minimum height of the tree trunk
    private static final float MAX_TRUNK_HEIGHT = 140f; // Maximum height of the tree trunk
    private static final float MIN_TRUNK_WIDTH = 15f; // Minimum width of the tree trunk
    private static final float MAX_TRUNK_WIDTH = 30f; // Maximum width of the tree trunk
    private static final int MAX_NUMBER_OF_LEAVES = 150; // Maximum number of leaves on the tree
    private static final int MAX_NUMBER_OF_FRUITS = 5; // Maximum number of fruits on the tree
    private static final float MAX_TRANSITION_DELAY_TIME = 0.8f; // Maximum delay before starting a leaf
    // sway transition

    // Constants for colors of the tree's components
    private static final Color TRUNK_COLOR = new Color(100, 50, 20); // Color of the trunk
    private static final Color LEAF_COLOR = new Color(50, 200, 30); // Color of the leaves
    private static final Color FRUIT_COLOR = new Color(200, 50, 50); // Color of the fruits

    // Tree components
    private final GameObject trunk; // The trunk of the tree
    private final ArrayList<GameObject> leaves; // List of leaves on the tree
    private final ArrayList<GameObject> fruits; // List of fruits on the tree

    private final Random random; // Random object for generating random positions and sizes

    /**
     * Constructs a Tree object, initializing its trunk, leaves, and fruits with randomized properties.
     *
     * @param x           The X-coordinate of the tree's base.
     * @param y           The Y-coordinate of the tree's base.
     * @param seed        A seed for randomizing the tree's properties.
     */
    public Tree(float x, float y, int seed) {
        this.random = new Random(Objects.hash(x, seed)); // Random instance with a
                                                        // seed for consistent behavior
        this.trunk = createTrunk(x, y); // Initialize the tree trunk
        this.leaves = generateGameObjectsList(MAX_NUMBER_OF_LEAVES, this::createLeaf); // Generate leaves
        this.fruits = generateGameObjectsList(MAX_NUMBER_OF_FRUITS, this::createFruit); // Generate fruits
    }

    /**
     * Adds or removes the tree and its components (trunk, leaves, and fruits) from the game world.
     *
     * @param action A function that either adds or removes a GameObject from the game world.
     */
    public void removeOrAddTreeFromGame(BiConsumer<GameObject, Integer> action) {
        action.accept(trunk, TRUNK_LAYER);
        for (GameObject leaf : leaves) {
            action.accept(leaf, LEAF_LAYER);
        }
        for (GameObject fruit : fruits) {
            action.accept(fruit, FRUIT_LAYER);
        }
    }

    /**
     * Returns the center position of the tree trunk.
     *
     * @return A Vector2 representing the center position of the trunk.
     */
    public Vector2 getTrunkCenter() {
        return trunk.getCenter();
    }

    /*
     * Creates the trunk of the tree as a Block with randomized height and width.
     *
     * param: x The X-coordinate of the tree's base.
     * param: y The Y-coordinate of the tree's base.
     * return: A GameObject representing the trunk of the tree.
     */
    private GameObject createTrunk(float x, float y) {
        float trunkHeight = random.nextFloat(MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT);
        float trunkWidth = random.nextFloat(MIN_TRUNK_WIDTH, MAX_TRUNK_WIDTH);

        RectangleRenderable trunkRenderer = new RectangleRenderable(TRUNK_COLOR);
        GameObject trunk = new Block(Vector2.ZERO, trunkRenderer);
        trunk.setDimensions(new Vector2(trunkWidth, trunkHeight));
        trunk.setCenter(new Vector2(x, y - trunkHeight * HALF_FACTOR + TRUNK_Y_POSITION_FACTOR));
        trunk.setTag(TRUNK_TAG);
        return trunk;
    }

    /*
     * Generates a list of GameObjects (e.g., leaves or fruits) by repeatedly calling a supplier function.
     *
     * param: maxNumberOfGameObjects The maximum number of GameObjects to create.
     * param: createGameObject       A supplier function that creates a single GameObject.
     * return: An ArrayList of GameObjects.
     */
    private ArrayList<GameObject> generateGameObjectsList(int maxNumberOfGameObjects,
                                                          Supplier<GameObject> createGameObject) {
        int numberOfGameObjects = random.nextInt(maxNumberOfGameObjects + 1);
        ArrayList<GameObject> gameObjectsList = new ArrayList<>();
        for (int i = 0; i < numberOfGameObjects; i++) {
            gameObjectsList.add(createGameObject.get());
        }
        return gameObjectsList;
    }

    /*
     * Creates a single leaf GameObject with swaying motion and randomized position on the tree's top.
     *
     * return: A GameObject representing a leaf.
     */
    private GameObject createLeaf() {
        Renderable leafRenderer = new RectangleRenderable(LEAF_COLOR);
        GameObject leaf = new GameObject(Vector2.ZERO, new Vector2(LEAF_SIZE, LEAF_SIZE), leafRenderer);
        leaf.setCenter(getRandomPositionOnTop());
        leaf.setTag(LEAF_TAG);

        float delayTime = random.nextFloat(MAX_TRANSITION_DELAY_TIME); // Random delay before swaying starts

        // Define swaying behavior
        Runnable createLeafTransitions = () -> {
            new Transition<>(
                    leaf,
                    value -> leaf.setDimensions(new Vector2(value, leaf.getDimensions().y())), //Adjust width
                    leaf.getDimensions().x() * LEAF_SWAY_MIN_WIDTH_FACTOR,
                    leaf.getDimensions().x() * LEAF_SWAY_MAX_WIDTH_FACTOR,
                    Transition.LINEAR_INTERPOLATOR_FLOAT,
                    LEAF_SWAY_TIME,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null
            );

            new Transition<>(
                    leaf,
                    leaf.renderer()::setRenderableAngle, // Adjust angle
                    LEAF_SWAY_MIN_ROTATION,
                    LEAF_SWAY_MAX_ROTATION,
                    Transition.LINEAR_INTERPOLATOR_FLOAT,
                    LEAF_SWAY_TIME,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null
            );
        };

        new ScheduledTask(leaf, delayTime, false, createLeafTransitions
        ); // Schedule the swaying behavior after the random delay to create more realistic look

        return leaf;
    }

    /*
     * Creates a single fruit GameObject that can regenerate after being "eaten."
     *
     * return: A GameObject representing a fruit.
     */
    private GameObject createFruit() {
        Renderable fruitRenderable = new OvalRenderable(FRUIT_COLOR);
        GameObject fruit =
                new GameObject(Vector2.ZERO, new Vector2(FRUIT_SIZE, FRUIT_SIZE), fruitRenderable){
            @Override
            public void onCollisionEnter(GameObject other, Collision collision) {
                super.onCollisionEnter(other, collision);
                renderer().setRenderable(null); // Hide the fruit

                // Schedule the fruit to regenerate after the cycle length
                new ScheduledTask(
                        this,
                        CYCLE_LENGTH,
                        false,
                        () -> {
                            setTag(FRUIT_TAG); // Mark the fruit as "not eaten"
                            renderer().setRenderable(fruitRenderable);
                        }
                );
            }
        };

        do {
            fruit.setCenter(getRandomPositionOnTop()); // Position the fruit
                                                        // randomly the tree's top but not on trunk
        } while (isFruitOnTrunk(fruit));

        fruit.setTag(FRUIT_TAG); // Set fruit tag for identification

        return fruit;
    }

    /*
     * Checks if a given fruit is positioned on the trunk of the tree.
     */
    private boolean isFruitOnTrunk(GameObject fruit) {
        // Check if the fruit's vertical position is above the trunk's top
        if (fruit.getCenter().y() > trunk.getTopLeftCorner().y()) {
            return false;
        }

        // Check if the fruit's horizontal position is outside the trunk's width
        if (fruit.getCenter().x() > trunk.getCenter().x() + trunk.getDimensions().x() * HALF_FACTOR ||
                fruit.getCenter().x() < trunk.getCenter().x() - trunk.getDimensions().x() * HALF_FACTOR) {
            return false;
        }
        return true;
    }

    /*
     * Returns a random position on the top area of the tree for placing leaves and fruits.
     *
     * return: A Vector2 representing a random position on the tree's top.
     */
    private Vector2 getRandomPositionOnTop() {
        float trunkY = trunk.getTopLeftCorner().y();
        float trunkX = trunk.getCenter().x();
        float randomX = random.nextFloat(trunkX - TREE_TOP_SIZE * HALF_FACTOR,
                trunkX + TREE_TOP_SIZE * HALF_FACTOR); // Random X-coordinate within the
                                                                // tree's top width
        float randomY = random.nextFloat(trunkY - TREE_TOP_SIZE + TOP_POSITION_Y_FACTOR,
                trunkY + TOP_POSITION_Y_FACTOR); // Random Y-coordinate within the tree's top height
        return new Vector2(randomX, randomY);
    }
}