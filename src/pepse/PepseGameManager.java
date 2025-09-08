package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;
import pepse.world.Sky;
import pepse.world.trees.Tree;

import java.util.*;

import static pepse.util.Constants.CHUNK_SIZE;
import static pepse.util.Constants.*;

/**
 * PepseGameManager is the main class for managing the game loop and initializing all game components.
 * It handles the creation of terrain, flora, the avatar, sky, and lighting effects,
 * as well as dynamically managing terrain chunks as the avatar moves.
 *
 * @author Yair Ben Yakar, Ofek Levy
 */
public class PepseGameManager extends GameManager {


    private int currentLeftChunk; // Index of the leftmost loaded chunk
    private int currentRightChunk; // Index of the rightmost loaded chunk
    private Terrain terrain; // Terrain object responsible for terrain generation
    private Avatar avatar; // The player's avatar
    private Flora flora; // Flora object responsible for tree generation
    private Map<Integer, List<Block>> chunkBlocks;
    private Map<Integer, List<Tree>> chunkTrees;


    /**
     * Constructor for PepseGameManager.
     *
     * @param windowTitle The title of the game window.
     * @param windowDimensions The dimensions of the game window.
     */
    public PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    /**
     * Initializes all game components, including terrain, avatar, sky, and lighting effects.
     * Also sets up initial terrain chunks and the camera to follow the avatar.
     *
     * @param imageReader Provides functionality for reading images from files.
     * @param soundReader Provides functionality for reading sound effects from files.
     * @param inputListener Handles user input events.
     * @param windowController Controls the game window properties and behavior.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        Vector2 windowDimensions = windowController.getWindowDimensions();
        // Set the target framerate
        windowController.setTargetFramerate(TARGET_FRAMERATE);

        // Create seed
        Random random = new Random();
        int seed = random.nextInt(MIN_SEED, MAX_SEED);

        // Init chunks
        chunkTrees = new HashMap<>();
        chunkBlocks = new HashMap<>();

        // Init Layers
        gameObjects().layers().shouldLayersCollide(TRUNK_LAYER, Layer.DEFAULT, true);
        gameObjects().layers().shouldLayersCollide(FRUIT_LAYER, Layer.DEFAULT, true);


        // Create the sky
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);

        // Create the sun and its halo
        GameObject sun = Sun.create(windowDimensions, CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);

        // Create clouds
        Cloud cloud = new Cloud(windowDimensions, gameObjects()::removeGameObject,
                gameObjects()::addGameObject);
        cloud.addCloud();

        // Create night
        GameObject night = Night.create(windowDimensions, CYCLE_LENGTH);
        gameObjects().addGameObject(night, NIGHT_LAYER);

        // Create terrain and Flora
        terrain = new Terrain(windowDimensions, seed);
        flora = new Flora(terrain::groundHeightAt, seed);


        // Generate initial terrain and flora
        List<Block> blocks = terrain.createInRange(INITIAL_MIN_X, INITIAL_MAX_X);
        List<Tree> trees = flora.createInRange(INITIAL_MIN_X, INITIAL_MAX_X);
        addTreesAndTerrain(blocks, trees);

        // Set initial chunk indices
        currentLeftChunk = INITIAL_LEFT_CHUNK;
        currentRightChunk = INITIAL_RIGHT_CHUNK;

        // Create the avatar
        avatar = new Avatar(new Vector2(windowDimensions.x() / 2,
                windowDimensions.y() / 4), inputListener,
                imageReader, cloud::makeRain);
        gameObjects().addGameObject(avatar, Layer.DEFAULT);

        // Set the camera to follow the avatar
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));

        // Create the energy level indicator
        EnergyLevelIndicator energyLevelIndicator = new EnergyLevelIndicator(
                ENERGY_INDICATOR_POSITION, ENERGY_INDICATOR_SIZE,
                new TextRenderable("ENERGY:", "Courier New", false, true),
                avatar::getEnergy,
                gameObjects()::addGameObject,
                Layer.UI
        );

        gameObjects().addGameObject(energyLevelIndicator, Layer.UI);
    }

    private void addTreesAndTerrain(List<Block> blocks, List<Tree> trees) {
        for (Block block : blocks) {
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
            int chunkIndex = Math.floorDiv((int) block.getTopLeftCorner().x(), CHUNK_SIZE);
            chunkBlocks.computeIfAbsent(chunkIndex, k -> new ArrayList<>()).add(block);
        }
        for (Tree tree : trees) {
            tree.removeOrAddTreeFromGame(gameObjects()::addGameObject);
            int chunkIndex = Math.floorDiv((int) tree.getTrunkCenter().x(), CHUNK_SIZE);
            chunkTrees.computeIfAbsent(chunkIndex, k -> new ArrayList<>()).add(tree);
        }
    }

    private boolean chunkExists(int chunkIndex) {
        return chunkBlocks.containsKey(chunkIndex);
    }

    /**
     * Adds a new terrain chunk if needed, based on the avatar's position.
     * Removes the farthest chunk on the opposite side to free memory.
     *
     * @param avatarPosition The current position of the avatar.
     * @param isRight If true, checks for a new chunk on the right; otherwise, on the left.
     */
    private void addChunkIfNeeded(Vector2 avatarPosition, boolean isRight) {
        int avatarChunk = (int) Math.floor(avatarPosition.x() / CHUNK_SIZE);

        if ((isRight && avatarChunk >= currentRightChunk - 1) ||
                (!isRight && avatarChunk <= currentLeftChunk + 1)) {

            int newChunk = isRight ? currentRightChunk + 1 : currentLeftChunk - 1;
            int minX = newChunk * CHUNK_SIZE;
            int maxX = minX + CHUNK_SIZE;

            // Generate the new chunk if it doesn't exist
            if (!chunkExists(newChunk)) {
                List<Block> blocks = terrain.createInRange(minX, maxX);
                List<Tree> trees = flora.createInRange(minX, maxX);
                addTreesAndTerrain(blocks, trees);
            } else {
                // Remove the farthest opposite chunk to free memory
                int farChunk = isRight ? currentLeftChunk - 1 : currentRightChunk + 1;
                removeChunk(farChunk);
            }

            // Update chunk indices
            if (isRight) {
                currentRightChunk = newChunk;
                currentLeftChunk++;
            } else {
                currentLeftChunk = newChunk;
                currentRightChunk--;
            }
        }
    }

