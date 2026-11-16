package ru.mipt.bit.platformer.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class TankHealthTest {
    @Test
    public void startsWithFullDefaultHealth() {
        Tank tank = new Tank(tile -> true, 0, 0, 0.4f);

        assertEquals(100, tank.getHealth());
        assertEquals(100, tank.getMaxHealth());
    }

    @Test
    public void maximumHealthDoesNotChangeAfterDamageOrDeath() {
        GameField field = new GameField(3, 3);
        Tank tank = new Tank(field, 1, 1, 0.4f, 80, BulletSettings.DEFAULT);
        field.addObject(tank);

        tank.takeDamage(20);

        assertEquals(60, tank.getHealth());
        assertEquals(80, tank.getMaxHealth());
        tank.takeDamage(61);
        assertEquals(-1, tank.getHealth());
        assertEquals(80, tank.getMaxHealth());
        assertFalse(field.contains(tank));
    }

    @Test
    public void acceptsZeroInitialHealthAsItsMaximum() {
        Tank tank = new Tank(tile -> true, 0, 0, 0.4f, 0, BulletSettings.DEFAULT);

        assertEquals(0, tank.getHealth());
        assertEquals(0, tank.getMaxHealth());
        assertTrue(tank.isAlive());
        tank.takeDamage(1);
        assertEquals(0, tank.getMaxHealth());
        assertFalse(tank.isAlive());
    }
}
