package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.game.Updatable;

import java.util.Objects;

public final class Tank extends GameObject implements Updatable {
    private static final float MOVEMENT_START_PROGRESS = 0f;
    private static final float MOVEMENT_COMPLETE_PROGRESS = 1f;

    private final MovementRules movementRules;
    private final float movementDurationSeconds;
    private final GridPoint2 destinationTile;
    private Direction direction = Direction.RIGHT;
    private float movementProgress = MOVEMENT_COMPLETE_PROGRESS;

    public Tank(MovementRules movementRules, int tileX, int tileY, float movementDurationSeconds) {
        super(tileX, tileY);
        this.movementRules = Objects.requireNonNull(movementRules);
        if (!Float.isFinite(movementDurationSeconds) || movementDurationSeconds <= 0f) {
            throw new IllegalArgumentException("Movement duration must be finite and positive");
        }
        this.movementDurationSeconds = movementDurationSeconds;
        destinationTile = getTilePosition();
    }

    public void tryStartMoving(Direction requestedDirection) {
        Objects.requireNonNull(requestedDirection);
        if (isMoving()) {
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
        movementProgress = Math.min(MOVEMENT_COMPLETE_PROGRESS,
                movementProgress + elapsedSeconds / movementDurationSeconds);
        if (!isMoving()) {
            moveToTile(destinationTile);
        }
    }

    public boolean isMoving() {
        return movementProgress < MOVEMENT_COMPLETE_PROGRESS;
    }

    public GridPoint2 getDestinationTile() {
        return new GridPoint2(destinationTile);
    }

    public Direction getDirection() {
        return direction;
    }

    public float getMovementProgress() {
        return movementProgress;
    }
}
