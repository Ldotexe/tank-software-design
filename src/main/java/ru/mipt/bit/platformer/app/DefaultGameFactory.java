package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import ru.mipt.bit.platformer.game.GameLoop;
import ru.mipt.bit.platformer.graphics.FieldRenderer;
import ru.mipt.bit.platformer.graphics.GameRenderer;
import ru.mipt.bit.platformer.graphics.ObjectLayer;
import ru.mipt.bit.platformer.graphics.RenderLayer;
import ru.mipt.bit.platformer.graphics.TankRenderer;
import ru.mipt.bit.platformer.graphics.TreeRenderer;
import ru.mipt.bit.platformer.input.KeyboardHandler;
import ru.mipt.bit.platformer.input.PlayerControls;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.GameSession;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DefaultGameFactory {
    private static final int PLAYER_START_TILE_X = 1;
    private static final int PLAYER_START_TILE_Y = 1;
    private static final int TREE_TILE_X = 1;
    private static final int TREE_TILE_Y = 3;
    private static final float MOVEMENT_DURATION_SECONDS = 0.4f;
    private static final String MAP_PATH = "level.tmx";
    private static final String TANK_TEXTURE_PATH = "images/tank_blue.png";
    private static final String TREE_TEXTURE_PATH = "images/greenTree.png";
    private static final Color BACKGROUND_COLOR = new Color(0f, 0f, 0.2f, 1f);

    private DefaultGameFactory() {
    }

    public static GameLoop create() {
        GameField field = new GameField();
        Tree tree = new Tree(TREE_TILE_X, TREE_TILE_Y);
        field.addObstacle(tree);
        Tank player = new Tank(field, PLAYER_START_TILE_X, PLAYER_START_TILE_Y, MOVEMENT_DURATION_SECONDS);
        GameSession session = new GameSession(Collections.singletonList(player));

        KeyboardHandler keyboard = new KeyboardHandler(Gdx.input::isKeyPressed, Gdx.input::isKeyJustPressed);
        PlayerControls.bindMovement(keyboard, player::tryStartMoving);

        Batch batch = new SpriteBatch();
        FieldRenderer fieldRenderer = new FieldRenderer(MAP_PATH, batch);
        List<RenderLayer> layers = Arrays.asList(
                fieldRenderer,
                new ObjectLayer<>(Collections.singletonList(player),
                        new TankRenderer(fieldRenderer.getTileLayer(), TANK_TEXTURE_PATH, Interpolation.smooth)),
                new ObjectLayer<>(Collections.singletonList(tree),
                        new TreeRenderer(fieldRenderer.getTileLayer(), TREE_TEXTURE_PATH, Interpolation.smooth))
        );
        GameRenderer renderer = new GameRenderer(batch, layers, BACKGROUND_COLOR);
        return new GameLoop(keyboard, session, renderer);
    }
}
