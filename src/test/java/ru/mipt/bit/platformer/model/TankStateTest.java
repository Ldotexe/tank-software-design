package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.state.HeavyDamageState;
import ru.mipt.bit.platformer.model.state.LightDamageState;
import ru.mipt.bit.platformer.model.state.MediumDamageState;
import ru.mipt.bit.platformer.model.state.TankState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleSupplier;

import static org.junit.Assert.*;

public class TankStateTest {
    private static final float MOVEMENT_DURATION = 0.4f;
    private static final float EPSILON = 0.00001f;

    @Test
    public void switchesStatesAtSeventyAndFifteenPercent() {
        int[] remainingHealth = {100, 71, 70, 15, 14, 0};
        TankState[] expected = {LightDamageState.INSTANCE, LightDamageState.INSTANCE,
                MediumDamageState.INSTANCE, MediumDamageState.INSTANCE,
                HeavyDamageState.INSTANCE, HeavyDamageState.INSTANCE};

        for (int index = 0; index < remainingHealth.length; index++) {
            Tank tank = tank(100);
            tank.takeDamage(100 - remainingHealth[index]);

            assertSame("Health: " + remainingHealth[index], expected[index], tank.getState());
        }
    }

    @Test
    public void thresholdsUseEachTanksInitialHealth() {
        int[] remainingHealth = {80, 57, 56, 12, 11};
        TankState[] expected = {LightDamageState.INSTANCE, LightDamageState.INSTANCE,
                MediumDamageState.INSTANCE, MediumDamageState.INSTANCE, HeavyDamageState.INSTANCE};

        for (int index = 0; index < remainingHealth.length; index++) {
            Tank tank = tank(80);
            tank.takeDamage(80 - remainingHealth[index]);

            assertSame("Health: " + remainingHealth[index], expected[index], tank.getState());
            assertEquals(80, tank.getMaxHealth());
        }
    }

    @Test
    public void largeHealthValuesDoNotOverflowThresholdCalculations() {
        int initialHealth = 2_000_000_000;
        int[] remainingHealth = {1_400_000_001, 1_400_000_000, 300_000_000, 299_999_999};
        TankState[] expected = {LightDamageState.INSTANCE, MediumDamageState.INSTANCE,
                MediumDamageState.INSTANCE, HeavyDamageState.INSTANCE};

        for (int index = 0; index < remainingHealth.length; index++) {
            Tank tank = tank(initialHealth);
            tank.takeDamage(initialHealth - remainingHealth[index]);

            assertSame("Health: " + remainingHealth[index], expected[index], tank.getState());
        }
    }

    @Test
    public void lightMediumAndHeavyStatesTakeOneTwoAndThreeBaseDurations() {
        int[] damage = {0, 30, 86};
        float[] durations = {MOVEMENT_DURATION, MOVEMENT_DURATION * 2, MOVEMENT_DURATION * 3};

        for (int index = 0; index < damage.length; index++) {
            Tank tank = tank(100);
            tank.takeDamage(damage[index]);
            tank.tryStartMoving(Direction.RIGHT);

            assertEquals(durations[index], tank.timeUntilArrival(), EPSILON);
            tank.update(durations[index] / 2);
            assertTrue(tank.isMoving());
            assertEquals(0.5f, tank.getMovementProgress(), EPSILON);
            assertEquals(durations[index] / 2, tank.timeUntilArrival(), EPSILON);
            tank.update(durations[index] / 2);
            assertFalse(tank.isMoving());
            assertEquals(new GridPoint2(1, 0), tank.getTilePosition());
        }
    }

