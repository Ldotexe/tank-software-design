package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.LevelListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class ObservedObjectLayer<T extends GameObject> implements RenderLayer, LevelListener {
    private final GameField field;
    private final Class<T> type;
    private final Predicate<T> filter;
    private final ObjectLayer<T> layer;
    private final List<T> objects = new ArrayList<>();
    private boolean disposed;

    public ObservedObjectLayer(GameField field, Class<T> type, Predicate<T> filter, ObjectRenderer<T> renderer) {
        this.field = Objects.requireNonNull(field);
        this.type = Objects.requireNonNull(type);
        this.filter = Objects.requireNonNull(filter);
        layer = new ObjectLayer<>(objects, renderer);
        field.addListener(this);
    }

    @Override
    public void objectAdded(GameObject object) {
        if (!disposed && type.isInstance(object)) {
            T candidate = type.cast(object);
            if (filter.test(candidate) && !objects.contains(candidate)) {
                objects.add(candidate);
            }
        }
    }

    @Override
    public void objectRemoved(GameObject object) {
        objects.remove(object);
    }

    @Override
    public void render(Batch batch) {
        if (disposed) {
            return;
        }
        layer.render(batch);
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        field.removeListener(this);
        objects.clear();
        layer.dispose();
    }
}
