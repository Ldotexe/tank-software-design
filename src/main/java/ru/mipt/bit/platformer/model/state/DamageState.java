package ru.mipt.bit.platformer.model.state;

abstract class DamageState implements TankState {
    private static final int PERCENT_SCALE = 100;
    private static final int LIGHT_DAMAGE_THRESHOLD_PERCENT = 70;
    private static final int HEAVY_DAMAGE_THRESHOLD_PERCENT = 15;

    @Override
    public void shoot(Runnable fire) {
        fire.run();
    }

    @Override
    public final TankState onHealthChanged(int health, int maxHealth) {
        long scaledHealth = (long) health * PERCENT_SCALE;
        if (maxHealth == 0 || scaledHealth < (long) maxHealth * HEAVY_DAMAGE_THRESHOLD_PERCENT) {
            return HeavyDamageState.INSTANCE;
        }
        if (scaledHealth <= (long) maxHealth * LIGHT_DAMAGE_THRESHOLD_PERCENT) {
            return MediumDamageState.INSTANCE;
        }
        return LightDamageState.INSTANCE;
    }
}
