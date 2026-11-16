package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TankShootingTest {
    @Test
    public void shootsIntoAdjacentTileInEveryDirection() {
        for (Direction direction : Direction.values()) {
            GameField field = new GameField(5, 5);
            Tank tank = new Tank(field, 2, 2, 0.4f);
            field.addObject(tank);
            tank.tryStartMoving(direction);

            tank.shoot();

            Bullet bullet = bullets(field).get(0);
            assertSame(tank, bullet.getOwner());
            assertEquals(direction, bullet.getDirection());
            assertEquals(direction.calculateAdjacentTile(new GridPoint2(2, 2)), bullet.getTilePosition());
            assertEquals(25, bullet.getDamage());
            assertEquals(0f, bullet.getMovementProgress(), 0f);
        }
    }

    @Test
    public void movingTankDoesNotHitItselfWhenShootingIntoItsDestination() {
        GameField field = new GameField(5, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f);
        field.addObject(tank);
        tank.tryStartMoving(Direction.RIGHT);
        tank.update(0.2f);

        tank.shoot();
        Bullet bullet = bullets(field).get(0);
        new GameSession(field).update(0f);

        assertTrue(field.contains(bullet));
        assertEquals(100, tank.getHealth());
        assertEquals(tank.getDestinationTile(), bullet.getTilePosition());
    }

    @Test
    public void usesConfiguredHealthDamageAndBulletDuration() {
        GameField field = new GameField(5, 3);
        Tank tank = new Tank(field, 0, 0, 0.4f, 7, new BulletSettings(3, 0.2f));
        field.addObject(tank);

        tank.shoot();
        Bullet bullet = bullets(field).get(0);
        new GameSession(field).update(0.1f);

        assertEquals(7, tank.getHealth());
        assertEquals(3, bullet.getDamage());
        assertEquals(0.5f, bullet.getMovementProgress(), 0.00001f);
    }

    @Test
    public void zeroHealthIsAliveAndNegativeHealthRemovesTank() {
        GameField field = new GameField(5, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f, 25, BulletSettings.DEFAULT);
        field.addObject(tank);

        tank.takeDamage(25);

        assertEquals(0, tank.getHealth());
        assertTrue(tank.isAlive());
        assertTrue(field.contains(tank));
        tank.takeDamage(1);
        assertEquals(-1, tank.getHealth());
        assertFalse(tank.isAlive());
        assertFalse(field.contains(tank));
        assertTrue(field.isTileUnoccupied(new GridPoint2(1, 1)));
    }

    @Test
    public void removedTankCannotMoveShootOrTakeMoreDamage() {
        GameField field = new GameField(5, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f);
        field.addObject(tank);
        field.removeObject(tank);

        tank.tryStartMoving(Direction.UP);
        tank.shoot();
        tank.takeDamage(25);
        tank.update(0.4f);

        assertFalse(tank.isMoving());
        assertEquals(Direction.RIGHT, tank.getDirection());
        assertEquals(new GridPoint2(1, 1), tank.getTilePosition());
        assertEquals(100, tank.getHealth());
        assertTrue(field.getObjects().isEmpty());
    }

    @Test
    public void destroyedTankCannotActOrCreateBullets() {
        GameField field = new GameField(5, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f);
        field.addObject(tank);
        tank.takeDamage(101);

        tank.tryStartMoving(Direction.UP);
        tank.shoot();
        tank.takeDamage(50);

        assertFalse(tank.isAlive());
        assertEquals(-1, tank.getHealth());
        assertFalse(tank.isMoving());
        assertTrue(field.getObjects().isEmpty());
    }

    @Test
    public void unregisteredTankNeedsLevelBeforeShooting() {
        Tank tank = new Tank(tile -> true, 1, 1, 0.4f);

        IllegalStateException error = assertThrows(IllegalStateException.class, tank::shoot);

        assertTrue(error.getMessage().contains("level"));
    }

    @Test
    public void rejectsNegativeHealthDamageAndMissingSettings() {
        assertThrows(IllegalArgumentException.class,
                () -> new Tank(tile -> true, 0, 0, 0.4f, -1, BulletSettings.DEFAULT));
        assertThrows(NullPointerException.class,
                () -> new Tank(tile -> true, 0, 0, 0.4f, 100, null));
        Tank tank = new Tank(tile -> true, 0, 0, 0.4f);
        assertThrows(IllegalArgumentException.class, () -> tank.takeDamage(-1));
        assertEquals(100, tank.getHealth());
        tank.takeDamage(0);
        assertEquals(100, tank.getHealth());
    }

    private List<Bullet> bullets(GameField field) {
        List<Bullet> bullets = new ArrayList<>();
        for (GameObject object : field.getObjects()) {
            if (object instanceof Bullet) {
                bullets.add((Bullet) object);
            }
        }
        return bullets;
    }
}
