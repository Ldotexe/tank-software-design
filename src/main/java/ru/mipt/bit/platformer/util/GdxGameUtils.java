package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.NoSuchElementException;

public final class GdxGameUtils {

    private static final float TILE_CENTER_OFFSET = 0.5f;
    private static final float ORIGINAL_SCALE = 1f;

    private GdxGameUtils() {
    }

    public static OrthogonalTiledMapRenderer createFullMapRenderer(TiledMap tiledMap, Batch batch) {
        TiledMapTileLayer tileLayer = getOnlyTileLayer(tiledMap);
        float mapWidthPixels = tileLayer.getWidth() * tileLayer.getTileWidth();
        float mapHeightPixels = tileLayer.getHeight() * tileLayer.getTileHeight();

        OrthogonalTiledMapRenderer mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        mapRenderer.getViewBounds().set(0f, 0f, mapWidthPixels, mapHeightPixels);

        return mapRenderer;
    }

    public static TiledMapTileLayer getOnlyTileLayer(Map map) {
        MapLayers layers = map.getLayers();
        switch (layers.size()) {
            case 0:
                throw new NoSuchElementException("Map has no layers");
            case 1:
                if (!(layers.get(0) instanceof TiledMapTileLayer)) {
                    throw new IllegalArgumentException("Map layer must be a tile layer");
                }
                return (TiledMapTileLayer) layers.get(0);
            default:
                throw new IllegalArgumentException("Map has more than one layer");
        }
    }

    public static Rectangle centerSpriteOnTile(TiledMapTileLayer tileLayer, Rectangle spriteBounds, GridPoint2 tileCoordinates) {
        Vector2 tileCenter = calculateTileCenterPixels(tileLayer, tileCoordinates);
        return spriteBounds.setCenter(tileCenter);
    }

    public static void drawSpriteAtOriginalSize(Batch batch, TextureRegion spriteRegion, Rectangle spriteBounds, float rotationDegrees) {
        int spriteWidthPixels = spriteRegion.getRegionWidth();
        int spriteHeightPixels = spriteRegion.getRegionHeight();
        float rotationOriginX = spriteWidthPixels / 2f;
        float rotationOriginY = spriteHeightPixels / 2f;
        batch.draw(spriteRegion, spriteBounds.x, spriteBounds.y, rotationOriginX, rotationOriginY, spriteWidthPixels, spriteHeightPixels, ORIGINAL_SCALE, ORIGINAL_SCALE, rotationDegrees);
    }

    public static Rectangle createSpriteBounds(TextureRegion spriteRegion) {
        return new Rectangle()
                .setWidth(spriteRegion.getRegionWidth())
                .setHeight(spriteRegion.getRegionHeight());
    }

    private static Vector2 calculateTileCenterPixels(TiledMapTileLayer tileLayer, GridPoint2 tileCoordinates) {
        float tileCenterX = (tileCoordinates.x + TILE_CENTER_OFFSET) * tileLayer.getTileWidth();
        float tileCenterY = (tileCoordinates.y + TILE_CENTER_OFFSET) * tileLayer.getTileHeight();
        return new Vector2(tileCenterX, tileCenterY);
    }
}
