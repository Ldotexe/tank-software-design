package ru.mipt.bit.platformer.command;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ShootCommandTest {
    @Test
    public void createsBulletOnlyWhenCommandIsExecuted() {
        GameField field = new GameField(5, 4);
        Tank tank = addTank(field, 1, 1);
        ShootCommand command = new ShootCommand(tank);
        assertEquals(1, field.getObjects().size());
        command.execute();
        Bullet bullet = (Bullet) field.getObjects().stream()
                .filter(object -> object instanceof Bullet).findFirst().get();
        assertEquals(new GridPoint2(2, 1), bullet.getTilePosition());
        assertEquals(Direction.RIGHT, bullet.getDirection());
        assertSame(tank, bullet.getOwner());
        assertEquals(25, bullet.getDamage());
    }

    @Test
    public void moveThenShootUsesNewDirectionEvenWhileMoving() {
        GameField field = new GameField(5, 4);
        Tank tank = addTank(field, 1, 1);
        Arrays.<Command>asList(new MoveCommand(tank, Direction.UP), new ShootCommand(tank))
                .forEach(Command::execute);
        assertTrue(tank.isMoving());
        Bullet bullet = (Bullet) field.getObjects().stream()
                .filter(object -> object instanceof Bullet).findFirst().get();
        assertEquals(new GridPoint2(1, 2), bullet.getTilePosition());
        assertEquals(Direction.UP, bullet.getDirection());
    }

    @Test
    public void commandsQueuedBeforeDeathCannotMoveOrShootAfterDeath() {
        GameField field = new GameField(5, 4);
        Tank tank = addTank(field, 1, 1);
        Command move = new MoveCommand(tank, Direction.RIGHT);
        Command shoot = new ShootCommand(tank);
        tank.takeDamage(101);
        move.execute();
        shoot.execute();
        assertFalse(tank.isAlive());
        assertFalse(tank.isMoving());
        assertTrue(field.getObjects().isEmpty());
    }

    @Test
    public void removedTankCannotExecuteAlreadyQueuedCommands() {
        GameField field = new GameField(5, 4);
        Tank tank = addTank(field, 1, 1);
        Command move = new MoveCommand(tank, Direction.RIGHT);
        Command shoot = new ShootCommand(tank);
        field.removeObject(tank);
        move.execute();
        shoot.execute();
        assertEquals(new GridPoint2(1, 1), tank.getTilePosition());
        assertFalse(tank.isMoving());
        assertTrue(field.getObjects().isEmpty());
    }

    @Test
    public void rejectsMissingTank() {
        assertThrows(NullPointerException.class, () -> new ShootCommand(null));
    }

    private Tank addTank(GameField field, int x, int y) {
        Tank tank = new Tank(field, x, y, 0.4f);
        field.addObstacle(tank);
        return tank;
    }
}
