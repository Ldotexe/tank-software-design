package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class TankStateSimulationTest {
    @Test(timeout = 2000)
    public void hitDuringMovementSlowsRemainingPathToMediumSpeedForAnyTickSize() {
        assertSameResultAfterHit(30, 0.875f);
    }

    @Test(timeout = 2000)
    public void hitDuringMovementSlowsRemainingPathToHeavySpeedForAnyTickSize() {
        assertSameResultAfterHit(86, 0.75f);
    }

    private void assertSameResultAfterHit(int damage, float expectedProgress) {
        Scene largeTick = scene(damage);
        Scene smallTicks = scene(damage);

        largeTick.session.update(0.5f);
        for (int frame = 0; frame < 10; frame++) {
            smallTicks.session.update(0.05f);
        }

        assertEquals(100 - damage, largeTick.target.getHealth());
        assertEquals(expectedProgress, largeTick.target.getMovementProgress(), 0.000001f);
        assertTrue(largeTick.target.isMoving());
        assertEquals(new GridPoint2(3, 1), largeTick.target.getTilePosition());
        assertEquals(new GridPoint2(3, 2), largeTick.target.getDestinationTile());
        assertEquivalent(largeTick, smallTicks);
        assertFalse(largeTick.field.isTileUnoccupied(new GridPoint2(3, 1)));
        assertFalse(largeTick.field.isTileUnoccupied(new GridPoint2(3, 2)));
        assertFalse(largeTick.field.getObjects().stream().anyMatch(object -> object instanceof Bullet));
        assertFalse(smallTicks.field.getObjects().stream().anyMatch(object -> object instanceof Bullet));

        largeTick.session.update(1f);
        for (int frame = 0; frame < 20; frame++) {
            smallTicks.session.update(0.05f);
        }

        assertFalse(largeTick.target.isMoving());
        assertEquals(new GridPoint2(3, 2), largeTick.target.getTilePosition());
        assertTrue(largeTick.field.isTileUnoccupied(new GridPoint2(3, 1)));
        assertEquivalent(largeTick, smallTicks);
    }

    private void assertEquivalent(Scene first, Scene second) {
        assertEquals(first.target.getHealth(), second.target.getHealth());
        assertEquals(first.target.getTilePosition(), second.target.getTilePosition());
        assertEquals(first.target.getDestinationTile(), second.target.getDestinationTile());
        assertEquals(first.target.getMovementProgress(), second.target.getMovementProgress(), 0.000001f);
        assertEquals(first.target.isMoving(), second.target.isMoving());
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 8; x++) {
                GridPoint2 tile = new GridPoint2(x, y);
                assertEquals(first.field.isTileUnoccupied(tile), second.field.isTileUnoccupied(tile));
            }
        }
    }

    private Scene scene(int damage) {
        GameField field = new GameField(8, 4);
        Tank owner = new Tank(field, 0, 0, 0.4f);
        Tank target = new Tank(field, 3, 1, 0.4f);
        field.addObject(owner);
        field.addObject(target);
        field.addObject(new Bullet(owner, new GridPoint2(1, 1), Direction.RIGHT,
                new BulletSettings(damage, 0.1f)));
        target.tryStartMoving(Direction.UP);
        return new Scene(field, target);
    }

    private static final class Scene {
        private final GameField field;
        private final Tank target;
        private final GameSession session;

        private Scene(GameField field, Tank target) {
            this.field = field;
            this.target = target;
            session = new GameSession(field);
        }
    }
}
