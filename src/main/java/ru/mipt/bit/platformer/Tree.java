package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

public class Tree extends GameObject {
    private static final String TEXTURE_PATH = "images/greenTree.png";

    public Tree(GameField gameField, int tileX, int tileY) {
        super(TEXTURE_PATH, tileX, tileY);
        gameField.centerSpriteOnTile(spriteBounds, tilePosition);
    }

    public boolean occupiesTile(GridPoint2 tileCoordinates) {
        return tilePosition.equals(tileCoordinates);
    }
}
