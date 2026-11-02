package ru.mipt.bit.platformer.game;

public interface GameView {
    void render();

    default void resize(int width, int height) {
    }

    void dispose();
}
