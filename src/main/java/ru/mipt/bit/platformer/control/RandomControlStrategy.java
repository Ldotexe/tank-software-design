package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class RandomControlStrategy implements TankControlStrategy {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final Random random;

    public RandomControlStrategy() {
        this(new Random());
    }

    public RandomControlStrategy(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    @Override
    public List<Command> getCommands(Tank tank) {
        Objects.requireNonNull(tank);
        Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        return Collections.singletonList(new MoveCommand(tank, direction));
    }
}
