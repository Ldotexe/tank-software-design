package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.graphics.util.TileMovement;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Objects;

final class HealthBarLayout {
    private static final float WIDTH_IN_TILES = 0.7f;
    private static final float HEIGHT_IN_TILES = 0.06f;
    private static final float VERTICAL_OFFSET_IN_TILES = 0.4f;

    private final TileMovement movement;
    private final Rectangle bounds;
    private final float verticalOffset;

    HealthBarLayout(TiledMapTileLayer tileLayer, Interpolation interpolation) {
        Objects.requireNonNull(tileLayer);
        movement = new TileMovement(tileLayer, Objects.requireNonNull(interpolation));
        bounds = new Rectangle(0f, 0f, tileLayer.getTileWidth() * WIDTH_IN_TILES,
                tileLayer.getTileHeight() * HEIGHT_IN_TILES);
        verticalOffset = tileLayer.getTileHeight() * VERTICAL_OFFSET_IN_TILES;
    }

    Rectangle boundsFor(Tank tank) {
        movement.interpolateSpritePosition(bounds, tank.getTilePosition(), tank.getDestinationTile(),
                tank.getMovementProgress());
        bounds.y += verticalOffset;
        return bounds;
    }

    float healthFraction(Tank tank) {
        if (tank.getMaxHealth() == 0) {
            return 0f;
        }
        return Math.max(0f, Math.min(1f, (float) tank.getHealth() / tank.getMaxHealth()));
    }
}
