package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

@FunctionalInterface
public interface MovementRules {
    boolean isTileUnoccupied(GridPoint2 destinationTile);
}
