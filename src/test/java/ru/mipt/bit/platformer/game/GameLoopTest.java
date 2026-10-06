package ru.mipt.bit.platformer.game;

import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GameLoopTest {
    @Test
    public void processesInputThenUpdatesModelThenRendersEachFrame() {
        List<String> calls = new ArrayList<>();
        GameLoop loop = new GameLoop(
                () -> calls.add("input"),
                time -> calls.add("update:" + time),
                view(() -> calls.add("render"), () -> calls.add("dispose")));
        loop.update(0.1f);
        loop.update(0.2f);
        assertEquals(Arrays.asList("input", "update:0.1", "render",
                "input", "update:0.2", "render"), calls);
    }

    @Test
    public void canRunWithAutomaticInputAndNoGraphics() {
        Tank player = new Tank(tile -> true, 1, 1, 0.4f);
        List<Integer> renderedPositions = new ArrayList<>();
        GameLoop loop = new GameLoop(
                () -> player.tryStartMoving(Direction.RIGHT), player,
                view(() -> renderedPositions.add(player.getTilePosition().x), () -> { }));
        loop.update(0.4f);
        loop.update(0.4f);
        assertEquals(Arrays.asList(2, 3), renderedPositions);
    }

    @Test
    public void disposesViewWithoutUpdatingGame() {
        List<String> calls = new ArrayList<>();
        GameLoop loop = new GameLoop(
                () -> calls.add("input"), time -> calls.add("update"),
                view(() -> calls.add("render"), () -> calls.add("dispose")));
        loop.dispose();
        assertEquals(Arrays.asList("dispose"), calls);
    }

    @Test
    public void rejectsMissingDependencies() {
        GameView view = view(() -> { }, () -> { });
        assertThrows(NullPointerException.class, () -> new GameLoop(null, time -> { }, view));
        assertThrows(NullPointerException.class, () -> new GameLoop(() -> { }, null, view));
        assertThrows(NullPointerException.class, () -> new GameLoop(() -> { }, time -> { }, null));
    }

    private GameView view(Runnable render, Runnable dispose) {
        return new GameView() {
            @Override
            public void render() {
                render.run();
            }

            @Override
            public void dispose() {
                dispose.run();
            }
        };
    }
}
