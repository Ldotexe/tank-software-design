package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameFieldTest {
    @Test
    public void emptyFieldHasNoObstacles() {
        GameField field = new GameField();
        assertTrue(field.getTrees().isEmpty());
        assertTrue(field.isTileUnoccupied(new GridPoint2(1, 3)));
    }

    @Test
    public void checksAllTreesAndLeavesOtherTilesFree() {
        GameField field = new GameField();
        field.addTree(1, 3);
        field.addTree(4, 5);
        assertEquals(2, field.getTrees().size());
        assertFalse(field.isTileUnoccupied(new GridPoint2(1, 3)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(4, 5)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(2, 3)));
    }

    @Test
    public void preservesExistingMovementOutsideVisibleMap() {
        assertTrue(new GameField().isTileUnoccupied(new GridPoint2(-1, 20)));
    }

    @Test
    public void callersCannotModifyTreeCollection() {
        GameField field = new GameField();
        field.addTree(1, 3);
        assertThrows(UnsupportedOperationException.class, () -> field.getTrees().clear());
        assertEquals(1, field.getTrees().size());
    }
}
