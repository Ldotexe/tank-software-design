package ru.mipt.bit.platformer.model.state;

public final class LightDamageState extends DamageState {
    public static final LightDamageState INSTANCE = new LightDamageState();
    private static final double SPEED_MULTIPLIER = 1d;

    private LightDamageState() {
    }

    @Override
    public double getSpeedMultiplier() {
        return SPEED_MULTIPLIER;
    }
}
