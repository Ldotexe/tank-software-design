package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.Direction;
import ru.mipt.bit.platformer.Tank;

import static com.badlogic.gdx.Input.Keys.*;

public final class PlayerControls {
    private PlayerControls() {
    }

    public static void bindMovement(KeyboardHandler keyboard, Tank tank) {
        keyboard.bindHeld(() -> tank.tryStartMoving(Direction.UP), UP, W);
        keyboard.bindHeld(() -> tank.tryStartMoving(Direction.LEFT), LEFT, A);
        keyboard.bindHeld(() -> tank.tryStartMoving(Direction.DOWN), DOWN, S);
        keyboard.bindHeld(() -> tank.tryStartMoving(Direction.RIGHT), RIGHT, D);
    }
}
