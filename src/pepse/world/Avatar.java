package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;


import static pepse.util.Constants.*;

/**
 * The Avatar class represents the player character in the game, providing movement, animation,
 * and energy management.
 * It handles user input to control the avatar's movement (left, right, jump) and updates the avatar's
 * energy level
 * based on actions. The class also manages the avatar's animation states such as idle, running, and
 * jumping.
 *
 * @author Yair Ben Yakar, Ofek Levy
 */
public class Avatar extends GameObject {

    // Constants for physics and avatar properties
    private static final float GRAVITY = 1000; // The force of gravity applied to the avatar
    private static final float RUN_VELOCITY = 400; // The velocity at which the avatar runs
    private static final float JUMP_VELOCITY = -500; // The velocity when the avatar jumps
    public static final int MAX_ENERGY = 500; // Maximum energy the avatar can have
    private static final String PREFIX = "_"; // Prefix for the sprite file names
    private static final String FILE_NAME = ".png"; // Extension for sprite files
    private static final float MOVE_ENERGY_COST = 0.5f; // Energy cost for movement
    private static final float JUMP_ENERGY_COST = 10f;  // Energy cost for jumping

    private final UserInputListener inputListener; // Listener for user input (keyboard)

    private float energy = MAX_ENERGY; // The current energy of the avatar

    private final AnimationRenderable idleAnimation; // The idle animation of the avatar
    private final AnimationRenderable runAnimation; // The running animation of the avatar
    private final AnimationRenderable jumpAnimation; // The jumping animation of the avatar
    private final Runnable updateJump; // Runnable to update jump-related logic

    /**
     * Gets the current energy of the avatar.
     *
     * @return The energy level of the avatar, rounded down to the nearest integer.
     */
    public int getEnergy() {
        return (int) Math.floor(energy);
    }

    /**
     * Constructs an Avatar object with the specified starting position and input listener.
     * Initializes animations, applies gravity, and sets up energy management.
     *
     * @param topLeftCorner The top-left corner position of the avatar.
     * @param inputListener The listener to handle user input.
     * @param imageReader   The image reader used to load sprite images for animations.
     * @param updateJump    Runnable to execute when a jump event occurs.
     */
    public Avatar(Vector2 topLeftCorner,
                  UserInputListener inputListener,
                  ImageReader imageReader,
                  Runnable updateJump) {
        super(topLeftCorner, new Vector2(50, 78), null); // Placeholder for renderable

        // Initialize animations
        this.idleAnimation = createAnimation(imageReader, "assets/idle", 4, 0, 0.2f);
        this.runAnimation = createAnimation(imageReader, "assets/run", 6, 0, 0.2f);
        this.jumpAnimation = createAnimation(imageReader, "assets/jump", 4, 0, 0.2f);

        this.inputListener = inputListener;
        this.updateJump = updateJump;
        this.energy = MAX_ENERGY; // Initialize energy

        // Set the default renderable to idle animation
        renderer().setRenderable(idleAnimation);

        // Apply gravity
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
    }

    /**
     * Creates an animation using a series of frames loaded from the specified path.
     *
     * @param imageReader  The image reader used to load the animation frames.
     * @param path         The path to the image files.
     * @param frameCount   The number of frames in the animation.
     * @param startIndex   The starting index for the animation frames.
     * @param frameTime    The time each frame is displayed.
     * @return The AnimationRenderable representing the animation.
     */
    private AnimationRenderable createAnimation(ImageReader imageReader, String path, int frameCount,
                                                int startIndex, float frameTime) {
        Renderable[] frames = new Renderable[frameCount];
        for (int i = startIndex; i < frameCount + startIndex; i++) {
            frames[i] = imageReader.readImage(path + PREFIX + i + FILE_NAME, true);
        }
        return new AnimationRenderable(frames, frameTime);
    }

    /**
     * Updates the avatar's position, animation, and energy based on user input.
     * Handles movement (left, right), jumping, and idle states. Adjusts the energy
     * of the avatar based on actions and recovers energy when idle.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Handle user input for movement and jumping
        float xVel = 0;
        boolean moveLeft = inputListener.isKeyPressed(java.awt.event.KeyEvent.VK_LEFT);
        boolean moveRight = inputListener.isKeyPressed(java.awt.event.KeyEvent.VK_RIGHT);
        boolean jump = inputListener.isKeyPressed(java.awt.event.KeyEvent.VK_SPACE) ||
                inputListener.isKeyPressed(KeyEvent.VK_UP); // Jump key pressed
        boolean notInTheAir = getVelocity().y() == 0; // Check if avatar is on the ground

        // Handle conflicting inputs: both left and right pressed
        if (moveLeft && moveRight) {
            xVel = 0; // Stay in place
            if (renderer().getRenderable() != idleAnimation) {
                renderer().setRenderable(idleAnimation); // Set idle animation
            }
            if (energy < MAX_ENERGY) {
                energy += 1; // Recover energy
            }
        }

        // Move left
        if (moveLeft && !moveRight && energy > MOVE_ENERGY_COST) {
            xVel -= RUN_VELOCITY;
            if (renderer().getRenderable() != runAnimation) {
                renderer().setRenderable(runAnimation); // Set run animation
            }
            renderer().setIsFlippedHorizontally(true);
            energy -= MOVE_ENERGY_COST; // Reduce energy
        }

        // Move right
        if (moveRight && !moveLeft && energy > MOVE_ENERGY_COST) {
            xVel += RUN_VELOCITY;
            if (renderer().getRenderable() != runAnimation) {
                renderer().setRenderable(runAnimation); // Set run animation
            }
            renderer().setIsFlippedHorizontally(false);
            energy -= MOVE_ENERGY_COST; // Reduce energy
        }

        transform().setVelocityX(xVel);

        // Jump if space is pressed and not in the air
        if (jump && notInTheAir && energy > JUMP_ENERGY_COST) {
            updateJump.run();
            transform().setVelocityY(JUMP_VELOCITY);
            if (!moveRight && !moveLeft) {
                if (renderer().getRenderable() != jumpAnimation) {
                    renderer().setRenderable(jumpAnimation); // Set jump animation
                }
            }

            energy -= JUMP_ENERGY_COST; // Reduce energy
        }

        // Set idle animation if no movement or jump
        if (!moveLeft && !moveRight && !jump && notInTheAir) {
            if (renderer().getRenderable() != idleAnimation) {
                renderer().setRenderable(idleAnimation); // Set idle animation
            }
            if (energy < MAX_ENERGY) {
                energy += 1; // Recover energy
            }
        }
    }

    /**
     * Handles collision events for the avatar. If the avatar collides with a "Fruit" object,
     * its energy level increases.
     *
     * @param other     The other game object involved in the collision.
     * @param collision The collision details.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (other.getTag().equals(FRUIT_TAG)) {
            other.setTag(EATEN_FRUIT_TAG); // Mark the fruit as "eaten"
            if (energy >= MAX_ENERGY - FRUIT_ENERGY_BOOST) {//making sure we don't go above max energy
                energy = MAX_ENERGY;
                return;
            }
            energy += FRUIT_ENERGY_BOOST; // Increase energy upon collecting a fruit
        }
        if(other.getTag().equals(BLOCK_TAG)){
            this.transform().setVelocityY(0);
        }
    }
}
