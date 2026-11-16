package ru.mipt.bit.platformer.model.state;

public interface TankState {
    double getSpeedMultiplier();

    void shoot(Runnable fire);

    TankState onHealthChanged(int health, int maxHealth);
}
