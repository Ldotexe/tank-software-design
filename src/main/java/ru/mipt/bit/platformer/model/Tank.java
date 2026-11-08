package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.game.Updatable;

import java.util.Objects;

public final class Tank extends GameObject implements Obstacle, Updatable {
    public static final int DEFAULT_HEALTH = 100;

    private static final float MOVEMENT_START_PROGRESS = 0f;
    private static final float MOVEMENT_COMPLETE_PROGRESS = 1f;

    private final MovementRules movementRules;
    private final float movementDurationSeconds;
    private final GridPoint2 destinationTile;
    private final BulletSettings bulletSettings;
    private final int maxHealth;
    private int health;
    private Direction direction = Direction.RIGHT;
    private double movementProgress = MOVEMENT_COMPLETE_PROGRESS;

    public Tank(MovementRules movementRules, int tileX, int tileY, float movementDurationSeconds) {
        this(movementRules, tileX, tileY, movementDurationSeconds, DEFAULT_HEALTH, BulletSettings.DEFAULT);
    }

    public Tank(MovementRules movementRules, int tileX, int tileY, float movementDurationSeconds,
                int health, BulletSettings bulletSettings) {
        super(tileX, tileY);
        this.movementRules = Objects.requireNonNull(movementRules);
        if (!Float.isFinite(movementDurationSeconds) || movementDurationSeconds <= 0f) {
            throw new IllegalArgumentException("Movement duration must be finite and positive");
        }
        if (health < 0) {
            throw new IllegalArgumentException("Initial tank health must be non-negative");
        }
        this.movementDurationSeconds = movementDurationSeconds;
        this.maxHealth = health;
        this.health = health;
        this.bulletSettings = Objects.requireNonNull(bulletSettings);
        destinationTile = getTilePosition();
    }

    public void tryStartMoving(Direction requestedDirection) {
        Objects.requireNonNull(requestedDirection);
        if (!isAlive() || !isActive() || isMoving()) {
            return;
        }
        direction = requestedDirection;
        GridPoint2 adjacentTile = direction.calculateAdjacentTile(getTilePosition());
        if (movementRules.isTileUnoccupied(adjacentTile)) {
            destinationTile.set(adjacentTile);
            movementProgress = MOVEMENT_START_PROGRESS;
        }
    }

    @Override
    public void update(float elapsedSeconds) {
        if (!Float.isFinite(elapsedSeconds) || elapsedSeconds < 0f) {
            throw new IllegalArgumentException("Elapsed time must be finite and non-negative");
        }
        advance(elapsedSeconds);
    }

    void advance(double elapsedSeconds) {
        if (!isAlive() || !isActive() || !isMoving()) {
            return;
        }
        if (elapsedSeconds >= timeUntilArrival()) {
            movementProgress = MOVEMENT_COMPLETE_PROGRESS;
        } else {
            movementProgress = Math.min(MOVEMENT_COMPLETE_PROGRESS,
                    movementProgress + elapsedSeconds / movementDurationSeconds);
        }
        if (!isMoving()) {
            moveToTile(destinationTile);
        }
    }

    double timeUntilArrival() {
        return (MOVEMENT_COMPLETE_PROGRESS - movementProgress) * movementDurationSeconds;
    }

    public boolean isMoving() {
        return movementProgress < MOVEMENT_COMPLETE_PROGRESS;
    }

    public void shoot() {
        if (!isAlive() || !isActive()) {
            return;
        }
        EntityWorld world = getWorld();
        if (world == null) {
            throw new IllegalStateException("Tank must be added to a level before shooting");
        }
        GridPoint2 start = direction.calculateAdjacentTile(getTilePosition());
        world.addObject(new Bullet(this, start, direction, bulletSettings));
    }

    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must be non-negative");
        }
        if (!isAlive() || !isActive()) {
            return;
        }
        health -= damage;
        if (!isAlive() && getWorld() != null) {
            getWorld().removeObject(this);
        }
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public boolean isAlive() {
        return health >= 0;
    }

    @Override
    public boolean occupiesTile(GridPoint2 tileCoordinates) {
        return getTilePosition().equals(tileCoordinates) || destinationTile.equals(tileCoordinates);
    }

    public GridPoint2 getDestinationTile() {
        return new GridPoint2(destinationTile);
    }

    public Direction getDirection() {
        return direction;
    }

    public float getMovementProgress() {
        return (float) movementProgress;
    }
}
