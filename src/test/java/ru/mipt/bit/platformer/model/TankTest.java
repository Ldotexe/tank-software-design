package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class TankTest {
    private static final float DURATION = 0.4f;
    private static final float EPSILON = 0.00001f;

    @Test
    public void startsIdleFacingRight() {
        Tank tank = new Tank(new GameField(), 1, 1, 0.4f);
        assertFalse(tank.isMoving());
        assertEquals(Direction.RIGHT, tank.getDirection());
        assertEquals(new GridPoint2(1, 1), tank.getTilePosition());
        assertEquals(tank.getTilePosition(), tank.getDestinationTile());
        assertEquals(1f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void movesOneTileInEveryDirection() {
        for (Direction direction : Direction.values()) {
            Tank tank = new Tank(new GameField(), 2, 2, 0.4f);
            tank.tryStartMoving(direction);
            assertTrue(tank.isMoving());
            assertEquals(direction, tank.getDirection());
            assertEquals(0f, tank.getMovementProgress(), 0f);
            assertEquals(direction.calculateAdjacentTile(new GridPoint2(2, 2)), tank.getDestinationTile());
            tank.update(DURATION);
            assertFalse(tank.isMoving());
            assertEquals(tank.getDestinationTile(), tank.getTilePosition());
        }
    }

    @Test
    public void advancesProgressBeforeCommittingTilePosition() {
        Tank tank = new Tank(new GameField(), 1, 1, 0.4f);
        tank.tryStartMoving(Direction.UP);
        tank.update(DURATION / 2);
        assertEquals(0.5f, tank.getMovementProgress(), EPSILON);
        assertEquals(new GridPoint2(1, 1), tank.getTilePosition());
        tank.update(DURATION / 2);
        assertEquals(new GridPoint2(1, 2), tank.getTilePosition());
    }

    @Test
    public void turnsButDoesNotMoveIntoTreeInAnyDirection() {
        for (Direction direction : Direction.values()) {
            GameField field = new GameField();
            GridPoint2 obstacle = direction.calculateAdjacentTile(new GridPoint2(1, 1));
            field.addObstacle(new Tree(obstacle.x, obstacle.y));
            Tank tank = new Tank(field, 1, 1, 0.4f);
            tank.tryStartMoving(direction);
            tank.update(DURATION);
            assertEquals(direction, tank.getDirection());
            assertFalse(tank.isMoving());
            assertEquals(new GridPoint2(1, 1), tank.getTilePosition());
            assertEquals(tank.getTilePosition(), tank.getDestinationTile());
        }
    }

    @Test
    public void ignoresNewDirectionsDuringMovement() {
        Tank tank = new Tank(new GameField(), 1, 1, 0.4f);
        tank.tryStartMoving(Direction.UP);
        tank.update(DURATION / 2);
        tank.tryStartMoving(Direction.LEFT);
        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationTile());
        assertEquals(0.5f, tank.getMovementProgress(), EPSILON);
    }

    @Test
    public void largeDeltaStopsAtDestinationAndNextMoveStartsThere() {
        Tank tank = new Tank(new GameField(), 1, 1, 0.4f);
        tank.tryStartMoving(Direction.UP);
        tank.update(DURATION * 10);
        assertEquals(1f, tank.getMovementProgress(), 0f);
        assertEquals(new GridPoint2(1, 2), tank.getTilePosition());
        tank.tryStartMoving(Direction.RIGHT);
        tank.update(DURATION);
        assertEquals(new GridPoint2(2, 2), tank.getTilePosition());
    }

    @Test
    public void zeroDeltaAndIdleUpdatesDoNotMoveTank() {
        Tank tank = new Tank(new GameField(), 1, 1, 0.4f);
        tank.update(DURATION);
        assertEquals(new GridPoint2(1, 1), tank.getTilePosition());
        tank.tryStartMoving(Direction.UP);
        tank.update(0f);
        assertEquals(0f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void rejectsInvalidTimeWithoutChangingProgress() {
        Tank tank = new Tank(new GameField(), 1, 1, 0.4f);
        tank.tryStartMoving(Direction.UP);
        for (float invalid : new float[]{-1f, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> tank.update(invalid));
        }
        assertEquals(0f, tank.getMovementProgress(), 0f);
    }

    @Test
    public void returnedCoordinatesCannotChangeMovement() {
        Tank tank = new Tank(new GameField(), 1, 1, 0.4f);
        tank.tryStartMoving(Direction.UP);
        tank.getTilePosition().set(99, 99);
        tank.getDestinationTile().set(99, 99);
        tank.update(DURATION);
        assertEquals(new GridPoint2(1, 2), tank.getTilePosition());
    }

    @Test
    public void canUseMovementRulesWithoutGameField() {
        MovementRules nonNegativeCoordinates = tile -> tile.x >= 0 && tile.y >= 0;
        Tank tank = new Tank(nonNegativeCoordinates, 0, 0, 0.4f);
        tank.tryStartMoving(Direction.LEFT);
        assertFalse(tank.isMoving());
        assertEquals(Direction.LEFT, tank.getDirection());
        tank.tryStartMoving(Direction.UP);
        tank.update(0.4f);
        assertEquals(new GridPoint2(0, 1), tank.getTilePosition());
    }

    @Test
    public void usesIndividualMovementDuration() {
        Tank fastTank = new Tank(tile -> true, 0, 0, 0.2f);
        Tank slowTank = new Tank(tile -> true, 0, 0, 0.8f);
        fastTank.tryStartMoving(Direction.RIGHT);
        slowTank.tryStartMoving(Direction.RIGHT);
        fastTank.update(0.2f);
        slowTank.update(0.2f);
        assertFalse(fastTank.isMoving());
        assertTrue(slowTank.isMoving());
        assertEquals(0.25f, slowTank.getMovementProgress(), EPSILON);
    }

    @Test
    public void rejectsInvalidDuration() {
        for (float duration : new float[]{0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Tank(tile -> true, 0, 0, duration));
        }
    }
}
