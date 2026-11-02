package ru.mipt.bit.platformer.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class BulletSettingsTest {
    @Test
    public void storesDamageAndTileDuration() {
        BulletSettings settings = new BulletSettings(10, 0.3f);
        assertEquals(10, settings.getDamage());
        assertEquals(0.3f, settings.getTileDurationSeconds(), 0f);
        assertEquals(25, BulletSettings.DEFAULT.getDamage());
        assertEquals(0.1f, BulletSettings.DEFAULT.getTileDurationSeconds(), 0f);
    }

    @Test
    public void rejectsNonPositiveDamage() {
        assertThrows(IllegalArgumentException.class, () -> new BulletSettings(0, 0.1f));
        assertThrows(IllegalArgumentException.class, () -> new BulletSettings(-1, 0.1f));
    }

    @Test
    public void rejectsInvalidTileDuration() {
        for (float duration : new float[]{0f, -1f, Float.NaN, Float.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new BulletSettings(25, duration));
        }
    }
}
