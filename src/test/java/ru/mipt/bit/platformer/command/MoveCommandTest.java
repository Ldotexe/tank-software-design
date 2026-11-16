package ru.mipt.bit.platformer.command;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import static org.junit.Assert.*;

public class MoveCommandTest {
    @Test
    public void startsMovementOnlyWhenExecuted() {
        Tank tank = new Tank(new GameField(4, 3), 1, 1, 0.4f);
        Command command = new MoveCommand(tank, Direction.UP);
        assertFalse(tank.isMoving());
        assertEquals(Direction.RIGHT, tank.getDirection());
        command.execute();
        assertTrue(tank.isMoving());
        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationTile());
    }

    @Test
    public void usesTankPositionAtExecutionTime() {
        Tank tank = new Tank(new GameField(4, 3), 1, 1, 0.4f);
        Command moveUp = new MoveCommand(tank, Direction.UP);
        new MoveCommand(tank, Direction.RIGHT).execute();
        tank.update(0.4f);
        moveUp.execute();
        tank.update(0.4f);
        assertEquals(new GridPoint2(2, 2), tank.getTilePosition());
    }

    @Test
    public void obeysObstaclesAndBounds() {
        GameField field = new GameField(3, 2);
        field.addObstacle(new Tree(1, 1));
        Tank tank = new Tank(field, 1, 0, 0.4f);
        new MoveCommand(tank, Direction.UP).execute();
        assertFalse(tank.isMoving());
        assertEquals(Direction.UP, tank.getDirection());
        new MoveCommand(tank, Direction.DOWN).execute();
        assertFalse(tank.isMoving());
        assertEquals(Direction.DOWN, tank.getDirection());
        assertEquals(new GridPoint2(1, 0), tank.getTilePosition());
    }

    @Test
    public void doesNotInterruptMovementInProgress() {
        Tank tank = new Tank(new GameField(4, 3), 1, 1, 0.4f);
        new MoveCommand(tank, Direction.UP).execute();
        tank.update(0.2f);
        new MoveCommand(tank, Direction.RIGHT).execute();
        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationTile());
        assertEquals(0.5f, tank.getMovementProgress(), 0.00001f);
    }

    @Test
    public void rejectsMissingTankAndDirection() {
        Tank tank = new Tank(new GameField(4, 3), 1, 1, 0.4f);
        assertThrows(NullPointerException.class, () -> new MoveCommand(null, Direction.UP));
        assertThrows(NullPointerException.class, () -> new MoveCommand(tank, null));
    }
}
