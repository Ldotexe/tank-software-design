package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class DirectionTest {
    @Test
    public void eachDirectionHasExpectedVectorAngleAndAdjacentTile() {
        assertDirection(Direction.UP, 0, 1, 90f);
        assertDirection(Direction.LEFT, -1, 0, -180f);
        assertDirection(Direction.DOWN, 0, -1, -90f);
        assertDirection(Direction.RIGHT, 1, 0, 0f);
    }

    private void assertDirection(Direction direction, int dx, int dy, float angle) {
        GridPoint2 origin = new GridPoint2(4, 5);
        assertEquals(new GridPoint2(dx, dy), direction.getVector());
        assertEquals(angle, direction.getRotationDegrees(), 0f);
        assertEquals(new GridPoint2(4 + dx, 5 + dy), direction.calculateAdjacentTile(origin));
        assertEquals(new GridPoint2(4, 5), origin);
    }

    @Test
    public void returnedVectorsCannotChangeEnumState() {
        Direction.UP.getVector().set(99, 99);
        assertEquals(new GridPoint2(0, 1), Direction.UP.getVector());
    }
}
