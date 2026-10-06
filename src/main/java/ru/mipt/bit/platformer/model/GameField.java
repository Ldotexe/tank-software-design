package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameField implements MovementRules {
    private final List<Obstacle> obstacles = new ArrayList<>();

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(Objects.requireNonNull(obstacle));
    }

    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    @Override
    public boolean isTileUnoccupied(GridPoint2 destinationTile) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.occupiesTile(destinationTile)) {
                return false;
            }
        }
        return true;
    }
}
