package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;

import static ru.mipt.bit.platformer.util.GdxGameUtils.centerSpriteOnTile;

public class TileMovement {

    private final TiledMapTileLayer tileLayer;
    private final Interpolation movementInterpolation;

    public TileMovement(TiledMapTileLayer tileLayer, Interpolation movementInterpolation) {
        this.tileLayer = tileLayer;
        this.movementInterpolation = movementInterpolation;
    }

    public Rectangle interpolateSpritePosition(Rectangle spriteBounds, GridPoint2 startTile, GridPoint2 destinationTile, float movementProgress) {
        centerSpriteOnTile(tileLayer, spriteBounds, startTile);
        float startX = spriteBounds.x;
        float startY = spriteBounds.y;

        centerSpriteOnTile(tileLayer, spriteBounds, destinationTile);
        float destinationX = spriteBounds.x;
        float destinationY = spriteBounds.y;

        float interpolatedX = movementInterpolation.apply(startX, destinationX, movementProgress);
        float interpolatedY = movementInterpolation.apply(startY, destinationY, movementProgress);

        return spriteBounds
                .setX(interpolatedX)
                .setY(interpolatedY);
    }
}
