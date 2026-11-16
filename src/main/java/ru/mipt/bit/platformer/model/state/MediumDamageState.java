package ru.mipt.bit.platformer.model.state;

public final class MediumDamageState extends DamageState {
    public static final MediumDamageState INSTANCE = new MediumDamageState();
    private static final double SPEED_MULTIPLIER = 0.5d;

    private MediumDamageState() {
    }

    @Override
    public double getSpeedMultiplier() {
        return SPEED_MULTIPLIER;
    }
}
