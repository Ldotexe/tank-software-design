package ru.mipt.bit.platformer.game;

import ru.mipt.bit.platformer.command.Command;

import java.util.List;

/**
 * Порт получения команд для игрового цикла.
 */
@FunctionalInterface
public interface CommandSource {
    List<Command> getCommands();
}
