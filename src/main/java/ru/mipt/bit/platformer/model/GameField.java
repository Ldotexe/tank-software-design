package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameField implements MovementRules, EntityWorld {
    private final int width;
    private final int height;
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<GameObject> objects = new ArrayList<>();
    private final List<LevelListener> listeners = new ArrayList<>();

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
        Objects.requireNonNull(obstacle);
        if (obstacle instanceof GameObject) {
            addObject((GameObject) obstacle);
        } else if (!obstacles.contains(obstacle)) {
            obstacles.add(obstacle);
        }
    }

    public List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    @Override
    public void addObject(GameObject object) {
        Objects.requireNonNull(object);
        if (objects.contains(object)) {
            return;
        }
        object.attachTo(this);
        objects.add(object);
        if (object instanceof Obstacle) {
            obstacles.add((Obstacle) object);
        }
        for (LevelListener listener : new ArrayList<>(listeners)) {
            if (!contains(object)) {
                break;
            }
            if (listeners.contains(listener)) {
                listener.objectAdded(object);
            }
        }
    }

    @Override
    public void removeObject(GameObject object) {
        if (!objects.remove(object)) {
            return;
        }
        if (object instanceof Obstacle) {
            obstacles.remove(object);
        }
        for (LevelListener listener : new ArrayList<>(listeners)) {
            if (contains(object)) {
                break;
            }
            if (listeners.contains(listener)) {
                listener.objectRemoved(object);
            }
        }
    }

    @Override
    public boolean contains(GameObject object) {
        return objects.contains(object);
    }

    @Override
    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public void addListener(LevelListener listener) {
        Objects.requireNonNull(listener);
        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
        for (GameObject object : new ArrayList<>(objects)) {
            if (contains(object)) {
                listener.objectAdded(object);
            }
        }
    }

    public void removeListener(LevelListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean isInside(GridPoint2 tile) {
        return tile.x >= 0 && tile.x < width && tile.y >= 0 && tile.y < height;
    }

    @Override
    public boolean isTileUnoccupied(GridPoint2 destinationTile) {
        if (!isInside(destinationTile)) {
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
