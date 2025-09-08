package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import static pepse.util.Constants.*;


/**
 * The Block class represents a single terrain block in the game world.
 * Each block has a fixed size and is immovable. The block is rendered using a provided renderable object
 * and is part of the terrain generation system.
 *
 * @author Yair Ben Yakar, Ofek Levy
 */
public class Block extends GameObject {

    /**
     * Constructor for the Block class.
     * Initializes the block at the specified top-left corner, assigns it a renderable object,
     * and sets its physics properties (immovable with no intersection).
     *
     * @param topLeftCorner The position of the block's top-left corner in the world coordinates.
     * @param renderable The renderable object used to display the block.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        // Call the superclass constructor with position, size, and renderable
        super(topLeftCorner, Vector2.ONES.mult(BLOCK_SIZE), renderable);

        // Prevent the block from intersecting with other objects in any direction
        physics().preventIntersectionsFromDirection(Vector2.ZERO);

        // Set the block as immovable by assigning an immovable mass
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

        setTag("block");
    }
}
