package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.game.CommandSource;
import ru.mipt.bit.platformer.game.HealthBarVisibility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

import static com.badlogic.gdx.Input.Keys.L;

public final class GameControls implements CommandSource {
    private final KeyboardHandler keyboard;
    private final List<Command> commands = new ArrayList<>();

    public GameControls(IntPredicate isKeyJustPressed, HealthBarVisibility healthBars) {
        keyboard = new KeyboardHandler(key -> false, isKeyJustPressed);
        Command toggleHealthBars = new ToggleHealthBarsCommand(healthBars);
        keyboard.bindJustPressed(() -> commands.add(toggleHealthBars), L);
    }

    @Override
    public List<Command> getCommands() {
        commands.clear();
        keyboard.handleInput();
        return new ArrayList<>(commands);
    }
}
