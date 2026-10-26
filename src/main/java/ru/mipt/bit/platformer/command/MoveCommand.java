package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Objects;

public final class MoveCommand implements Command {
    private final Tank tank;
    private final Direction direction;

    public MoveCommand(Tank tank, Direction direction) {
        this.tank = Objects.requireNonNull(tank);
        this.direction = Objects.requireNonNull(direction);
    }

    @Override
    public void execute() {
        tank.tryStartMoving(direction);
    }
}
