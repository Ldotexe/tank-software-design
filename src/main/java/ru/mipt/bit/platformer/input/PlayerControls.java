package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.Direction;

import java.util.function.Consumer;

import static com.badlogic.gdx.Input.Keys.*;

public final class PlayerControls {
    private PlayerControls() {
    }

    public static void bindMovement(KeyboardHandler keyboard, Consumer<Direction> move) {
        keyboard.bindHeld(() -> move.accept(Direction.UP), UP, W);
        keyboard.bindHeld(() -> move.accept(Direction.LEFT), LEFT, A);
        keyboard.bindHeld(() -> move.accept(Direction.DOWN), DOWN, S);
        keyboard.bindHeld(() -> move.accept(Direction.RIGHT), RIGHT, D);
    }
}
