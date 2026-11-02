package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.game.Updatable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class GameSessionTest {
    @Test
    public void updatesAllObjectsInOrderWithSameElapsedTime() {
        List<String> updates = new ArrayList<>();
        GameField field = new GameField(10, 8);
        field.addObject(new TestObject(time -> updates.add("player:" + time)));
        field.addObject(new TestObject(time -> updates.add("projectile:" + time)));
        GameSession session = new GameSession(field);
        session.update(0.25f);
        assertEquals(Arrays.asList("player:0.25", "projectile:0.25"), updates);
    }

    @Test
    public void usesSuppliedPlayerAndCollisionRules() {
        GameField field = new GameField(10, 8);
        field.addObstacle(new Tree(4, 6));
        Tank player = new Tank(field, 4, 4, 0.2f);
        field.addObject(player);
        GameSession session = new GameSession(field);
        player.tryStartMoving(Direction.UP);
        session.update(0.2f);
        assertEquals(new GridPoint2(4, 5), player.getTilePosition());
        player.tryStartMoving(Direction.UP);
        session.update(0.2f);
        assertEquals(new GridPoint2(4, 5), player.getTilePosition());
        assertFalse(player.isMoving());
    }

    @Test
    public void updatesObjectsAddedAfterSessionCreation() {
        GameField field = new GameField(10, 8);
        GameSession session = new GameSession(field);
        Tank player = new Tank(field, 1, 1, 0.4f);
        field.addObject(player);
        player.tryStartMoving(Direction.RIGHT);
        session.update(0.4f);
        assertEquals(new GridPoint2(2, 1), player.getTilePosition());
    }

    @Test
    public void supportsEmptySession() {
        new GameSession(new GameField(10, 8)).update(0.4f);
    }

    @Test
    public void skipsObjectsRemovedBeforeTheirTurn() {
        GameField field = new GameField(10, 8);
        List<String> calls = new ArrayList<>();
        TestObject removed = new TestObject(time -> calls.add("removed"));
        field.addObject(new TestObject(time -> field.removeObject(removed)));
        field.addObject(removed);

        new GameSession(field).update(0.1f);

        assertTrue(calls.isEmpty());
    }

    @Test
    public void objectsAddedDuringTickStartUpdatingNextTick() {
        GameField field = new GameField(10, 8);
        List<String> calls = new ArrayList<>();
        TestObject added = new TestObject(time -> calls.add("added"));
        field.addObject(new TestObject(time -> field.addObject(added)));
        GameSession session = new GameSession(field);

        session.update(0.1f);
        assertTrue(calls.isEmpty());
        session.update(0.1f);
        assertEquals(Collections.singletonList("added"), calls);
    }

    @Test
    public void updatesBulletsAddedBeforeTickWithoutRecreatingSession() {
        GameField field = new GameField(10, 8);
        Tank tank = new Tank(field, 0, 0, 0.4f);
        field.addObject(tank);
        GameSession session = new GameSession(field);

        tank.shoot();
        Bullet bullet = (Bullet) field.getObjects().get(1);
        session.update(0.1f);

        assertEquals(new GridPoint2(2, 0), bullet.getTilePosition());
    }

    @Test
    public void commitsTankPositionWhenBulletSubstepsRoundProgressToOne() {
        GameField field = new GameField(100, 100);
        Tank tank = new Tank(field, 0, 0, 0.4f);
        Tank owner = new Tank(field, 0, 10, 0.4f);
        field.addObject(tank);
        field.addObject(owner);
        field.addObject(new Bullet(owner, new GridPoint2(1, 10), Direction.RIGHT, BulletSettings.DEFAULT));
        tank.tryStartMoving(Direction.UP);
        GameSession session = new GameSession(field);

        for (float time : new float[]{0.05f, 0.05f, 0.05f, 0.016f, 0.05f, 0.05f,
                0.1f, 1f / 60, 0.1f}) {
            session.update(time);
        }

        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(0, 1), tank.getTilePosition());
        assertEquals(tank.getDestinationTile(), tank.getTilePosition());
        assertTrue(field.isTileUnoccupied(new GridPoint2(0, 0)));
    }

    @Test
    public void rejectsNullFieldAndInvalidTime() {
        assertThrows(NullPointerException.class, () -> new GameSession(null));
        GameSession session = new GameSession(new GameField(10, 8));
        for (float time : new float[]{-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> session.update(time));
        }
    }

    private static final class TestObject extends GameObject implements Updatable {
        private final Consumer<Float> update;

        private TestObject(Consumer<Float> update) {
            super(0, 0);
            this.update = update;
        }

        @Override
        public void update(float elapsedSeconds) {
            update.accept(elapsedSeconds);
        }
    }
}
