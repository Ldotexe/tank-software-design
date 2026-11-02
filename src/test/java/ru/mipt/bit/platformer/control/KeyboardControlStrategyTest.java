package ru.mipt.bit.platformer.control;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class KeyboardControlStrategyTest {
    @Test
    public void mapsAllArrowsAndWasdKeysToMovementCommands() {
        assertDirection(UP, Direction.UP);
        assertDirection(W, Direction.UP);
        assertDirection(LEFT, Direction.LEFT);
        assertDirection(A, Direction.LEFT);
        assertDirection(DOWN, Direction.DOWN);
        assertDirection(S, Direction.DOWN);
        assertDirection(RIGHT, Direction.RIGHT);
        assertDirection(D, Direction.RIGHT);
    }

    @Test
    public void createsNoCommandsForIdleOrUnboundKeys() {
        Tank tank = tank();
        assertTrue(new KeyboardControlStrategy(key -> false).getCommands(tank).isEmpty());
        assertTrue(new KeyboardControlStrategy(key -> key == SPACE).getCommands(tank).isEmpty());
        assertFalse(tank.isMoving());
    }

    @Test
    public void bothAlternativeKeysProduceOnlyOneCommand() {
        Tank tank = tank();
        Set<Integer> pressed = new HashSet<>(Arrays.asList(UP, W));
        List<Command> commands = new KeyboardControlStrategy(pressed::contains).getCommands(tank);
        assertEquals(1, commands.size());
        commands.get(0).execute();
        assertEquals(Direction.UP, tank.getDirection());
    }

    @Test
    public void preservesOriginalDirectionPriority() {
        Tank tank = tank();
        List<Command> commands = new KeyboardControlStrategy(key -> true).getCommands(tank);
        assertEquals(4, commands.size());
        assertFalse(tank.isMoving());
        commands.forEach(Command::execute);
        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationTile());
    }

    @Test
    public void triesNextHeldDirectionWhenFirstIsBlocked() {
        GameField field = new GameField(4, 3);
        field.addObstacle(new Tree(1, 2));
        Tank tank = new Tank(field, 1, 1, 0.4f);
        Set<Integer> pressed = new HashSet<>(Arrays.asList(UP, LEFT));
        new KeyboardControlStrategy(pressed::contains).getCommands(tank).forEach(Command::execute);
        assertEquals(Direction.LEFT, tank.getDirection());
        assertEquals(new GridPoint2(0, 1), tank.getDestinationTile());
    }

    @Test
    public void releasedKeysDoNotLeaveOldCommandsAndSnapshotsKeepTheirTank() {
        Set<Integer> pressed = new HashSet<>(Arrays.asList(UP));
        KeyboardControlStrategy strategy = new KeyboardControlStrategy(pressed::contains);
        Tank first = tank();
        Tank second = tank();
        List<Command> firstCommands = strategy.getCommands(first);
        pressed.clear();
        assertTrue(strategy.getCommands(second).isEmpty());
        firstCommands.forEach(Command::execute);
        assertTrue(first.isMoving());
        assertFalse(second.isMoving());
    }

    @Test
    public void rejectsMissingInputAndTank() {
        assertThrows(NullPointerException.class, () -> new KeyboardControlStrategy(null));
        assertThrows(NullPointerException.class,
                () -> new KeyboardControlStrategy(key -> false).getCommands(null));
    }

    @Test
    public void spaceProducesShotOnlyOnNewPress() {
        Set<Integer> justPressed = new HashSet<>(Arrays.asList(SPACE));
        KeyboardControlStrategy strategy = new KeyboardControlStrategy(key -> key == SPACE, justPressed::contains);
        Tank tank = tank();
        List<Command> firstPress = strategy.getCommands(tank);
        assertEquals(1, firstPress.size());
        assertTrue(firstPress.get(0) instanceof ShootCommand);
        justPressed.clear();
        assertTrue(strategy.getCommands(tank).isEmpty());
        justPressed.add(SPACE);
        assertEquals(1, strategy.getCommands(tank).size());
    }

    @Test
    public void moveAndShotAreCollectedBeforeEitherIsExecuted() {
        GameField field = new GameField(4, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f);
        field.addObstacle(tank);
        KeyboardControlStrategy strategy = new KeyboardControlStrategy(key -> key == UP, key -> key == SPACE);
        List<Command> commands = strategy.getCommands(tank);
        assertEquals(2, commands.size());
        assertTrue(commands.get(0) instanceof MoveCommand);
        assertTrue(commands.get(1) instanceof ShootCommand);
        assertEquals(Direction.RIGHT, tank.getDirection());
        assertFalse(tank.isMoving());
        assertEquals(1, field.getObjects().size());
        commands.forEach(Command::execute);
        Bullet bullet = (Bullet) field.getObjects().stream()
                .filter(object -> object instanceof Bullet).findFirst().get();
        assertEquals(Direction.UP, bullet.getDirection());
    }

    @Test
    public void movingTankCanShootButReceivesNoNewMovementCommand() {
        Tank tank = tank();
        tank.tryStartMoving(Direction.UP);
        tank.update(0.2f);
        KeyboardControlStrategy strategy = new KeyboardControlStrategy(key -> key == LEFT, key -> key == SPACE);
        List<Command> commands = strategy.getCommands(tank);
        assertEquals(1, commands.size());
        assertTrue(commands.get(0) instanceof ShootCommand);
        commands.get(0).execute();
        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(0.5f, tank.getMovementProgress(), 0.00001f);
    }

    @Test
    public void rejectsMissingJustPressedInput() {
        assertThrows(NullPointerException.class, () -> new KeyboardControlStrategy(key -> false, null));
    }

    private void assertDirection(int key, Direction direction) {
        Tank tank = tank();
        List<Command> commands = new KeyboardControlStrategy(candidate -> candidate == key).getCommands(tank);
        assertEquals(1, commands.size());
        assertFalse(tank.isMoving());
        commands.get(0).execute();
        assertEquals(direction, tank.getDirection());
        assertEquals(direction.calculateAdjacentTile(new GridPoint2(1, 1)), tank.getDestinationTile());
    }

    private Tank tank() {
        GameField field = new GameField(4, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f);
        field.addObstacle(tank);
        return tank;
    }
}
