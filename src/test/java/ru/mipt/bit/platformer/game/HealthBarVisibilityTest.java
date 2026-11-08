package ru.mipt.bit.platformer.game;

import org.junit.Test;

import static org.junit.Assert.*;

public class HealthBarVisibilityTest {
    @Test
    public void startsVisibleAndTogglesInBothDirections() {
        HealthBarVisibility healthBars = new HealthBarVisibility();

        assertTrue(healthBars.isVisible());
        healthBars.toggle();
        assertFalse(healthBars.isVisible());
        healthBars.toggle();
        assertTrue(healthBars.isVisible());
    }
}
