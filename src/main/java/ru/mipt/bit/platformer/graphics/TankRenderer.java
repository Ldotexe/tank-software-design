package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.model.Tank;

public final class TankRenderer implements ObjectRenderer<Tank> {
    private final SpriteRenderer sprite;
    private final Color tint;

    public TankRenderer(TiledMapTileLayer tileLayer, String texturePath, Interpolation interpolation) {
        this(tileLayer, texturePath, interpolation, Color.WHITE);
    }

    public TankRenderer(TiledMapTileLayer tileLayer, String texturePath, Interpolation interpolation, Color tint) {
        sprite = new SpriteRenderer(texturePath, tileLayer, interpolation);
        this.tint = new Color(tint);
    }

    @Override
    public void render(Batch batch, Tank tank) {
        float previousColor = batch.getPackedColor();
        batch.setColor(tint);
        try {
            sprite.render(batch, tank.getTilePosition(), tank.getDestinationTile(),
                    tank.getMovementProgress(), tank.getDirection().getRotationDegrees());
        } finally {
            batch.setPackedColor(previousColor);
        }
    }

    @Override
    public void dispose() {
        sprite.dispose();
    }
}