    private void removeChunk(int chunkIndex) {
        // Retrieve the blocks associated with the chunk index
        List<Tree> trees = chunkTrees.get(chunkIndex);
        List<Block> blocks = chunkBlocks.get(chunkIndex);


        if (trees != null) {
            // Remove each block from the game objects
            for (Tree tree : trees) {
                tree.removeOrAddTreeFromGame(gameObjects()::removeGameObject);
            }

            // Remove the chunk from the map
            chunkTrees.remove(chunkIndex);
        }

        if (blocks != null) {
            // Remove each block from the game objects
            for (Block block : blocks) {
                gameObjects().removeGameObject(block, Layer.STATIC_OBJECTS);
            }

            // Remove the chunk from the map
            chunkBlocks.remove(chunkIndex);
        }
    }


        /**
         * Updates the game state. Dynamically manages terrain chunks based on the avatar's position.
         *
         * @param deltaTime Time elapsed since the last frame, in seconds.
         */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 avatarPosition = avatar.getCenter();
        addChunkIfNeeded(avatarPosition, false); // For the left side
        addChunkIfNeeded(avatarPosition, true);  // For the right side
    }

    /**
     * The main method to run the Pepse game.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        String windowTitle = "Pepse";
        Vector2 windowDimensions = new Vector2(1600 , 768);
        new PepseGameManager(windowTitle, windowDimensions).run();
    }
}
