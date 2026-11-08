package ru.mipt.bit.platformer.command;

import ru.mipt.bit.platformer.game.HealthBarVisibility;

import java.util.Objects;

public final class ToggleHealthBarsCommand implements Command {
    private final HealthBarVisibility healthBars;

    public ToggleHealthBarsCommand(HealthBarVisibility healthBars) {
        this.healthBars = Objects.requireNonNull(healthBars);
    }

    @Override
    public void execute() {
        healthBars.toggle();
    }
}
