package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public abstract class GameObject {
    private final GridPoint2 tilePosition;
    private EntityWorld world;

    protected GameObject(int tileX, int tileY) {
        tilePosition = new GridPoint2(tileX, tileY);
    }

    public GridPoint2 getTilePosition() {
        return new GridPoint2(tilePosition);
    }

    protected void moveToTile(GridPoint2 destination) {
        tilePosition.set(destination);
    }

    protected EntityWorld getWorld() {
        return world;
    }

    protected boolean isActive() {
        return world == null || world.contains(this);
    }

    void attachTo(EntityWorld world) {
        if (this.world != null && this.world != world && this.world.contains(this)) {
            throw new IllegalArgumentException("Object already belongs to another level");
        }
        this.world = world;
    }
}
