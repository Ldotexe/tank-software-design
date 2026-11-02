package ru.mipt.bit.platformer.graphics.util;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public final class TileLayerResizer {
    private TileLayerResizer() {
    }

    public static TiledMapTileLayer resize(TiledMapTileLayer source, int width, int height) {
        if (width <= 0 || height <= 0 || source.getWidth() <= 0 || source.getHeight() <= 0) {
            throw new IllegalArgumentException("Tile layer dimensions must be positive");
        }
        TiledMapTileLayer resized = new TiledMapTileLayer(width, height,
                source.getTileWidth(), source.getTileHeight());
        resized.setName(source.getName());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                resized.setCell(x, y, source.getCell(x % source.getWidth(), y % source.getHeight()));
            }
        }
        return resized;
    }
}
