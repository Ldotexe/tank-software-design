package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BulletCollisionTest {
    @Test
    public void shotIntoTreeIsRemovedDuringTickAndTreeRemains() {
        GameField field = new GameField(5, 3);
        Tank owner = addTank(field, 0, 1);
        Tree tree = new Tree(1, 1);
        field.addObject(tree);

        owner.shoot();
        Bullet bullet = bullets(field).get(0);
        assertTrue(field.contains(bullet));
        new GameSession(field).update(0f);

        assertFalse(field.contains(bullet));
        assertTrue(field.contains(tree));
        assertEquals(100, owner.getHealth());
    }

    @Test
    public void shotOutsideLevelIsRemovedDuringTick() {
        GameField field = new GameField(3, 2);
        Tank owner = addTank(field, 2, 1);

        owner.shoot();
        Bullet bullet = bullets(field).get(0);
        new GameSession(field).update(0f);

        assertFalse(field.contains(bullet));
        assertEquals(100, owner.getHealth());
    }

    @Test
    public void movingBulletDisappearsAtEveryBoundary() {
        assertLeavesField(new GridPoint2(0, 1), Direction.LEFT);
        assertLeavesField(new GridPoint2(4, 1), Direction.RIGHT);
        assertLeavesField(new GridPoint2(2, 0), Direction.DOWN);
        assertLeavesField(new GridPoint2(2, 2), Direction.UP);
    }

    @Test
    public void crossingTankCellDealsDamageOnlyOnce() {
        GameField field = new GameField(8, 3);
        Tank owner = addTank(field, 0, 1);
        Tank target = addTank(field, 3, 1);
        Bullet bullet = addBullet(field, owner, 1, 1, Direction.RIGHT);
        GameSession session = new GameSession(field);

        session.update(0.5f);
        session.update(0.5f);

        assertFalse(field.contains(bullet));
        assertTrue(field.contains(target));
        assertEquals(75, target.getHealth());
    }

    @Test
    public void fatalHitRemovesTankAndReleasesReservedTiles() {
        GameField field = new GameField(8, 3);
        Tank owner = addTank(field, 0, 0);
        Tank target = new Tank(field, 3, 1, 0.4f, 10, BulletSettings.DEFAULT);
        field.addObject(target);
        target.tryStartMoving(Direction.UP);
        Bullet bullet = addBullet(field, owner, 2, 1, Direction.RIGHT);

        new GameSession(field).update(0.1f);

        assertEquals(-15, target.getHealth());
        assertFalse(field.contains(target));
        assertFalse(field.contains(bullet));
        assertTrue(field.isTileUnoccupied(new GridPoint2(3, 1)));
        assertTrue(field.isTileUnoccupied(new GridPoint2(3, 2)));
    }

    @Test
    public void hitsBothReservedTilesOfMovingTank() {
        assertHitsMovingTank(1);
        assertHitsMovingTank(2);
    }

    @Test
    public void largeDeltaDoesNotTunnelThroughTree() {
        GameField field = new GameField(10, 3);
        Tank owner = addTank(field, 0, 1);
        Tree tree = new Tree(3, 1);
        field.addObject(tree);
        Bullet bullet = addBullet(field, owner, 1, 1, Direction.RIGHT);

        new GameSession(field).update(1f);

        assertFalse(field.contains(bullet));
        assertEquals(new GridPoint2(3, 1), bullet.getTilePosition());
        assertTrue(field.contains(tree));
    }

    @Test
    public void allBulletsInSameSpawnTileDisappear() {
        GameField field = new GameField(8, 3);
        Tank owner = addTank(field, 0, 0);
        Bullet first = addBullet(field, owner, 2, 1, Direction.RIGHT);
        Bullet second = addBullet(field, owner, 2, 1, Direction.LEFT);
        Bullet third = addBullet(field, owner, 2, 1, Direction.UP);

        new GameSession(field).update(0f);

        assertFalse(field.contains(first));
        assertFalse(field.contains(second));
        assertFalse(field.contains(third));
    }

    @Test
    public void headOnBulletsCannotSwapTiles() {
        GameField field = new GameField(8, 3);
        Tank owner = addTank(field, 0, 0);
        Bullet first = addBullet(field, owner, 2, 1, Direction.RIGHT);
        Bullet second = addBullet(field, owner, 3, 1, Direction.LEFT);

        new GameSession(field).update(0.1f);

        assertFalse(field.contains(first));
        assertFalse(field.contains(second));
    }

    @Test
    public void bulletsEnteringSameTileAreBothRemoved() {
        GameField field = new GameField(8, 3);
        Tank owner = addTank(field, 0, 0);
        Bullet first = addBullet(field, owner, 1, 1, Direction.RIGHT);
        Bullet second = addBullet(field, owner, 3, 1, Direction.LEFT);

        new GameSession(field).update(0.1f);

        assertFalse(field.contains(first));
        assertFalse(field.contains(second));
    }

    @Test
    public void equallyFastFollowingBulletsDoNotCollide() {
        GameField field = new GameField(8, 3);
        Tank owner = addTank(field, 0, 0);
        Bullet first = addBullet(field, owner, 1, 1, Direction.RIGHT);
        Bullet second = addBullet(field, owner, 2, 1, Direction.RIGHT);

        new GameSession(field).update(0.1f);

        assertTrue(field.contains(first));
        assertTrue(field.contains(second));
        assertEquals(new GridPoint2(2, 1), first.getTilePosition());
        assertEquals(new GridPoint2(3, 1), second.getTilePosition());
    }

    @Test
    public void largeDeltaStillDetectsMeetingBullets() {
        GameField field = new GameField(10, 3);
        Tank owner = addTank(field, 0, 0);
        Bullet first = addBullet(field, owner, 1, 1, Direction.RIGHT);
        Bullet second = addBullet(field, owner, 7, 1, Direction.LEFT);

        new GameSession(field).update(1f);

        assertFalse(field.contains(first));
        assertFalse(field.contains(second));
    }

    @Test
    public void largeAndSmallTicksHitMovingTargetBeforeItVacatesSource() {
        GameField largeStepField = movingTargetScene();
        GameField smallStepField = movingTargetScene();
        Tank largeStepTarget = (Tank) largeStepField.getObjects().get(1);
        Tank smallStepTarget = (Tank) smallStepField.getObjects().get(1);

        new GameSession(largeStepField).update(0.5f);
        GameSession smallStepSession = new GameSession(smallStepField);
        for (int frame = 0; frame < 10; frame++) {
            smallStepSession.update(0.05f);
        }

        assertEquals(75, largeStepTarget.getHealth());
        assertEquals(largeStepTarget.getHealth(), smallStepTarget.getHealth());
        assertEquals(new GridPoint2(3, 2), largeStepTarget.getTilePosition());
        assertEquals(largeStepTarget.getTilePosition(), smallStepTarget.getTilePosition());
        assertTrue(bullets(largeStepField).isEmpty());
        assertTrue(bullets(smallStepField).isEmpty());
    }

    @Test
    public void simultaneousHitsRemoveAllBulletsEvenWhenFirstHitDestroysTank() {
        GameField field = new GameField(8, 3);
        Tank owner = addTank(field, 0, 0);
        Tank target = new Tank(field, 3, 1, 0.4f, 10, BulletSettings.DEFAULT);
        field.addObject(target);
        Bullet first = addBullet(field, owner, 3, 1, Direction.RIGHT);
        Bullet second = addBullet(field, owner, 3, 1, Direction.LEFT);

        new GameSession(field).update(0f);

        assertFalse(field.contains(target));
        assertFalse(field.contains(first));
        assertFalse(field.contains(second));
    }

    private void assertLeavesField(GridPoint2 start, Direction direction) {
        GameField field = new GameField(5, 3);
        Tank owner = addTank(field, 0, 0);
        Bullet bullet = addBullet(field, owner, start.x, start.y, direction);
        GameSession session = new GameSession(field);

        session.update(0.05f);
        assertTrue(field.contains(bullet));
        session.update(0.05f);
        assertFalse(field.contains(bullet));
    }

    private void assertHitsMovingTank(int bulletY) {
        GameField field = new GameField(8, 4);
        Tank owner = addTank(field, 0, 0);
        Tank target = addTank(field, 2, 1);
        target.tryStartMoving(Direction.UP);
        Bullet bullet = addBullet(field, owner, 1, bulletY, Direction.RIGHT);

        new GameSession(field).update(0.1f);

        assertEquals(75, target.getHealth());
        assertFalse(field.contains(bullet));
        assertTrue(target.isMoving());
    }

    private GameField movingTargetScene() {
        GameField field = new GameField(8, 4);
        Tank owner = addTank(field, 0, 0);
        Tank target = addTank(field, 3, 1);
        target.tryStartMoving(Direction.UP);
        target.update(0.15f);
        addBullet(field, owner, 1, 1, Direction.RIGHT);
        return field;
    }

    private Tank addTank(GameField field, int x, int y) {
        Tank tank = new Tank(field, x, y, 0.4f);
        field.addObject(tank);
        return tank;
    }

    private Bullet addBullet(GameField field, Tank owner, int x, int y, Direction direction) {
        Bullet bullet = new Bullet(owner, new GridPoint2(x, y), direction, BulletSettings.DEFAULT);
        field.addObject(bullet);
        return bullet;
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
