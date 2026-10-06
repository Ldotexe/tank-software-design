package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class TreeTest {
    @Test
    public void occupiesOnlyItsOwnTile() {
        Tree tree = new Tree(1, 3);
        assertTrue(tree.occupiesTile(new GridPoint2(1, 3)));
        assertFalse(tree.occupiesTile(new GridPoint2(1, 2)));
        assertFalse(tree.occupiesTile(new GridPoint2(2, 3)));
    }

    @Test
    public void changingReturnedPositionDoesNotMoveObstacle() {
        Tree tree = new Tree(1, 3);
        tree.getTilePosition().set(2, 3);
        assertTrue(tree.occupiesTile(new GridPoint2(1, 3)));
    }
}
