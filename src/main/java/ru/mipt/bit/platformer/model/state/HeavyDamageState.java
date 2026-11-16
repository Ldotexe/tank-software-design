package ru.mipt.bit.platformer.model.state;

public final class HeavyDamageState extends DamageState {
    public static final HeavyDamageState INSTANCE = new HeavyDamageState();
    private static final double SPEED_MULTIPLIER = 1d / 3d;

    private HeavyDamageState() {
    }

    @Override
    public double getSpeedMultiplier() {
        return SPEED_MULTIPLIER;
    }

    @Override
    public void shoot(Runnable fire) {
    }
}
