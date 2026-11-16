package ru.mipt.bit.platformer.model;

public final class BulletSettings {
    public static final BulletSettings DEFAULT = new BulletSettings(25, 0.1f);

    private final int damage;
    private final float tileDurationSeconds;

    public BulletSettings(int damage, float tileDurationSeconds) {
        if (damage <= 0) {
            throw new IllegalArgumentException("Bullet damage must be positive");
        }
        if (!Float.isFinite(tileDurationSeconds) || tileDurationSeconds <= 0f) {
            throw new IllegalArgumentException("Bullet tile duration must be finite and positive");
        }
        this.damage = damage;
        this.tileDurationSeconds = tileDurationSeconds;
    }

    public int getDamage() {
        return damage;
    }

    public float getTileDurationSeconds() {
        return tileDurationSeconds;
    }
}
