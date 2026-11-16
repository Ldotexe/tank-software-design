package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.game.HealthBarVisibility;
import ru.mipt.bit.platformer.model.Tank;

import java.util.Objects;

public final class HealthBarRenderer implements ObjectRenderer<Tank> {
    private static final int PIXEL_SIZE = 1;
    private static final float BORDER_FRACTION = 0.2f;
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 1f);
    private static final Color HEALTH_COLOR = new Color(0.2f, 0.9f, 0.2f, 1f);

    private final ObjectRenderer<Tank> renderer;
    private final HealthBarVisibility visibility;
    private final HealthBarLayout layout;
    private Texture pixel;
    private boolean disposed;

    public HealthBarRenderer(ObjectRenderer<Tank> renderer, TiledMapTileLayer tileLayer,
                             Interpolation interpolation, HealthBarVisibility visibility) {
        this.renderer = Objects.requireNonNull(renderer);
        this.visibility = Objects.requireNonNull(visibility);
        layout = new HealthBarLayout(tileLayer, interpolation);
    }

    @Override
    public void render(Batch batch, Tank tank) {
        if (disposed) {
            return;
        }
        renderer.render(batch, tank);
        if (!visibility.isVisible()) {
            return;
        }
        if (pixel == null) {
            pixel = createPixel();
        }

        Rectangle bounds = layout.boundsFor(tank);
        float border = bounds.height * BORDER_FRACTION;
        float filledWidth = (bounds.width - border * 2f) * layout.healthFraction(tank);
        float previousColor = batch.getPackedColor();
        try {
            batch.setColor(BACKGROUND_COLOR);
            batch.draw(pixel, bounds.x, bounds.y, bounds.width, bounds.height);
            if (filledWidth > 0f) {
                batch.setColor(HEALTH_COLOR);
                batch.draw(pixel, bounds.x + border, bounds.y + border,
                        filledWidth, bounds.height - border * 2f);
            }
        } finally {
            batch.setPackedColor(previousColor);
        }
    }

    private Texture createPixel() {
        Pixmap image = new Pixmap(PIXEL_SIZE, PIXEL_SIZE, Pixmap.Format.RGBA8888);
        try {
            image.setColor(Color.WHITE);
            image.fill();
            return new Texture(image);
        } finally {
            image.dispose();
        }
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        try {
            renderer.dispose();
        } finally {
            if (pixel != null) {
                pixel.dispose();
            }
        }
    }
}
