package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.game.InputHandler;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Objects;

public final class TankController implements InputHandler {
    private final Tank tank;
    private final TankControlStrategy strategy;

    public TankController(Tank tank, TankControlStrategy strategy) {
        this.tank = Objects.requireNonNull(tank);
        this.strategy = Objects.requireNonNull(strategy);
    }

    @Override
    public void handleInput() {
        if (tank.isMoving()) {
            return;
        }
        for (Command command : strategy.getCommands(tank)) {
            command.execute();
        }
    }
}
