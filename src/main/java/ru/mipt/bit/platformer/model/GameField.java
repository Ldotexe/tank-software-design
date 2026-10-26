package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameField implements MovementRules {
    private final int width;
    private final int height;
    private final List<Obstacle> obstacles = new ArrayList<>();

    public GameField(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Field dimensions must be positive");
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(Objects.requireNonNull(obstacle));
    }

    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    @Override
    public boolean isTileUnoccupied(GridPoint2 destinationTile) {
        if (destinationTile.x < 0 || destinationTile.x >= width
                || destinationTile.y < 0 || destinationTile.y >= height) {
            return false;
        }
        for (Obstacle obstacle : obstacles) {
            if (obstacle.occupiesTile(destinationTile)) {
                return false;
            }
        }
        return true;
    }
}
