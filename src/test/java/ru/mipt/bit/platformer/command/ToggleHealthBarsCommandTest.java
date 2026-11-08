package ru.mipt.bit.platformer.command;

import org.junit.Test;
import ru.mipt.bit.platformer.game.HealthBarVisibility;

import static org.junit.Assert.*;

public class ToggleHealthBarsCommandTest {
    @Test
    public void changesVisibilityOnlyWhenExecutedAndCanBeReused() {
        HealthBarVisibility healthBars = new HealthBarVisibility();
        Command command = new ToggleHealthBarsCommand(healthBars);

        assertTrue(healthBars.isVisible());
        command.execute();
        assertFalse(healthBars.isVisible());
        command.execute();
        assertTrue(healthBars.isVisible());
    }

    @Test
    public void rejectsMissingVisibilityState() {
        assertThrows(NullPointerException.class, () -> new ToggleHealthBarsCommand(null));
    }
}
