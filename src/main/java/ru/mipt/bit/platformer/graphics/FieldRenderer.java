package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.GameField;
import ru.mipt.bit.platformer.Tree;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public final class FieldRenderer implements Disposable {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final TreeRenderer treeRenderer;

    public FieldRenderer(String mapPath, Batch batch) {
        map = new TmxMapLoader().load(mapPath);
        mapRenderer = createFullMapRenderer(map, batch);
        treeRenderer = new TreeRenderer(getTileLayer());
    }

    public TiledMapTileLayer getTileLayer() {
        return getOnlyTileLayer(map);
    }

    public void renderGround() {
        mapRenderer.render();
    }

    public void renderObstacles(Batch batch, GameField field) {
        for (Tree tree : field.getTrees()) {
            treeRenderer.render(batch, tree);
        }
    }

    @Override
    public void dispose() {
        treeRenderer.dispose();
        // The map renderer borrows the batch and does not dispose it.
        mapRenderer.dispose();
        map.dispose();
    }
}
