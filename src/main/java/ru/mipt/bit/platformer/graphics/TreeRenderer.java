package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.Tree;

public final class TreeRenderer implements Disposable {
    private static final String TEXTURE_PATH = "images/greenTree.png";
    private static final float NO_ROTATION_DEGREES = 0f;
    private static final float STATIONARY_PROGRESS = 1f;
    private final SpriteRenderer sprite;

    public TreeRenderer(TiledMapTileLayer tileLayer) {
        sprite = new SpriteRenderer(TEXTURE_PATH, tileLayer);
    }

    public void render(Batch batch, Tree tree) {
        GridPoint2 position = tree.getTilePosition();
        sprite.render(batch, position, position, STATIONARY_PROGRESS, NO_ROTATION_DEGREES);
    }

    @Override
    public void dispose() {
        sprite.dispose();
    }
}
