package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameFieldTest {
    @Test
    public void emptyFieldHasNoObstacles() {
        GameField field = new GameField(10, 8);
        assertTrue(field.getObstacles().isEmpty());
        assertTrue(field.isTileUnoccupied(new GridPoint2(1, 3)));
    }

    @Test
    public void checksAllObstaclesAndLeavesOtherTilesFree() {
        GameField field = new GameField(10, 8);
        field.addObstacle(new Tree(1, 3));
        field.addObstacle(new Tree(4, 5));
        assertEquals(2, field.getObstacles().size());
        assertFalse(field.isTileUnoccupied(new GridPoint2(1, 3)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(4, 5)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(2, 3)));
    }

    @Test
    public void storesFieldDimensions() {
        GameField field = new GameField(5, 3);
        assertEquals(5, field.getWidth());
        assertEquals(3, field.getHeight());
    }

    @Test
    public void blocksTilesOutsideAllFourBoundaries() {
        GameField field = new GameField(5, 3);
        assertFalse(field.isTileUnoccupied(new GridPoint2(-1, 1)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(5, 1)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(2, -1)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(2, 3)));
    }

    @Test
    public void allowsCornerTilesInsideRectangularField() {
        GameField field = new GameField(5, 3);
        assertTrue(field.isTileUnoccupied(new GridPoint2(0, 0)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(4, 0)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(0, 2)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(4, 2)));
    }

    @Test
    public void rejectsNonPositiveDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new GameField(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new GameField(5, 0));
        assertThrows(IllegalArgumentException.class, () -> new GameField(-1, 3));
        assertThrows(IllegalArgumentException.class, () -> new GameField(5, -1));
    }

    @Test
    public void callersCannotModifyObstacleCollection() {
        GameField field = new GameField(10, 8);
        field.addObstacle(new Tree(1, 3));
        assertThrows(UnsupportedOperationException.class, () -> field.getObstacles().clear());
        assertEquals(1, field.getObstacles().size());
    }

    @Test
    public void supportsOtherObstacleTypesAndShapes() {
        GameField field = new GameField(10, 8);
        field.addObstacle(new Tree(1, 3));
        Obstacle wall = tile -> tile.x == 4 && tile.y >= 1 && tile.y <= 3;
        field.addObstacle(wall);
        assertFalse(field.isTileUnoccupied(new GridPoint2(1, 3)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(4, 1)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(4, 3)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(4, 4)));
    }

    @Test
    public void rejectsNullObstacle() {
        assertThrows(NullPointerException.class, () -> new GameField(10, 8).addObstacle(null));
    }
}
