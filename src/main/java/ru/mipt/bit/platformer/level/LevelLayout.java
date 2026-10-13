package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class LevelLayout {
    private final int width;
    private final int height;
    private final List<Tree> trees;
    private final GridPoint2 playerStart;

    public LevelLayout(int width, int height, List<Tree> trees, GridPoint2 playerStart) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.playerStart = new GridPoint2(Objects.requireNonNull(playerStart));
        requireInsideLevel(this.playerStart);
        List<Tree> treeList = new ArrayList<>(Objects.requireNonNull(trees));
        Set<GridPoint2> occupiedTiles = new HashSet<>();
        for (Tree tree : treeList) {
            GridPoint2 position = Objects.requireNonNull(tree).getTilePosition();
            requireInsideLevel(position);
            if (!occupiedTiles.add(position)) {
                throw new IllegalArgumentException("Multiple trees at " + position);
            }
            if (position.equals(this.playerStart)) {
                throw new IllegalArgumentException("Player start is occupied by a tree");
            }
        }
        this.trees = Collections.unmodifiableList(treeList);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Tree> getTrees() {
        return trees;
    }

    public GridPoint2 getPlayerStart() {
        return new GridPoint2(playerStart);
    }

    private void requireInsideLevel(GridPoint2 position) {
        if (position.x < 0 || position.x >= width || position.y < 0 || position.y >= height) {
            throw new IllegalArgumentException("Tile outside level: " + position);
        }
    }
}
