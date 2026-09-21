package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Objects;

public final class Tank extends GameObject {
    public static final float TILE_MOVEMENT_DURATION_SECONDS = 0.4f;
    private static final float MOVEMENT_START_PROGRESS = 0f;
    private static final float MOVEMENT_COMPLETE_PROGRESS = 1f;

    private final GameField gameField;
    private final GridPoint2 destinationTile;
    private Direction direction = Direction.RIGHT;
    private float movementProgress = MOVEMENT_COMPLETE_PROGRESS;

    public Tank(GameField gameField, int tileX, int tileY) {
        super(tileX, tileY);
        this.gameField = Objects.requireNonNull(gameField);
        destinationTile = getTilePosition();
    }

    public void tryStartMoving(Direction requestedDirection) {
        Objects.requireNonNull(requestedDirection);
        if (isMoving()) {
            return;
        }
        direction = requestedDirection;
        GridPoint2 adjacentTile = direction.calculateAdjacentTile(getTilePosition());
        if (gameField.isTileUnoccupied(adjacentTile)) {
            destinationTile.set(adjacentTile);
            movementProgress = MOVEMENT_START_PROGRESS;
        }
    }

    public void updateMovement(float elapsedSeconds) {
        if (!Float.isFinite(elapsedSeconds) || elapsedSeconds < 0f) {
            throw new IllegalArgumentException("Elapsed time must be finite and non-negative");
        }
        movementProgress = Math.min(MOVEMENT_COMPLETE_PROGRESS,
                movementProgress + elapsedSeconds / TILE_MOVEMENT_DURATION_SECONDS);
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
