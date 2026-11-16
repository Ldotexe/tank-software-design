package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.game.CommandSource;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TankController implements CommandSource {
    private final Tank tank;
    private final TankControlStrategy strategy;

    public TankController(Tank tank, TankControlStrategy strategy) {
        this.tank = Objects.requireNonNull(tank);
        this.strategy = Objects.requireNonNull(strategy);
    }

    @Override
    public List<Command> getCommands() {
        if (!tank.isAlive()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(strategy.getCommands(tank));
    }
}
