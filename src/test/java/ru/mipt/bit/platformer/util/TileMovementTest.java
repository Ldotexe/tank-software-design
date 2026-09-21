package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import org.junit.Test;

import static org.junit.Assert.*;

public class TileMovementTest {
    @Test
    public void placesSpriteAtStartMidpointAndDestination() {
        TileMovement movement = new TileMovement(new TiledMapTileLayer(10, 8, 128, 64), Interpolation.smooth);
        Rectangle sprite = new Rectangle(0, 0, 32, 16);
        GridPoint2 start = new GridPoint2(1, 1);
        GridPoint2 destination = new GridPoint2(2, 2);
        movement.interpolateSpritePosition(sprite, start, destination, 0f);
        assertPosition(sprite, 176f, 88f);
        movement.interpolateSpritePosition(sprite, start, destination, 0.5f);
        assertPosition(sprite, 240f, 120f);
        movement.interpolateSpritePosition(sprite, start, destination, 1f);
        assertPosition(sprite, 304f, 152f);
        assertEquals(32f, sprite.width, 0f);
        assertEquals(16f, sprite.height, 0f);
        assertEquals(new GridPoint2(1, 1), start);
        assertEquals(new GridPoint2(2, 2), destination);
    }

    @Test
    public void usesSuppliedInterpolationForReverseMovement() {
        TileMovement movement = new TileMovement(new TiledMapTileLayer(10, 8, 128, 128), Interpolation.smooth);
        Rectangle sprite = new Rectangle(0, 0, 32, 32);
        movement.interpolateSpritePosition(sprite, new GridPoint2(1, 1), new GridPoint2(0, 1), 0.25f);
        // Smoothstep at 0.25 is 0.15625; 176 - 128 * 0.15625 = 156.
        assertPosition(sprite, 156f, 176f);
    }

    @Test
    public void stationaryObjectRemainsCentered() {
        TileMovement movement = new TileMovement(new TiledMapTileLayer(10, 8, 128, 128), Interpolation.smooth);
        Rectangle sprite = new Rectangle(0, 0, 32, 32);
        GridPoint2 position = new GridPoint2(1, 3);
        movement.interpolateSpritePosition(sprite, position, position, 0.5f);
        assertPosition(sprite, 176f, 432f);
    }

    private void assertPosition(Rectangle sprite, float x, float y) {
        assertEquals(x, sprite.x, 0.00001f);
        assertEquals(y, sprite.y, 0.00001f);
    }
}
