package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.model.Bullet;

public final class BulletRenderer implements ObjectRenderer<Bullet> {
    private static final int TEXTURE_SIZE = 32;
    private static final int BULLET_RADIUS = 12;
    private final SpriteRenderer sprite;

    public BulletRenderer(TiledMapTileLayer tileLayer) {
        sprite = new SpriteRenderer(createTexture(), tileLayer, Interpolation.linear);
    }

    private Texture createTexture() {
        Pixmap image = new Pixmap(TEXTURE_SIZE, TEXTURE_SIZE, Pixmap.Format.RGBA8888);
        try {
            image.setColor(Color.ORANGE);
            image.fillCircle(TEXTURE_SIZE / 2, TEXTURE_SIZE / 2, BULLET_RADIUS);
            return new Texture(image);
        } finally {
            image.dispose();
        }
    }

    @Override
    public void render(Batch batch, Bullet bullet) {
        sprite.render(batch, bullet.getTilePosition(), bullet.getDestinationTile(),
                bullet.getMovementProgress(), bullet.getDirection().getRotationDegrees());
    }

    @Override
    public void dispose() {
        sprite.dispose();
    }
}
