package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.game.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public final class GameRenderer implements GameView {
    private final Batch batch;
    private final List<RenderLayer> layers;
    private final Color backgroundColor;

    public GameRenderer(Batch batch, List<? extends RenderLayer> layers, Color backgroundColor) {
        this.batch = Objects.requireNonNull(batch);
        this.layers = new ArrayList<>(layers);
        this.layers.forEach(Objects::requireNonNull);
        this.backgroundColor = new Color(backgroundColor);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        for (RenderLayer layer : layers) {
            layer.render(batch);
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
