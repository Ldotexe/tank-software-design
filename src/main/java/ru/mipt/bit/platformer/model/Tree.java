package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public final class Tree extends GameObject implements Obstacle {
    public Tree(int tileX, int tileY) {
        super(tileX, tileY);
    }

    @Override
    public boolean occupiesTile(GridPoint2 tileCoordinates) {
        return getTilePosition().equals(tileCoordinates);
    }
}
