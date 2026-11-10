package ru.mipt.bit.platformer.control;

import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class RandomControlStrategyTest {
    @Test
    public void choosesDirectionUsingSuppliedRandomAndDefersExecution() {
        Random expected = new Random(42);
        RandomControlStrategy strategy = new RandomControlStrategy(new Random(42));
        Direction[] directions = Direction.values();
        for (int index = 0; index < 10; index++) {
            Tank tank = new Tank(new GameField(3, 3), 1, 1, 0.4f);
            List<Command> commands = strategy.getCommands(tank);
            assertEquals(1, commands.size());
            assertFalse(tank.isMoving());
            commands.get(0).execute();
            assertEquals(directions[expected.nextInt(directions.length)], tank.getDirection());
            assertTrue(tank.isMoving());
        }
    }

    @Test
    public void blockedRandomCommandDoesNotLeaveField() {
        Random random = new Random() {
            @Override
            public int nextInt(int bound) {
                return Direction.LEFT.ordinal();
            }
        };
        Tank tank = new Tank(new GameField(3, 3), 0, 0, 0.4f);
        new RandomControlStrategy(random).getCommands(tank).get(0).execute();
        assertEquals(Direction.LEFT, tank.getDirection());
        assertFalse(tank.isMoving());
    }

    @Test
    public void rejectsMissingRandomAndTank() {
        assertThrows(NullPointerException.class, () -> new RandomControlStrategy(null));
        assertThrows(NullPointerException.class,
                () -> new RandomControlStrategy(new Random(42)).getCommands(null));
    }
}
