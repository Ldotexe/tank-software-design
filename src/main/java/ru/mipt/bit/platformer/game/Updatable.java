package ru.mipt.bit.platformer.game;

@FunctionalInterface
public interface Updatable {
    void update(float elapsedSeconds);
}
