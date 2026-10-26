package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.mipt.bit.platformer.control.KeyboardControlStrategy;
import ru.mipt.bit.platformer.control.RandomControlStrategy;
import ru.mipt.bit.platformer.control.TankController;
import ru.mipt.bit.platformer.game.GameLoop;
import ru.mipt.bit.platformer.game.InputHandler;
import ru.mipt.bit.platformer.graphics.FieldRenderer;
import ru.mipt.bit.platformer.graphics.GameRenderer;
import ru.mipt.bit.platformer.graphics.ObjectLayer;
import ru.mipt.bit.platformer.graphics.RenderLayer;
import ru.mipt.bit.platformer.graphics.TankRenderer;
import ru.mipt.bit.platformer.graphics.TreeRenderer;
import ru.mipt.bit.platformer.level.LevelLayout;
import ru.mipt.bit.platformer.level.TankGroup;
import ru.mipt.bit.platformer.model.GameSession;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class DefaultGameFactory {
    private static final String MAP_PATH = "level.tmx";
    private static final String TANK_TEXTURE_PATH = "images/tank_blue.png";
    private static final String TREE_TEXTURE_PATH = "images/greenTree.png";
    private static final Color BACKGROUND_COLOR = new Color(0f, 0f, 0.2f, 1f);

    private DefaultGameFactory() {
    }

    public static GameLoop create(LevelLayout layout, TankGroup tanks) {
        GameSession session = new GameSession(tanks.getTanks());
        List<TankController> controllers = new ArrayList<>();
        controllers.add(new TankController(tanks.getPlayer(),
                new KeyboardControlStrategy(Gdx.input::isKeyPressed)));
        for (Tank bot : tanks.getBots()) {
            controllers.add(new TankController(bot, new RandomControlStrategy(new Random())));
        }
        InputHandler input = () -> controllers.forEach(TankController::handleInput);

        Batch batch = new SpriteBatch();
        FieldRenderer fieldRenderer = new FieldRenderer(MAP_PATH, batch, layout.getWidth(), layout.getHeight());
        TiledMapTileLayer tileLayer = fieldRenderer.getTileLayer();
        List<RenderLayer> layers = Arrays.asList(
                fieldRenderer,
                new ObjectLayer<>(Collections.singletonList(tanks.getPlayer()),
                        new TankRenderer(tileLayer, TANK_TEXTURE_PATH, Interpolation.smooth)),
                new ObjectLayer<>(tanks.getBots(),
                        new TankRenderer(tileLayer, TANK_TEXTURE_PATH, Interpolation.smooth, Color.GRAY)),
                new ObjectLayer<>(layout.getTrees(),
                        new TreeRenderer(tileLayer, TREE_TEXTURE_PATH, Interpolation.smooth))
        );
        Viewport viewport = new FitViewport(tileLayer.getWidth() * tileLayer.getTileWidth(),
                tileLayer.getHeight() * tileLayer.getTileHeight());
        GameRenderer renderer = new GameRenderer(batch, layers, BACKGROUND_COLOR, viewport);
        renderer.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return new GameLoop(input, session, renderer);
    }
}
