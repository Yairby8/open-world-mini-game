package pepse.world;


import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


import static pepse.util.Constants.BLOCK_SIZE;
import static pepse.util.Constants.CHUNK_SIZE;

/**
 * The Terrain class generates and manages the terrain blocks in the game world.
 * The terrain is dynamically generated based on pseudo-random noise, and it
 * allows the creation of terrain blocks in a specified range, simulating
 * a natural-looking ground surface with depth.
 *
 * @author Yair Ben Yakar, Ofek Levy
 */
public class Terrain {
    private static final int TERRAIN_DEPTH = 20; // Number of blocks below the surface
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74); // Base color for ground
    private static final float GROUND_HEIGHT_FACTOR = (float) 2 / 3; // Ground height as a fraction of screen
    // height
    private static final int NOISE_FACTOR = 7; // Factor to scale the noise variation

    private final NoiseGenerator noiseGenerator; // Noise generator used to create random terrain variations
    private final float groundHeightAtX0; // Base ground height at x=0

    /**
     * Constructor for Terrain.
     * Initializes the terrain based on the window dimensions and
     * a provided seed for random number generation.
     *
     * @param windowDimensions The dimensions of the game window, used to calculate the ground height at x=0.
     * @param seed The seed for the noise generator to ensure deterministic random terrain generation.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = windowDimensions.y() * GROUND_HEIGHT_FACTOR; // Default ground height is 2/3
        // of the screen height
        this.noiseGenerator = new NoiseGenerator(seed, (int) groundHeightAtX0); // Initialize the noise
        // generator
    }

    /**
     * Returns the ground height at a specific x-coordinate using pseudo-random noise.
     * The height is adjusted by the noise factor to simulate natural terrain variation.
     *
     * @param x The x-coordinate to calculate the ground height for.
     * @return The ground height at the given x-coordinate, with noise variation.
     */
    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, BLOCK_SIZE * NOISE_FACTOR); // Adjust factor for
        // desired variation
        return groundHeightAtX0 + noise; // The base ground height plus the noise variation
    }

    /**
     * Creates terrain blocks in a specified x-coordinate range.
     * The blocks are generated starting from the ground level and extend below the surface
     * to create depth, simulating a 2D world with a solid terrain.
     *
     * @param minX The starting x-coordinate for terrain generation.
     * @param maxX The ending x-coordinate for terrain generation.
     * @return A list of terrain blocks created within the specified range.
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();

        // Generate blocks for the range of x-coordinates
        for (int x = minX; x < maxX; x += BLOCK_SIZE) {
            float groundHeight = groundHeightAt(x); // Get the height of the ground at this x-coordinate
            // Generate blocks from the ground height down to create terrain depth
            for (int y = (int) groundHeight; y < groundHeight + TERRAIN_DEPTH * BLOCK_SIZE; y += BLOCK_SIZE){
                Renderable groundRenderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)); //
                // Renderable for terrain blocks
                Vector2 position = new Vector2(x, y); // Position of the block
                Block block = new Block(position, groundRenderable); // Create the block
                blocks.add(block); // Add the block to the list

                int chunkIndex = Math.floorDiv(x, CHUNK_SIZE);
            }
        }
        return blocks; // Return the list of generated blocks
    }
}
