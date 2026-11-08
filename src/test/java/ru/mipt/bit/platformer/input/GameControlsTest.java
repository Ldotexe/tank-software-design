package ru.mipt.bit.platformer.input;

import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.game.HealthBarVisibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class GameControlsTest {
    @Test
    public void idleAndUnboundKeysProduceNoCommands() {
        HealthBarVisibility healthBars = new HealthBarVisibility();
        Set<Integer> justPressed = new HashSet<>();
        GameControls controls = new GameControls(justPressed::contains, healthBars);

        assertTrue(controls.getCommands().isEmpty());
        justPressed.addAll(Arrays.asList(SPACE, W, UP));
        assertTrue(controls.getCommands().isEmpty());
        assertTrue(healthBars.isVisible());
    }

    @Test
    public void lQueuesToggleWithoutChangingVisibilityDuringCollection() {
        HealthBarVisibility healthBars = new HealthBarVisibility();
        GameControls controls = new GameControls(key -> key == L, healthBars);

        List<Command> commands = controls.getCommands();

        assertEquals(1, commands.size());
        assertTrue(commands.get(0) instanceof ToggleHealthBarsCommand);
        assertTrue(healthBars.isVisible());
        commands.get(0).execute();
        assertFalse(healthBars.isVisible());
    }

    @Test
    public void onlyNewPressesToggleVisibility() {
        HealthBarVisibility healthBars = new HealthBarVisibility();
        Set<Integer> justPressed = new HashSet<>(Arrays.asList(L));
        GameControls controls = new GameControls(justPressed::contains, healthBars);

        controls.getCommands().forEach(Command::execute);
        assertFalse(healthBars.isVisible());
        justPressed.clear();
        assertTrue(controls.getCommands().isEmpty());
        assertTrue(controls.getCommands().isEmpty());
        assertFalse(healthBars.isVisible());
        justPressed.add(L);
        controls.getCommands().forEach(Command::execute);
        assertTrue(healthBars.isVisible());
    }

    @Test
    public void nextCollectionAndCallerChangesDoNotMutateQueuedCommands() {
        HealthBarVisibility healthBars = new HealthBarVisibility();
        Set<Integer> justPressed = new HashSet<>(Arrays.asList(L));
        GameControls controls = new GameControls(justPressed::contains, healthBars);
        List<Command> firstFrame = controls.getCommands();
        justPressed.clear();
        List<Command> secondFrame = controls.getCommands();

        assertEquals(1, firstFrame.size());
        assertTrue(secondFrame.isEmpty());
        secondFrame.add(() -> fail("Caller command leaked into next frame"));
        assertTrue(controls.getCommands().isEmpty());
        firstFrame.forEach(Command::execute);
        assertFalse(healthBars.isVisible());
    }

    @Test
    public void rejectsMissingInputAndVisibilityState() {
        assertThrows(NullPointerException.class,
                () -> new GameControls(null, new HealthBarVisibility()));
        assertThrows(NullPointerException.class,
                () -> new GameControls(key -> false, null));
    }
}
