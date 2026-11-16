package ru.mipt.bit.platformer.model;

public interface LevelListener {
    void objectAdded(GameObject object);

    void objectRemoved(GameObject object);
}
