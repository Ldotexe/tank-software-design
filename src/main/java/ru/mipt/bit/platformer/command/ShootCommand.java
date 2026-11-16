package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.model.Tank;

import java.util.Objects;

public final class ShootCommand implements Command {
    private final Tank tank;

    public ShootCommand(Tank tank) {
        this.tank = Objects.requireNonNull(tank);
    }

    @Override
    public void execute() {
        tank.shoot();
    }
}
