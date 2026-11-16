package ru.mipt.bit.platformer.model;

/**
 * Выходной порт уведомлений о появлении и удалении объектов логического уровня.
 */
public interface LevelListener {
    void objectAdded(GameObject object);

    void objectRemoved(GameObject object);
}
