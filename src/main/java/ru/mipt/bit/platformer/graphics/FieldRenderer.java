package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import static ru.mipt.bit.platformer.graphics.util.GdxGameUtils.*;

public final class FieldRenderer implements RenderLayer {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    public FieldRenderer(String mapPath, Batch batch) {
        map = new TmxMapLoader().load(mapPath);
        mapRenderer = createFullMapRenderer(map, batch);
    }

    public TiledMapTileLayer getTileLayer() {
        return getOnlyTileLayer(map);
    }

    @Override
    public void render(Batch batch) {
        mapRenderer.render();
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
    }
}
