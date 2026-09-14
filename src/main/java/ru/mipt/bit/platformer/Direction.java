package ru.mipt.bit.platformer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;

import static com.badlogic.gdx.Input.Keys.*;

public enum Direction {
    UP(0, 1, 90f, Input.Keys.UP, W),
    LEFT(-1, 0, -180f, Input.Keys.LEFT, A),
    DOWN(0, -1, -90f, Input.Keys.DOWN, S),
    RIGHT(1, 0, 0f, Input.Keys.RIGHT, D);

    private final int tileOffsetX;
    private final int tileOffsetY;
    private final float rotationDegrees;
    private final int arrowKey;
    private final int letterKey;

    Direction(int tileOffsetX, int tileOffsetY, float rotationDegrees, int arrowKey, int letterKey) {
        this.tileOffsetX = tileOffsetX;
        this.tileOffsetY = tileOffsetY;
        this.rotationDegrees = rotationDegrees;
        this.arrowKey = arrowKey;
        this.letterKey = letterKey;
    }

    public GridPoint2 calculateAdjacentTile(GridPoint2 tilePosition) {
        return new GridPoint2(tilePosition).add(tileOffsetX, tileOffsetY);
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }

    public boolean isMovementKeyPressed(Input input) {
        return input.isKeyPressed(arrowKey) || input.isKeyPressed(letterKey);
    }
}
