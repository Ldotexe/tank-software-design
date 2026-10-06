package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.Objects;

public final class ObjectLayer<T> implements RenderLayer {
    private final Iterable<? extends T> objects;
    private final ObjectRenderer<T> renderer;

    public ObjectLayer(Iterable<? extends T> objects, ObjectRenderer<T> renderer) {
        this.objects = Objects.requireNonNull(objects);
        this.renderer = Objects.requireNonNull(renderer);
    }

    @Override
    public void render(Batch batch) {
        batch.begin();
        try {
            for (T object : objects) {
                renderer.render(batch, object);
            }
        } finally {
            batch.end();
        }
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
