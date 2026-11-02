package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.mipt.bit.platformer.game.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public final class GameRenderer implements GameView {
    private final Batch batch;
    private final List<RenderLayer> layers;
    private final Color backgroundColor;
    private final Viewport viewport;

    public GameRenderer(Batch batch, List<? extends RenderLayer> layers, Color backgroundColor, Viewport viewport) {
        this.batch = Objects.requireNonNull(batch);
        this.layers = new ArrayList<>(layers);
        this.layers.forEach(Objects::requireNonNull);
        this.backgroundColor = new Color(backgroundColor);
        this.viewport = Objects.requireNonNull(viewport);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        for (RenderLayer layer : layers) {
            layer.render(batch);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width > 0 && height > 0) {
            viewport.update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        for (RenderLayer layer : layers) {
            layer.dispose();
        }
        batch.dispose();
    }
}
