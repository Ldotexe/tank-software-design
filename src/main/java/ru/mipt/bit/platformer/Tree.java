package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

public final class Tree extends GameObject {
    public Tree(int tileX, int tileY) {
        super(tileX, tileY);
    }

    public boolean occupiesTile(GridPoint2 tileCoordinates) {
        return getTilePosition().equals(tileCoordinates);
    }
}
