package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class BulletTest {
    @Test
    public void storesOwnerDirectionSettingsAndCopiesCoordinates() {
        Tank owner = new Tank(tile -> true, 0, 0, 0.4f);
        GridPoint2 start = new GridPoint2(2, 3);
        Bullet bullet = new Bullet(owner, start, Direction.UP, new BulletSettings(17, 0.2f));
        start.set(99, 99);
        bullet.getTilePosition().set(99, 99);
        bullet.getDestinationTile().set(99, 99);

        assertSame(owner, bullet.getOwner());
        assertEquals(Direction.UP, bullet.getDirection());
        assertEquals(17, bullet.getDamage());
        assertEquals(new GridPoint2(2, 3), bullet.getTilePosition());
        assertEquals(new GridPoint2(2, 4), bullet.getDestinationTile());
        assertEquals(0f, bullet.getMovementProgress(), 0f);
    }

    @Test
    public void advancesProgressAndMovesThroughEveryTile() {
        Tank owner = new Tank(tile -> true, 0, 0, 0.4f);
        Bullet bullet = new Bullet(owner, new GridPoint2(1, 1), Direction.RIGHT,
                new BulletSettings(25, 0.25f));

        bullet.update(0.125f);
        assertEquals(new GridPoint2(1, 1), bullet.getTilePosition());
        assertEquals(0.5f, bullet.getMovementProgress(), 0.00001f);
        bullet.update(0.625f);
        assertEquals(new GridPoint2(4, 1), bullet.getTilePosition());
        assertEquals(new GridPoint2(5, 1), bullet.getDestinationTile());
        assertEquals(0f, bullet.getMovementProgress(), 0.00001f);
    }

    @Test
    public void rejectsInvalidTimeWithoutMoving() {
        Tank owner = new Tank(tile -> true, 0, 0, 0.4f);
        Bullet bullet = new Bullet(owner, new GridPoint2(1, 1), Direction.RIGHT, BulletSettings.DEFAULT);

        for (float time : new float[]{-1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> bullet.update(time));
        }
        bullet.update(0f);
        assertEquals(new GridPoint2(1, 1), bullet.getTilePosition());
        assertEquals(0f, bullet.getMovementProgress(), 0f);
    }

    @Test
    public void commitsPositionWhenElapsedTimeRoundsToTileDuration() {
        Tank owner = new Tank(tile -> true, 0, 0, 0.4f);
        Bullet bullet = new Bullet(owner, new GridPoint2(1, 1), Direction.RIGHT, BulletSettings.DEFAULT);
        bullet.update(0.09);

        bullet.update(Math.nextDown(bullet.timeUntilNextTile()));

        assertEquals(new GridPoint2(2, 1), bullet.getTilePosition());
        assertEquals(new GridPoint2(3, 1), bullet.getDestinationTile());
        assertEquals(0f, bullet.getMovementProgress(), 0f);
    }

    @Test
    public void rejectsMissingOwnerPositionDirectionAndSettings() {
        Tank owner = new Tank(tile -> true, 0, 0, 0.4f);
        GridPoint2 start = new GridPoint2();
        assertThrows(NullPointerException.class,
                () -> new Bullet(null, start, Direction.RIGHT, BulletSettings.DEFAULT));
        assertThrows(NullPointerException.class,
                () -> new Bullet(owner, null, Direction.RIGHT, BulletSettings.DEFAULT));
        assertThrows(NullPointerException.class,
                () -> new Bullet(owner, start, null, BulletSettings.DEFAULT));
        assertThrows(NullPointerException.class,
                () -> new Bullet(owner, start, Direction.RIGHT, null));
    }
}
