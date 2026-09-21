package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.Tank;

public final class TankRenderer implements Disposable {
    private static final String TEXTURE_PATH = "images/tank_blue.png";
    private final SpriteRenderer sprite;

    public TankRenderer(TiledMapTileLayer tileLayer) {
        sprite = new SpriteRenderer(TEXTURE_PATH, tileLayer);
    }

    public void render(Batch batch, Tank tank) {
        sprite.render(batch, tank.getTilePosition(), tank.getDestinationTile(),
                tank.getMovementProgress(), tank.getDirection().getRotationDegrees());
    }

    @Override
    public void dispose() {
        sprite.dispose();
    }
}
