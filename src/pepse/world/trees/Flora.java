package pepse.world.trees;


import java.util.*;
import java.util.function.Function;


/**
 * Class responsible for generating and managing trees, leaves, and fruits in the game world.
 */
public class Flora {
    private static final int TREE_SPACING = 100; // Distance between trees
    private static final float CHANCE_TO_PLACE_A_TREE = 0.1f; // Seconds


    private final int seed;
    private final Function<Float, Float> getGroundHeight;

    /**
     * Constructs a Flora object for generating vegetation.
     */
    public Flora(Function<Float, Float> getGroundHeight, int seed) {
        this.getGroundHeight = getGroundHeight;
        this.seed = seed;
    }

    /**
     * Creates trees in the specified X range.
     *
     * @param minX The starting X coordinate for the trees.
     * @param maxX The ending X coordinate for the trees.
     */
    public List<Tree> createInRange(int minX, int maxX) {
        List<Tree> trees = new ArrayList<>();

        int key = Objects.hash(minX, maxX, seed);
        Random random = new Random(key);
        int treeCount = (maxX - minX) / TREE_SPACING;

        for (int i = 0; i < treeCount; i++) {
            float x = minX + i * TREE_SPACING;
            if (random.nextFloat() <= CHANCE_TO_PLACE_A_TREE) {
                Tree tree = new Tree(
                        x,
                        getGroundHeight.apply(x),
                        seed
                );
                trees.add(tree);
            }
        }
        return trees;
    }
}
