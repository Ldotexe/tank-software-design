package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameObjectTest {
    @Test
    public void storesTilePositionWithoutExposingMutableState() {
        GameObject object = new GameObject(2, 3) { };
        object.getTilePosition().set(99, 99);
        assertEquals(new GridPoint2(2, 3), object.getTilePosition());
    }

    @Test
    public void movingCopiesDestinationCoordinates() {
        GameObject object = new GameObject(2, 3) { };
        GridPoint2 destination = new GridPoint2(4, 5);
        object.moveToTile(destination);
        destination.set(99, 99);
        assertEquals(new GridPoint2(4, 5), object.getTilePosition());
    }
}
