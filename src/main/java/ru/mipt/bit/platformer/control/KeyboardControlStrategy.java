package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.input.KeyboardHandler;
import ru.mipt.bit.platformer.input.PlayerControls;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class KeyboardControlStrategy implements TankControlStrategy {
    private final KeyboardHandler keyboard;
    private final List<Direction> pressedDirections = new ArrayList<>();

    public KeyboardControlStrategy(IntPredicate isKeyPressed) {
        keyboard = new KeyboardHandler(isKeyPressed, key -> false);
        PlayerControls.bindMovement(keyboard, pressedDirections::add);
    }

    @Override
    public List<Command> getCommands(Tank tank) {
        Objects.requireNonNull(tank);
        pressedDirections.clear();
        keyboard.handleInput();
        List<Command> commands = new ArrayList<>();
        for (Direction direction : pressedDirections) {
            commands.add(new MoveCommand(tank, direction));
        }
        return commands;
    }
}
