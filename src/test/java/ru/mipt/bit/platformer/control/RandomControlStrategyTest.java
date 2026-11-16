package ru.mipt.bit.platformer.control;

import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class RandomControlStrategyTest {
    @Test
    public void choosesAmongAllDirectionsAndShootingUsingSuppliedRandom() {
        Random expected = new Random(42);
        RandomControlStrategy strategy = new RandomControlStrategy(new Random(42));
        Direction[] directions = Direction.values();
        for (int index = 0; index < 20; index++) {
            GameField field = new GameField(3, 3);
            Tank tank = new Tank(field, 1, 1, 0.4f);
            field.addObstacle(tank);
            List<Command> commands = strategy.getCommands(tank);
            int action = expected.nextInt(directions.length + 1);
            assertEquals(1, commands.size());
            assertFalse(tank.isMoving());
            assertEquals(1, field.getObjects().size());
            if (action == directions.length) {
                assertTrue(commands.get(0) instanceof ShootCommand);
                commands.get(0).execute();
                assertEquals(2, field.getObjects().size());
            } else {
                assertTrue(commands.get(0) instanceof MoveCommand);
                commands.get(0).execute();
                assertEquals(directions[action], tank.getDirection());
                assertTrue(tank.isMoving());
            }
        }
    }

    @Test
    public void blockedRandomCommandDoesNotLeaveField() {
        Tank tank = new Tank(new GameField(3, 3), 0, 0, 0.4f);
        new RandomControlStrategy(fixedAction(Direction.LEFT.ordinal())).getCommands(tank).get(0).execute();
        assertEquals(Direction.LEFT, tank.getDirection());
        assertFalse(tank.isMoving());
    }

    @Test
    public void movingTankCanReceiveShot() {
        GameField field = new GameField(4, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f);
        field.addObstacle(tank);
        tank.tryStartMoving(Direction.RIGHT);
        List<Command> commands = new RandomControlStrategy(fixedAction(Direction.values().length)).getCommands(tank);
        assertEquals(1, commands.size());
        assertTrue(commands.get(0) instanceof ShootCommand);
        commands.get(0).execute();
        assertEquals(2, field.getObjects().size());
    }

    @Test
    public void movingTankReceivesNoMovementCommand() {
        Tank tank = new Tank(new GameField(4, 3), 1, 1, 0.4f);
        tank.tryStartMoving(Direction.RIGHT);
        assertTrue(new RandomControlStrategy(fixedAction(Direction.UP.ordinal())).getCommands(tank).isEmpty());
    }

    @Test
    public void rejectsMissingRandomAndTank() {
        assertThrows(NullPointerException.class, () -> new RandomControlStrategy(null));
        assertThrows(NullPointerException.class,
                () -> new RandomControlStrategy(new Random(42)).getCommands(null));
    }

    private Random fixedAction(int action) {
        return new Random() {
            @Override
            public int nextInt(int bound) {
                assertEquals(Direction.values().length + 1, bound);
                return action;
            }
        };
    }
}
