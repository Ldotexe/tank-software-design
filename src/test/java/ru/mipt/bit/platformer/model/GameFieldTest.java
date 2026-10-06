package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameFieldTest {
    @Test
    public void emptyFieldHasNoObstacles() {
        GameField field = new GameField();
        assertTrue(field.getObstacles().isEmpty());
        assertTrue(field.isTileUnoccupied(new GridPoint2(1, 3)));
    }

    @Test
    public void checksAllObstaclesAndLeavesOtherTilesFree() {
        GameField field = new GameField();
        field.addObstacle(new Tree(1, 3));
        field.addObstacle(new Tree(4, 5));
        assertEquals(2, field.getObstacles().size());
        assertFalse(field.isTileUnoccupied(new GridPoint2(1, 3)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(4, 5)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(2, 3)));
    }

    @Test
    public void preservesExistingMovementOutsideVisibleMap() {
        assertTrue(new GameField().isTileUnoccupied(new GridPoint2(-1, 20)));
    }

    @Test
    public void callersCannotModifyObstacleCollection() {
        GameField field = new GameField();
        field.addObstacle(new Tree(1, 3));
        assertThrows(UnsupportedOperationException.class, () -> field.getObstacles().clear());
        assertEquals(1, field.getObstacles().size());
    }

    @Test
    public void supportsOtherObstacleTypesAndShapes() {
        GameField field = new GameField();
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
        assertThrows(NullPointerException.class, () -> new GameField().addObstacle(null));
    }
}
