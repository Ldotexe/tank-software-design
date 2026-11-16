package ru.mipt.bit.platformer.control;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.Tank;

import java.util.List;

@FunctionalInterface
public interface TankControlStrategy {
    List<Command> getCommands(Tank tank);
}
