package ru.mipt.bit.platformer.control;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
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
    public void executesCommandsFromReplaceableStrategyInOrder() {
        List<String> calls = new ArrayList<>();
        Tank tank = tank();
        TankControlStrategy strategy = controlled -> {
            assertSame(tank, controlled);
            Command first = () -> calls.add("first");
            Command second = () -> calls.add("second");
            return Arrays.asList(first, second);
        };
        new TankController(tank, strategy).handleInput();
        assertEquals(Arrays.asList("first", "second"), calls);
    }

    @Test
    public void doesNotAskStrategyWhileTankIsMoving() {
        Tank tank = tank();
        tank.tryStartMoving(Direction.UP);
        tank.update(0.2f);
        AtomicInteger strategyCalls = new AtomicInteger();
        TankControlStrategy strategy = controlled -> {
            strategyCalls.incrementAndGet();
            return Collections.singletonList(new MoveCommand(controlled, Direction.LEFT));
        };
        new TankController(tank, strategy).handleInput();
        assertEquals(0, strategyCalls.get());
        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(0.5f, tank.getMovementProgress(), 0.00001f);
    }

    @Test
    public void choosesNextCommandAfterTankReachesDestination() {
        Tank tank = new Tank(new GameField(5, 3), 1, 1, 0.4f);
        AtomicInteger calls = new AtomicInteger();
        TankController controller = new TankController(tank, controlled -> {
            calls.incrementAndGet();
            return Collections.singletonList(new MoveCommand(controlled, Direction.RIGHT));
        });
        controller.handleInput();
        controller.handleInput();
        assertEquals(1, calls.get());
        tank.update(0.4f);
        controller.handleInput();
        tank.update(0.4f);
        assertEquals(2, calls.get());
        assertEquals(new GridPoint2(3, 1), tank.getTilePosition());
    }

    @Test
    public void randomStrategyDoesNotDrawNewDirectionInMotion() {
        AtomicInteger draws = new AtomicInteger();
        Random random = new Random() {
            @Override
            public int nextInt(int bound) {
                draws.incrementAndGet();
                return Direction.RIGHT.ordinal();
            }
        };
        Tank tank = new Tank(new GameField(5, 3), 1, 1, 0.4f);
        TankController controller = new TankController(tank, new RandomControlStrategy(random));
        controller.handleInput();
        tank.update(0.2f);
        controller.handleInput();
        assertEquals(1, draws.get());
        tank.update(0.2f);
        controller.handleInput();
        assertEquals(2, draws.get());
    }

    @Test
    public void acceptsStrategyWithNoCommand() {
        Tank tank = tank();
        new TankController(tank, controlled -> Collections.emptyList()).handleInput();
        assertFalse(tank.isMoving());
    }

    @Test
    public void rejectsMissingTankAndStrategy() {
        TankControlStrategy strategy = controlled -> Collections.emptyList();
        assertThrows(NullPointerException.class, () -> new TankController(null, strategy));
        assertThrows(NullPointerException.class, () -> new TankController(tank(), null));
    }

    private Tank tank() {
        return new Tank(new GameField(4, 3), 1, 1, 0.4f);
    }
}
