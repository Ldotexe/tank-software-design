package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class Tank extends GameObject {
    private static final String TEXTURE_PATH = "images/tank_blue.png";
    private static final float TILE_MOVEMENT_DURATION_SECONDS = 0.4f;

    private final GameField gameField;
    private final GridPoint2 destinationTile;
    private Direction direction = Direction.RIGHT;
    private float movementProgress = MOVEMENT_COMPLETE_PROGRESS;

    public Tank(GameField gameField, int tileX, int tileY) {
        super(TEXTURE_PATH, tileX, tileY);
        this.gameField = gameField;
        destinationTile = new GridPoint2(tilePosition);
        gameField.centerSpriteOnTile(spriteBounds, tilePosition);
    }

    public void tryStartMoving(Direction direction) {
        if (!isEqual(movementProgress, MOVEMENT_COMPLETE_PROGRESS)) {
            return;
        }
        this.direction = direction;
        GridPoint2 adjacentTile = direction.calculateAdjacentTile(tilePosition);
        if (gameField.isTileUnoccupied(adjacentTile)) {
            destinationTile.set(adjacentTile);
            movementProgress = MOVEMENT_START_PROGRESS;
        }
    }

    public void updateMovement(float elapsedSeconds) {
        gameField.interpolateSpritePosition(spriteBounds, tilePosition, destinationTile, movementProgress);
        movementProgress = advanceMovementProgress(movementProgress, elapsedSeconds, TILE_MOVEMENT_DURATION_SECONDS);
        if (isEqual(movementProgress, MOVEMENT_COMPLETE_PROGRESS)) {
            tilePosition.set(destinationTile);
        }
    }

    @Override
    protected float getRotationDegrees() {
        return direction.getRotationDegrees();
    }
}
