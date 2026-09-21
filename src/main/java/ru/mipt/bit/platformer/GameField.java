package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameField {
    private final List<Tree> trees = new ArrayList<>();

    public void addTree(int tileX, int tileY) {
        trees.add(new Tree(tileX, tileY));
    }

    public List<Tree> getTrees() {
        return Collections.unmodifiableList(trees);
    }

    public boolean isTileUnoccupied(GridPoint2 destinationTile) {
        for (Tree tree : trees) {
            if (tree.occupiesTile(destinationTile)) {
                return false;
            }
        }
        return true;
    }
}
