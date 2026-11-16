package ru.mipt.bit.platformer.game;

/**
 * Выходной порт отображения игры без зависимости от графической библиотеки.
 */
public interface GameView {
    void render();

    default void resize(int width, int height) {
    }

    void dispose();
}
