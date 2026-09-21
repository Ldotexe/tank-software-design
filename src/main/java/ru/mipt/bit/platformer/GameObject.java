package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

/** A model's position in tiles, independent of textures and screen coordinates. */
public abstract class GameObject {
    private final GridPoint2 tilePosition;

    protected GameObject(int tileX, int tileY) {
        tilePosition = new GridPoint2(tileX, tileY);
    }

    public GridPoint2 getTilePosition() {
        return new GridPoint2(tilePosition);
    }

    protected void moveToTile(GridPoint2 destination) {
        tilePosition.set(destination);
    }
}
