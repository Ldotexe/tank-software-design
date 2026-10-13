package ru.mipt.bit.platformer.graphics.util;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.junit.Test;

import static org.junit.Assert.*;

public class TileLayerResizerTest {
    @Test
    public void repeatsTemplateToFillLargerLevelWithoutChangingTileSize() {
        TiledMapTileLayer source = new TiledMapTileLayer(2, 2, 128, 64);
        source.setName("Ground");
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                source.setCell(x, y, new TiledMapTileLayer.Cell());
            }
        }
        TiledMapTileLayer result = TileLayerResizer.resize(source, 5, 3);
        assertEquals(5, result.getWidth());
        assertEquals(3, result.getHeight());
        assertEquals(128f, result.getTileWidth(), 0f);
        assertEquals(64f, result.getTileHeight(), 0f);
        assertEquals("Ground", result.getName());
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 3; y++) {
                assertSame(source.getCell(x % 2, y % 2), result.getCell(x, y));
            }
        }
        assertEquals(2, source.getWidth());
        assertEquals(2, source.getHeight());
    }

    @Test
    public void cropsTemplateForSmallerLevel() {
        TiledMapTileLayer source = new TiledMapTileLayer(10, 8, 128, 128);
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        source.setCell(0, 0, cell);
        TiledMapTileLayer result = TileLayerResizer.resize(source, 3, 2);
        assertEquals(3, result.getWidth());
        assertEquals(2, result.getHeight());
        assertSame(cell, result.getCell(0, 0));
        assertNull(result.getCell(2, 1));
    }

    @Test
    public void rejectsEmptyDimensionsAndEmptyTemplate() {
        TiledMapTileLayer source = new TiledMapTileLayer(2, 2, 128, 128);
        assertThrows(IllegalArgumentException.class, () -> TileLayerResizer.resize(source, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> TileLayerResizer.resize(source, 1, -1));
        assertThrows(IllegalArgumentException.class, () -> TileLayerResizer.resize(
                new TiledMapTileLayer(0, 0, 128, 128), 1, 1));
    }
}