    @Test
    public void damageChangesRemainingSpeedWithoutResettingMovementProgress() {
        Tank tank = tank(100);
        tank.tryStartMoving(Direction.RIGHT);
        tank.update(0.1f);
        assertEquals(0.25f, tank.getMovementProgress(), EPSILON);

        tank.takeDamage(30);

        assertSame(MediumDamageState.INSTANCE, tank.getState());
        assertEquals(0.25f, tank.getMovementProgress(), EPSILON);
        assertEquals(0.6d, tank.timeUntilArrival(), EPSILON);
        tank.update(0.2f);
        assertEquals(0.5f, tank.getMovementProgress(), EPSILON);
        assertEquals(new GridPoint2(0, 0), tank.getTilePosition());
        tank.takeDamage(56);
        assertSame(HeavyDamageState.INSTANCE, tank.getState());
        assertEquals(0.5f, tank.getMovementProgress(), EPSILON);
        assertEquals(0.6d, tank.timeUntilArrival(), EPSILON);
        tank.update(0.3f);
        assertEquals(0.75f, tank.getMovementProgress(), EPSILON);
        tank.update(0.3f);
        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(1, 0), tank.getTilePosition());
    }

    @Test
    public void onlyHeavyDamagePreventsShooting() {
        for (int damage : new int[]{0, 29, 30, 85, 86, 100}) {
            GameField field = new GameField(4, 3);
            Tank tank = registeredTank(field, 100);
            tank.takeDamage(damage);

            tank.shoot();

            assertEquals("Damage: " + damage, damage <= 85 ? 1 : 0, bulletCount(field));
        }
    }

    @Test
    public void zeroInitialHealthIsHeavyAndKeepsPreviousAliveRule() {
        GameField field = new GameField(4, 3);
        Tank tank = registeredTank(field, 0);

        assertSame(HeavyDamageState.INSTANCE, tank.getState());
        assertTrue(tank.isAlive());
        tank.tryStartMoving(Direction.RIGHT);
        assertEquals(MOVEMENT_DURATION * 3, tank.timeUntilArrival(), EPSILON);
        tank.shoot();
        assertEquals(0, bulletCount(field));
        tank.takeDamage(1);
        assertFalse(tank.isAlive());
        assertFalse(field.contains(tank));
    }

    @Test
    public void customStateControlsSpeedShootingAndHealthTransitions() {
        GameField field = new GameField(4, 3);
        Tank tank = registeredTank(field, 100);
        List<Integer> receivedHealth = new ArrayList<>();
        TankState boosted = new TankState() {
            @Override
            public double getSpeedMultiplier() {
                return 2d;
            }

            @Override
            public void shoot(Runnable fire) {
                fire.run();
            }

            @Override
            public TankState onHealthChanged(int health, int maxHealth) {
                receivedHealth.add(health);
                receivedHealth.add(maxHealth);
                return this;
            }
        };
        tank.changeState(boosted);
        tank.tryStartMoving(Direction.RIGHT);
        tank.update(0.1f);
        assertEquals(0.5f, tank.getMovementProgress(), EPSILON);

        tank.takeDamage(90);

        assertSame(boosted, tank.getState());
        assertEquals(Arrays.asList(10, 100), receivedHealth);
        tank.shoot();
        assertEquals(1, bulletCount(field));
        tank.update(0.1f);
        assertFalse(tank.isMoving());
    }

    @Test
    public void customStateCanChooseAnotherStateAfterDamage() {
        Tank tank = tank(100);
        TankState temporary = new TankState() {
            @Override
            public double getSpeedMultiplier() {
                return 2d;
            }

            @Override
            public void shoot(Runnable fire) {
                fire.run();
            }

            @Override
            public TankState onHealthChanged(int health, int maxHealth) {
                return HeavyDamageState.INSTANCE;
            }
        };
        tank.changeState(temporary);

        tank.takeDamage(1);

        assertSame(HeavyDamageState.INSTANCE, tank.getState());
        assertEquals(99, tank.getHealth());
    }

    @Test
    public void invalidStateSpeedIsRejectedWithoutReplacingCurrentState() {
        Tank tank = tank(100);
        TankState previous = tank.getState();
        assertThrows(NullPointerException.class, () -> tank.changeState(null));
        for (double speed : new double[]{0d, -1d, Double.NaN,
                Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            TankState invalid = new TankState() {
                @Override
                public double getSpeedMultiplier() {
                    return speed;
                }

                @Override
                public void shoot(Runnable fire) {
                    fire.run();
                }

                @Override
                public TankState onHealthChanged(int health, int maxHealth) {
                    return this;
                }
            };

            assertThrows(IllegalArgumentException.class, () -> tank.changeState(invalid));
            assertSame(previous, tank.getState());
        }
    }

    @Test
    public void customStateControlsShotFrequencyWithSuppliedGameTime() {
        GameField field = new GameField(4, 3);
        Tank tank = registeredTank(field, 100);
        double[] gameTime = {0d};
        DoubleSupplier clock = () -> gameTime[0];
        TankState timed = new TankState() {
            private double nextShotAt = Double.NEGATIVE_INFINITY;

            @Override
            public double getSpeedMultiplier() {
                return 2d;
            }

            @Override
            public void shoot(Runnable fire) {
                double now = clock.getAsDouble();
                if (now >= nextShotAt) {
                    fire.run();
                    nextShotAt = now + 0.5d;
                }
            }

            @Override
            public TankState onHealthChanged(int health, int maxHealth) {
                return this;
            }
        };
        tank.changeState(timed);

        tank.shoot();
        tank.shoot();
        assertEquals(1, bulletCount(field));
        gameTime[0] = 0.49d;
        tank.shoot();
        assertEquals(1, bulletCount(field));
        gameTime[0] = 0.5d;
        tank.shoot();
        tank.shoot();
        assertEquals(2, bulletCount(field));
        gameTime[0] = 1d;
        tank.shoot();
        assertEquals(3, bulletCount(field));
    }

    private Tank tank(int health) {
        return new Tank(tile -> true, 0, 0, MOVEMENT_DURATION, health, BulletSettings.DEFAULT);
    }

    private Tank registeredTank(GameField field, int health) {
        Tank tank = new Tank(field, 0, 0, MOVEMENT_DURATION, health, BulletSettings.DEFAULT);
        field.addObject(tank);
        return tank;
    }

    private int bulletCount(GameField field) {
        return (int) field.getObjects().stream().filter(object -> object instanceof Bullet).count();
    }
}
