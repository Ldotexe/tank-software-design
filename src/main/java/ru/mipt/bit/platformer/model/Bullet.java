package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.game.Updatable;

import java.util.Objects;

public final class Bullet extends GameObject implements Updatable {
    private final Tank owner;
    private final Direction direction;
    private final BulletSettings settings;
    private final GridPoint2 destinationTile;
    private double elapsedInTile;

    public Bullet(Tank owner, GridPoint2 start, Direction direction, BulletSettings settings) {
        super(Objects.requireNonNull(start).x, start.y);
        this.owner = Objects.requireNonNull(owner);
        this.direction = Objects.requireNonNull(direction);
        this.settings = Objects.requireNonNull(settings);
        destinationTile = direction.calculateAdjacentTile(start);
    }

    public Tank getOwner() {
        return owner;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getDamage() {
        return settings.getDamage();
    }

    public GridPoint2 getDestinationTile() {
        return new GridPoint2(destinationTile);
    }

    public float getMovementProgress() {
        return (float) (elapsedInTile / settings.getTileDurationSeconds());
    }

    double timeUntilNextTile() {
        return settings.getTileDurationSeconds() - elapsedInTile;
    }

    @Override
    public void update(float elapsedSeconds) {
        if (!Float.isFinite(elapsedSeconds) || elapsedSeconds < 0f) {
            throw new IllegalArgumentException("Elapsed time must be finite and non-negative");
        }
        double remaining = elapsedSeconds;
        while (remaining > 0) {
            double step = Math.min(remaining, timeUntilNextTile());
            update(step);
            remaining -= step;
        }
    }

    void update(double elapsedSeconds) {
        boolean reachesNextTile = elapsedSeconds >= timeUntilNextTile();
        elapsedInTile += elapsedSeconds;
        if (reachesNextTile || elapsedInTile >= settings.getTileDurationSeconds()) {
            moveToTile(destinationTile);
            destinationTile.set(direction.calculateAdjacentTile(destinationTile));
            elapsedInTile = 0;
        }
    }
}
