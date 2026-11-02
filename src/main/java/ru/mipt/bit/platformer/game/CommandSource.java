package ru.mipt.bit.platformer.game;

import ru.mipt.bit.platformer.command.Command;

import java.util.List;

@FunctionalInterface
public interface CommandSource {
    List<Command> getCommands();
}
