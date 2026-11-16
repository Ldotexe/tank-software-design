package ru.mipt.bit.platformer.game;

import ru.mipt.bit.platformer.command.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GameLoop {
    private final CommandSource input;
    private final Updatable model;
    private final GameView view;

    public GameLoop(CommandSource input, Updatable model, GameView view) {
        this.input = Objects.requireNonNull(input);
        this.model = Objects.requireNonNull(model);
        this.view = Objects.requireNonNull(view);
    }

    public void update(float elapsedSeconds) {
        List<Command> commands = new ArrayList<>(input.getCommands());
        for (Command command : commands) {
            command.execute();
        }
        model.update(elapsedSeconds);
        view.render();
    }

    public void resize(int width, int height) {
        view.resize(width, height);
    }

    public void dispose() {
        view.dispose();
    }
}
