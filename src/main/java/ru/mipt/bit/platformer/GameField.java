package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.util.TileMovement;

import java.util.ArrayList;
import java.util.List;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class GameField implements Disposable {
    private final TiledMap tiledMap;
    private final MapRenderer mapRenderer;
    private final TiledMapTileLayer groundLayer;
    private final TileMovement tileMovement;
    private final List<Tree> trees = new ArrayList<>();

    public GameField(String mapPath, Batch batch) {
        tiledMap = new TmxMapLoader().load(mapPath);
        mapRenderer = createFullMapRenderer(tiledMap, batch);
        groundLayer = getOnlyTileLayer(tiledMap);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
    }

    public void addTree(int tileX, int tileY) {
        trees.add(new Tree(this, tileX, tileY));
    }

    public boolean isTileUnoccupied(GridPoint2 destinationTile) {
        for (Tree tree : trees) {
            if (tree.occupiesTile(destinationTile)) {
                return false;
            }
        }
        return true;
    }

    public void centerSpriteOnTile(Rectangle spriteBounds, GridPoint2 tilePosition) {
        GdxGameUtils.centerSpriteOnTile(groundLayer, spriteBounds, tilePosition);
    }

    public void interpolateSpritePosition(Rectangle spriteBounds, GridPoint2 startTile, GridPoint2 destinationTile, float movementProgress) {
        tileMovement.interpolateSpritePosition(spriteBounds, startTile, destinationTile, movementProgress);
    }

    public void renderGround() {
        mapRenderer.render();
    }

    public void renderObstacles(Batch batch) {
        for (Tree tree : trees) {
            tree.render(batch);
        }
    }

    @Override
    public void dispose() {
        for (Tree tree : trees) {
            tree.dispose();
        }
        tiledMap.dispose();
    }
}
