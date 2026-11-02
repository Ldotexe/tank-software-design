package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.input.KeyboardHandler;
import ru.mipt.bit.platformer.input.PlayerControls;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;

import static com.badlogic.gdx.Input.Keys.SPACE;

public final class KeyboardControlStrategy implements TankControlStrategy {
    private final KeyboardHandler keyboard;
    private final List<Direction> pressedDirections = new ArrayList<>();
    private boolean shootPressed;

    public KeyboardControlStrategy(IntPredicate isKeyPressed) {
        this(isKeyPressed, key -> false);
    }

    public KeyboardControlStrategy(IntPredicate isKeyPressed, IntPredicate isKeyJustPressed) {
        keyboard = new KeyboardHandler(isKeyPressed, isKeyJustPressed);
        PlayerControls.bindMovement(keyboard, pressedDirections::add);
        keyboard.bindJustPressed(() -> shootPressed = true, SPACE);
    }

    @Override
    public List<Command> getCommands(Tank tank) {
        Objects.requireNonNull(tank);
        pressedDirections.clear();
        shootPressed = false;
        keyboard.handleInput();
        List<Command> commands = new ArrayList<>();
        if (!tank.isMoving()) {
            for (Direction direction : pressedDirections) {
                commands.add(new MoveCommand(tank, direction));
            }
        }
        if (shootPressed) {
            commands.add(new ShootCommand(tank));
        }
        return commands;
    }
}
