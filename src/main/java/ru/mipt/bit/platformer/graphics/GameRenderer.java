package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.GameSession;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public final class GameRenderer implements Disposable {
    private static final String LEVEL_MAP_PATH = "level.tmx";
    private static final Color BACKGROUND_COLOR = new Color(0f, 0f, 0.2f, 1f);

    private final GameSession session;
    private final Batch batch;
    private final FieldRenderer fieldRenderer;
    private final TankRenderer tankRenderer;

    public GameRenderer(GameSession session) {
        this.session = session;
        batch = new SpriteBatch();
        fieldRenderer = new FieldRenderer(LEVEL_MAP_PATH, batch);
        tankRenderer = new TankRenderer(fieldRenderer.getTileLayer());
    }

    public void render() {
        Gdx.gl.glClearColor(BACKGROUND_COLOR.r, BACKGROUND_COLOR.g, BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        fieldRenderer.renderGround();
        batch.begin();
        tankRenderer.render(batch, session.getPlayer());
        fieldRenderer.renderObstacles(batch, session.getField());
        batch.end();
    }

    @Override
    public void dispose() {
        tankRenderer.dispose();
        fieldRenderer.dispose();
        batch.dispose();
    }
}
