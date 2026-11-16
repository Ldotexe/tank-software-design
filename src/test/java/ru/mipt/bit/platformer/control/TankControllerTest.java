package ru.mipt.bit.platformer.control;

import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class TankControllerTest {
    @Test
    public void collectsCommandsWithoutExecutingThem() {
        List<String> calls = new ArrayList<>();
        Tank tank = tank();
        TankControlStrategy strategy = controlled -> {
            assertSame(tank, controlled);
            return Arrays.asList(() -> calls.add("first"), () -> calls.add("second"));
        };
        List<Command> commands = new TankController(tank, strategy).getCommands();
        assertTrue(calls.isEmpty());
        commands.forEach(Command::execute);
        assertEquals(Arrays.asList("first", "second"), calls);
    }

    @Test
    public void movingTankCanStillAskStrategyForShooting() {
        GameField field = new GameField(5, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f);
        field.addObstacle(tank);
        tank.tryStartMoving(Direction.UP);
        tank.update(0.2f);
        AtomicInteger strategyCalls = new AtomicInteger();
        TankController controller = new TankController(tank, controlled -> {
            strategyCalls.incrementAndGet();
            return Collections.singletonList(new ShootCommand(controlled));
        });
        List<Command> commands = controller.getCommands();
        assertEquals(1, strategyCalls.get());
        assertEquals(1, field.getObjects().size());
        commands.forEach(Command::execute);
        assertEquals(2, field.getObjects().size());
        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(0.5f, tank.getMovementProgress(), 0.00001f);
    }

    @Test
    public void deadTankDoesNotAskStrategyOrReturnCommands() {
        Tank tank = tank();
        tank.takeDamage(101);
        AtomicInteger strategyCalls = new AtomicInteger();
        TankController controller = new TankController(tank, controlled -> {
            strategyCalls.incrementAndGet();
            return Collections.singletonList(new MoveCommand(controlled, Direction.LEFT));
        });
        assertTrue(controller.getCommands().isEmpty());
        assertEquals(0, strategyCalls.get());
    }

    @Test
    public void randomMovementWaitsForPreviousTransitionToFinish() {
        Tank tank = new Tank(new GameField(5, 3), 1, 1, 0.4f);
        Random random = new Random() {
            @Override
            public int nextInt(int bound) {
                return Direction.RIGHT.ordinal();
            }
        };
        TankController controller = new TankController(tank, new RandomControlStrategy(random));
        controller.getCommands().forEach(Command::execute);
        tank.update(0.2f);
        assertTrue(controller.getCommands().isEmpty());
        tank.update(0.2f);
        controller.getCommands().forEach(Command::execute);
        tank.update(0.4f);
        assertEquals(3, tank.getTilePosition().x);
    }

    @Test
    public void commandSnapshotDoesNotChangeWhenStrategyReusesList() {
        List<Command> strategyCommands = new ArrayList<>();
        AtomicInteger executions = new AtomicInteger();
        strategyCommands.add(executions::incrementAndGet);
        TankController controller = new TankController(tank(), controlled -> strategyCommands);
        List<Command> commands = controller.getCommands();
        strategyCommands.clear();
        commands.forEach(Command::execute);
        assertEquals(1, executions.get());
    }

    @Test
    public void acceptsStrategyWithNoCommand() {
        Tank tank = tank();
        assertTrue(new TankController(tank, controlled -> Collections.emptyList()).getCommands().isEmpty());
        assertFalse(tank.isMoving());
    }

    @Test
    public void rejectsMissingTankAndStrategy() {
        TankControlStrategy strategy = controlled -> Collections.emptyList();
        assertThrows(NullPointerException.class, () -> new TankController(null, strategy));
        assertThrows(NullPointerException.class, () -> new TankController(tank(), null));
    }

    private Tank tank() {
        GameField field = new GameField(4, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f);
        field.addObstacle(tank);
        return tank;
    }
}
