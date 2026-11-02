package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.List;

public interface EntityWorld {
    void addObject(GameObject object);

    void removeObject(GameObject object);

    boolean contains(GameObject object);

    List<GameObject> getObjects();

    boolean isInside(GridPoint2 tile);
}
