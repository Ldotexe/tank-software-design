package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class RandomLevelSource implements LevelSource {
    private final int width;
    private final int height;
    private final int treeCount;
    private final Random random;

    public RandomLevelSource(int width, int height, int treeCount) {
        this(width, height, treeCount, new Random());
    }

    public RandomLevelSource(int width, int height, int treeCount, Random random) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        long tileCount = (long) width * height;
        if (tileCount > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Level contains too many tiles");
        }
        if (treeCount < 0 || treeCount >= tileCount) {
            throw new IllegalArgumentException("Tree count must leave at least one free tile");
        }
        this.width = width;
        this.height = height;
        this.treeCount = treeCount;
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public LevelLayout load() {
        List<GridPoint2> tiles = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles.add(new GridPoint2(x, y));
            }
        }
        Collections.shuffle(tiles, random);
        List<Tree> trees = new ArrayList<>(treeCount);
        for (int index = 0; index < treeCount; index++) {
            GridPoint2 position = tiles.get(index);
            trees.add(new Tree(position.x, position.y));
        }
        return new LevelLayout(width, height, trees, tiles.get(treeCount));
    }
}
