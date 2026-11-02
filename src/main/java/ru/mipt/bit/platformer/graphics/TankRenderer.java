package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.model.Tank;

public final class TankRenderer implements ObjectRenderer<Tank> {
    private final SpriteRenderer sprite;

    public TankRenderer(TiledMapTileLayer tileLayer, String texturePath, Interpolation interpolation) {
        sprite = new SpriteRenderer(texturePath, tileLayer, interpolation);
    }

    @Override
    public void render(Batch batch, Tank tank) {
        sprite.render(batch, tank.getTilePosition(), tank.getDestinationTile(),
                tank.getMovementProgress(), tank.getDirection().getRotationDegrees());
    }

    @Override
    public void dispose() {
        sprite.dispose();
    }
}
