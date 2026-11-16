package ru.mipt.bit.platformer.game;

import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.control.TankController;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class GameLoopTest {
    @Test
    public void generatesThenExecutesThenUpdatesThenRendersEachFrame() {
        List<String> calls = new ArrayList<>();
        GameLoop loop = new GameLoop(() -> {
            calls.add("generate");
            return Arrays.asList(() -> calls.add("first"), () -> calls.add("second"));
        }, time -> calls.add("update:" + time),
                view(() -> calls.add("render"), () -> calls.add("dispose")));
        loop.update(0.1f);
        loop.update(0.2f);
        assertEquals(Arrays.asList("generate", "first", "second", "update:0.1", "render",
                "generate", "first", "second", "update:0.2", "render"), calls);
    }

    @Test
    public void collectsAllControllersBeforeExecutingAnyCommand() {
        List<String> calls = new ArrayList<>();
        AtomicInteger executions = new AtomicInteger();
        Tank first = new Tank(tile -> true, 0, 0, 0.4f);
        Tank second = new Tank(tile -> true, 1, 0, 0.4f);
        TankController firstController = new TankController(first, tank -> {
            calls.add("first selected");
            return Collections.singletonList(() -> {
                executions.incrementAndGet();
                calls.add("first executed");
            });
        });
        TankController secondController = new TankController(second, tank -> {
            assertEquals(0, executions.get());
            calls.add("second selected");
            return Collections.singletonList(() -> calls.add("second executed"));
        });
        GameLoop loop = new GameLoop(() -> {
            List<Command> commands = new ArrayList<>();
            commands.addAll(firstController.getCommands());
            commands.addAll(secondController.getCommands());
            return commands;
        }, time -> calls.add("update"), view(() -> calls.add("render"), () -> { }));
        loop.update(0.1f);
        assertEquals(Arrays.asList("first selected", "second selected", "first executed",
                "second executed", "update", "render"), calls);
    }

    @Test
    public void snapshotsCommandsBeforeExecution() {
        List<String> calls = new ArrayList<>();
        List<Command> commands = new ArrayList<>();
        commands.add(() -> {
            calls.add("first");
            commands.add(() -> calls.add("added"));
        });
        GameLoop loop = new GameLoop(() -> commands, time -> { }, view(() -> { }, () -> { }));
        loop.update(0.1f);
        assertEquals(Collections.singletonList("first"), calls);
        loop.update(0.1f);
        assertEquals(Arrays.asList("first", "first", "added"), calls);
    }

    @Test
    public void canRunWithAutomaticInputAndNoGraphics() {
        Tank player = new Tank(tile -> true, 1, 1, 0.4f);
        List<Integer> renderedPositions = new ArrayList<>();
        GameLoop loop = new GameLoop(
                () -> Collections.singletonList(new MoveCommand(player, Direction.RIGHT)), player,
                view(() -> renderedPositions.add(player.getTilePosition().x), () -> { }));
        loop.update(0.4f);
        loop.update(0.4f);
        assertEquals(Arrays.asList(2, 3), renderedPositions);
    }

    @Test
    public void disposesViewWithoutCollectingCommandsOrUpdatingGame() {
        List<String> calls = new ArrayList<>();
        GameLoop loop = new GameLoop(() -> {
            calls.add("generate");
            return Collections.emptyList();
        }, time -> calls.add("update"), view(() -> calls.add("render"), () -> calls.add("dispose")));
        loop.dispose();
        assertEquals(Collections.singletonList("dispose"), calls);
    }

    @Test
    public void rejectsMissingDependencies() {
        GameView view = view(() -> { }, () -> { });
        assertThrows(NullPointerException.class, () -> new GameLoop(null, time -> { }, view));
        assertThrows(NullPointerException.class, () -> new GameLoop(Collections::emptyList, null, view));
        assertThrows(NullPointerException.class, () -> new GameLoop(Collections::emptyList, time -> { }, null));
    }

    @Test
    public void forwardsWindowSizeToView() {
        List<String> calls = new ArrayList<>();
        GameView view = new GameView() {
            @Override
            public void render() {
            }

            @Override
            public void resize(int width, int height) {
                calls.add(width + "x" + height);
            }

            @Override
            public void dispose() {
            }
        };
        GameLoop loop = new GameLoop(Collections::emptyList, time -> calls.add("update"), view);
        loop.resize(640, 360);
        assertEquals(Collections.singletonList("640x360"), calls);
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
