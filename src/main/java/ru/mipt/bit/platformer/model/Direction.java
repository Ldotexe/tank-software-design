package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public enum Direction {
    UP(0, 1, 90f),
    LEFT(-1, 0, -180f),
    DOWN(0, -1, -90f),
    RIGHT(1, 0, 0f);

    private final GridPoint2 vector;
    private final float rotationDegrees;

    Direction(int offsetX, int offsetY, float rotationDegrees) {
        vector = new GridPoint2(offsetX, offsetY);
        this.rotationDegrees = rotationDegrees;
    }

    public GridPoint2 getVector() {
        return new GridPoint2(vector);
    }

    public GridPoint2 calculateAdjacentTile(GridPoint2 tilePosition) {
        return new GridPoint2(tilePosition).add(vector);
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }
}
