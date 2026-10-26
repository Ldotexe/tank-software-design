package ru.mipt.bit.platformer.graphics.util;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class GdxGameUtilsTest {
    @Test
    public void returnsOnlyTileLayer() {
        Map map = new Map();
        TiledMapTileLayer layer = new TiledMapTileLayer(10, 8, 128, 128);
        map.getLayers().add(layer);
        assertSame(layer, GdxGameUtils.getOnlyTileLayer(map));
    }

    @Test
    public void rejectsMissingMultipleAndNonTileLayers() {
        Map map = new Map();
        assertThrows(NoSuchElementException.class, () -> GdxGameUtils.getOnlyTileLayer(map));
        map.getLayers().add(new MapLayer());
        assertThrows(IllegalArgumentException.class, () -> GdxGameUtils.getOnlyTileLayer(map));
        map.getLayers().add(new MapLayer());
        assertThrows(IllegalArgumentException.class, () -> GdxGameUtils.getOnlyTileLayer(map));
    }

    @Test
    public void centersBoundsOnNonSquareTileWithoutChangingSize() {
        Rectangle bounds = new Rectangle(99, 99, 20, 10);
        assertSame(bounds, GdxGameUtils.centerSpriteOnTile(
                new TiledMapTileLayer(10, 8, 128, 64), bounds, new GridPoint2(2, 3)));
        assertEquals(310f, bounds.x, 0f);
        assertEquals(219f, bounds.y, 0f);
        assertEquals(20f, bounds.width, 0f);
        assertEquals(10f, bounds.height, 0f);
    }
}
