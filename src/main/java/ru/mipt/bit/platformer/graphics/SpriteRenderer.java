package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

final class SpriteRenderer implements Disposable {
    private final Texture texture;
    private final TextureRegion sprite;
    private final Rectangle bounds;
    private final TileMovement tileMovement;

    SpriteRenderer(String texturePath, TiledMapTileLayer tileLayer) {
        texture = new Texture(texturePath);
        sprite = new TextureRegion(texture);
        bounds = createSpriteBounds(sprite);
        tileMovement = new TileMovement(tileLayer, Interpolation.smooth);
    }

    void render(Batch batch, GridPoint2 start, GridPoint2 destination,
                float progress, float rotationDegrees) {
        tileMovement.interpolateSpritePosition(bounds, start, destination, progress);
        drawSpriteAtOriginalSize(batch, sprite, bounds, rotationDegrees);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
