package ru.mipt.bit.platformer.game;

public final class HealthBarVisibility {
    private boolean visible = true;

    public boolean isVisible() {
        return visible;
    }

    public void toggle() {
        visible = !visible;
    }
}
