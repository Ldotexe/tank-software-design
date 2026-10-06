package ru.mipt.bit.platformer.game;

import java.util.Objects;

public final class GameLoop {
    private final InputHandler input;
    private final Updatable model;
    private final GameView view;

    public GameLoop(InputHandler input, Updatable model, GameView view) {
        this.input = Objects.requireNonNull(input);
        this.model = Objects.requireNonNull(model);
        this.view = Objects.requireNonNull(view);
    }

    public void update(float elapsedSeconds) {
        input.handleInput();
        model.update(elapsedSeconds);
        view.render();
    }

    public void dispose() {
        view.dispose();
    }
}
