package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import static ru.mipt.bit.platformer.graphics.util.GdxGameUtils.*;
import static ru.mipt.bit.platformer.graphics.util.TileLayerResizer.resize;

public final class FieldRenderer implements RenderLayer {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    public FieldRenderer(String mapPath, Batch batch, int width, int height) {
        map = new TmxMapLoader().load(mapPath);
        TiledMapTileLayer source = getOnlyTileLayer(map);
        map.getLayers().remove(source);
        map.getLayers().add(resize(source, width, height));
        mapRenderer = createFullMapRenderer(map, batch);
    }

    public TiledMapTileLayer getTileLayer() {
        return getOnlyTileLayer(map);
    }

    @Override
    public void render(Batch batch) {
        TiledMapTileLayer layer = getTileLayer();
        mapRenderer.setView(batch.getProjectionMatrix(), 0f, 0f,
                layer.getWidth() * layer.getTileWidth(), layer.getHeight() * layer.getTileHeight());
        mapRenderer.render();
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
    }
}
