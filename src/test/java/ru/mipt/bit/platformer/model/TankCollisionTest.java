package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class TankCollisionTest {
    private static final float DURATION = 0.4f;

    @Test
    public void idleTankOccupiesOnlyItsCurrentTile() {
        GameField field = new GameField(5, 3);
        Tank tank = addTank(field, 1, 1);

        assertTrue(tank.occupiesTile(new GridPoint2(1, 1)));
        assertFalse(tank.occupiesTile(new GridPoint2(2, 1)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(1, 1)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(2, 1)));
    }

    @Test
    public void movingTankReservesSourceAndDestinationImmediately() {
        GameField field = new GameField(5, 3);
        Tank tank = addTank(field, 1, 1);

        tank.tryStartMoving(Direction.RIGHT);

        assertFalse(field.isTileUnoccupied(new GridPoint2(1, 1)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(2, 1)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(3, 1)));

        tank.update(DURATION / 2);

        assertFalse(field.isTileUnoccupied(new GridPoint2(1, 1)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(2, 1)));
    }

    @Test
    public void completedMovementReleasesOnlyTheSourceTile() {
        GameField field = new GameField(5, 3);
        Tank tank = addTank(field, 1, 1);

        tank.tryStartMoving(Direction.RIGHT);
        tank.update(DURATION);

        assertTrue(field.isTileUnoccupied(new GridPoint2(1, 1)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(2, 1)));
        assertFalse(tank.occupiesTile(new GridPoint2(1, 1)));
        assertTrue(tank.occupiesTile(new GridPoint2(2, 1)));
    }

    @Test
    public void secondTankCannotMoveIntoReservedDestination() {
        GameField field = new GameField(5, 3);
        Tank first = addTank(field, 0, 1);
        Tank second = addTank(field, 2, 1);

        first.tryStartMoving(Direction.RIGHT);
        second.tryStartMoving(Direction.LEFT);

        assertTrue(first.isMoving());
        assertFalse(second.isMoving());
        assertEquals(new GridPoint2(1, 1), first.getDestinationTile());
        assertEquals(new GridPoint2(2, 1), second.getDestinationTile());

        first.update(DURATION);
        second.tryStartMoving(Direction.LEFT);

        assertFalse(second.isMoving());
    }

    @Test
    public void adjacentTanksCannotSwapTiles() {
        GameField field = new GameField(5, 3);
        Tank first = addTank(field, 1, 1);
        Tank second = addTank(field, 2, 1);

        first.tryStartMoving(Direction.RIGHT);
        second.tryStartMoving(Direction.LEFT);
        first.update(DURATION);
        second.update(DURATION);

        assertFalse(first.isMoving());
        assertFalse(second.isMoving());
        assertEquals(new GridPoint2(1, 1), first.getTilePosition());
        assertEquals(new GridPoint2(2, 1), second.getTilePosition());
    }

    @Test
    public void followingTankWaitsUntilSourceTileIsFullyReleased() {
        GameField field = new GameField(5, 3);
        Tank leader = addTank(field, 1, 1);
        Tank follower = addTank(field, 0, 1);

        leader.tryStartMoving(Direction.RIGHT);
        leader.update(DURATION / 2);
        follower.tryStartMoving(Direction.RIGHT);

        assertFalse(follower.isMoving());
        assertEquals(new GridPoint2(0, 1), follower.getTilePosition());

        leader.update(DURATION / 2);
        follower.tryStartMoving(Direction.RIGHT);

        assertTrue(follower.isMoving());
        assertEquals(new GridPoint2(1, 1), follower.getDestinationTile());
        follower.update(DURATION);
        assertEquals(new GridPoint2(1, 1), follower.getTilePosition());
        assertEquals(new GridPoint2(2, 1), leader.getTilePosition());
    }

    @Test
    public void tanksCanMoveAlongSeparateRows() {
        GameField field = new GameField(5, 3);
        Tank first = addTank(field, 0, 0);
        Tank second = addTank(field, 0, 1);

        first.tryStartMoving(Direction.RIGHT);
        second.tryStartMoving(Direction.RIGHT);

        assertTrue(first.isMoving());
        assertTrue(second.isMoving());
        first.update(DURATION);
        second.update(DURATION);
        assertEquals(new GridPoint2(1, 0), first.getTilePosition());
        assertEquals(new GridPoint2(1, 1), second.getTilePosition());
    }

    @Test
    public void cannotLeaveAnyEdgeOfRectangularField() {
        assertBlockedAtEdge(Direction.LEFT, 0, 1);
        assertBlockedAtEdge(Direction.RIGHT, 4, 1);
        assertBlockedAtEdge(Direction.DOWN, 2, 0);
        assertBlockedAtEdge(Direction.UP, 2, 2);
    }

    @Test
    public void canReachCornerInsideRectangularField() {
        GameField field = new GameField(5, 3);
        Tank tank = addTank(field, 4, 1);

        tank.tryStartMoving(Direction.UP);
        tank.update(DURATION);

        assertEquals(new GridPoint2(4, 2), tank.getTilePosition());
    }

    @Test
    public void singleTileFieldKeepsTankInside() {
        GameField field = new GameField(1, 1);
        Tank tank = addTank(field, 0, 0);

        for (Direction direction : Direction.values()) {
            tank.tryStartMoving(direction);
            assertFalse(tank.isMoving());
            assertEquals(new GridPoint2(0, 0), tank.getTilePosition());
        }
    }

    private void assertBlockedAtEdge(Direction direction, int x, int y) {
        GameField field = new GameField(5, 3);
        Tank tank = addTank(field, x, y);

        tank.tryStartMoving(direction);
        tank.update(DURATION);

        assertFalse(tank.isMoving());
        assertEquals(direction, tank.getDirection());
        assertEquals(new GridPoint2(x, y), tank.getTilePosition());
        assertEquals(new GridPoint2(x, y), tank.getDestinationTile());
    }

    private Tank addTank(GameField field, int x, int y) {
        Tank tank = new Tank(field, x, y, DURATION);
        field.addObstacle(tank);
        return tank;
    }
}
