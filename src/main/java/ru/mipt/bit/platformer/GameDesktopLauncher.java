package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameDesktopLauncher implements ApplicationListener {

    private static final String LEVEL_MAP_PATH = "level.tmx";
    private static final int WINDOW_WIDTH_PIXELS = 1280;
    private static final int WINDOW_HEIGHT_PIXELS = 1024;
    private static final int PLAYER_START_TILE_X = 1;
    private static final int PLAYER_START_TILE_Y = 1;
    private static final int TREE_TILE_X = 1;
    private static final int TREE_TILE_Y = 3;
    private static final Color BACKGROUND_COLOR = new Color(0f, 0f, 0.2f, 1f);

    private Batch spriteBatch;
    private GameField gameField;
    private Tank playerTank;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        gameField = new GameField(LEVEL_MAP_PATH, spriteBatch);
        gameField.addTree(TREE_TILE_X, TREE_TILE_Y);
        playerTank = new Tank(gameField, PLAYER_START_TILE_X, PLAYER_START_TILE_Y);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        for (Direction direction : Direction.values()) {
            if (direction.isMovementKeyPressed(Gdx.input)) {
                playerTank.tryStartMoving(direction);
            }
        }
        playerTank.updateMovement(Gdx.graphics.getDeltaTime());

        gameField.renderGround();
        spriteBatch.begin();
        playerTank.render(spriteBatch);
        gameField.renderObstacles(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        // do not react to window resizing
    }

    @Override
    public void pause() {
        // game doesn't get paused
    }

    @Override
    public void resume() {
        // game doesn't get paused
    }

    @Override
    public void dispose() {
        // dispose of all the native resources (classes which implement com.badlogic.gdx.utils.Disposable)
        playerTank.dispose();
        gameField.dispose();
        spriteBatch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration windowConfiguration = new Lwjgl3ApplicationConfiguration();
        // level width: 10 tiles x 128px, height: 8 tiles x 128px
        windowConfiguration.setWindowedMode(WINDOW_WIDTH_PIXELS, WINDOW_HEIGHT_PIXELS);
        new Lwjgl3Application(new GameDesktopLauncher(), windowConfiguration);
    }
}
