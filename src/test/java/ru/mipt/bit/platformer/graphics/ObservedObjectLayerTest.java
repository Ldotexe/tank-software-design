package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.junit.Test;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ObservedObjectLayerTest {
    @Test
    public void drawsExistingObjectsReceivedThroughInitialReplay() {
        GameField field = new GameField(5, 4);
        Tree first = new Tree(1, 2);
        Tree second = new Tree(3, 2);
        field.addObject(first);
        field.addObject(second);
        field.addObject(new Tank(field, 0, 0, 0.4f));
        RecordingRenderer<Tree> renderer = new RecordingRenderer<>();
        ObservedObjectLayer<Tree> layer = new ObservedObjectLayer<>(field, Tree.class, tree -> true, renderer);

        layer.render(batch(new ArrayList<>()));

        assertEquals(Arrays.asList(first, second), renderer.rendered);
    }

    @Test
    public void tracksLaterAdditionsAndRemovalsWithoutStaleDraws() {
        GameField field = new GameField(5, 4);
        RecordingRenderer<Tree> renderer = new RecordingRenderer<>();
        ObservedObjectLayer<Tree> layer = new ObservedObjectLayer<>(field, Tree.class, tree -> true, renderer);
        Tree first = new Tree(1, 2);
        Tree second = new Tree(3, 2);
        field.addObject(first);
        layer.render(batch(new ArrayList<>()));
        assertEquals(Arrays.asList(first), renderer.rendered);

        renderer.rendered.clear();
        field.removeObject(first);
        field.addObject(second);
        layer.render(batch(new ArrayList<>()));

        assertEquals(Arrays.asList(second), renderer.rendered);
    }

    @Test
    public void appliesTypeAndPredicateToInitialAndNewObjects() {
        GameField field = new GameField(5, 4);
        Tank player = new Tank(field, 0, 0, 0.4f);
        Tank bot = new Tank(field, 1, 0, 0.4f);
        field.addObject(player);
        field.addObject(new Tree(2, 2));
        RecordingRenderer<Tank> renderer = new RecordingRenderer<>();
        ObservedObjectLayer<Tank> layer = new ObservedObjectLayer<>(field, Tank.class, tank -> tank != player, renderer);
        field.addObject(bot);
        field.addObject(new Tree(4, 3));

        layer.render(batch(new ArrayList<>()));

        assertEquals(Arrays.asList(bot), renderer.rendered);
    }

    @Test
    public void objectRemovalDoesNotDisposeSharedRenderer() {
        GameField field = new GameField(5, 4);
        Tree first = new Tree(1, 2);
        Tree second = new Tree(3, 2);
        field.addObject(first);
        field.addObject(second);
        RecordingRenderer<Tree> renderer = new RecordingRenderer<>();
        ObservedObjectLayer<Tree> layer = new ObservedObjectLayer<>(field, Tree.class, tree -> true, renderer);

        field.removeObject(first);
        assertEquals(0, renderer.disposeCount);
        layer.render(batch(new ArrayList<>()));
        assertEquals(Arrays.asList(second), renderer.rendered);
        field.removeObject(second);
        assertEquals(0, renderer.disposeCount);
        layer.dispose();
        layer.dispose();
        assertEquals(1, renderer.disposeCount);
    }

    @Test
    public void disposedLayerStopsRenderingAndAcceptingNewObjects() {
        GameField field = new GameField(5, 4);
        RecordingRenderer<Tree> renderer = new RecordingRenderer<>();
        ObservedObjectLayer<Tree> layer = new ObservedObjectLayer<>(field, Tree.class, tree -> true, renderer);
        field.addObject(new Tree(1, 2));
        layer.dispose();
        field.addObject(new Tree(3, 2));
        List<String> batchCalls = new ArrayList<>();

        layer.render(batch(batchCalls));

        assertTrue(renderer.rendered.isEmpty());
        assertTrue(batchCalls.isEmpty());
        assertEquals(1, renderer.disposeCount);
    }

    @Test
    public void repeatedAddNotificationDoesNotDuplicateDrawing() {
        GameField field = new GameField(5, 4);
        Tree tree = new Tree(1, 2);
        field.addObject(tree);
        RecordingRenderer<Tree> renderer = new RecordingRenderer<>();
        ObservedObjectLayer<Tree> layer = new ObservedObjectLayer<>(field, Tree.class, object -> true, renderer);
        layer.objectAdded(tree);
        layer.objectAdded(tree);

        layer.render(batch(new ArrayList<>()));

        assertEquals(Arrays.asList(tree), renderer.rendered);
    }

    @Test
    public void endsBatchWhenRenderingFails() {
        GameField field = new GameField(5, 4);
        field.addObject(new Tree(1, 2));
        ObjectRenderer<Tree> renderer = new ObjectRenderer<Tree>() {
            @Override
            public void render(Batch batch, Tree object) {
                throw new IllegalStateException("Cannot draw object");
            }

            @Override
            public void dispose() {
            }
        };
        ObservedObjectLayer<Tree> layer = new ObservedObjectLayer<>(field, Tree.class, tree -> true, renderer);
        List<String> calls = new ArrayList<>();

        assertThrows(IllegalStateException.class, () -> layer.render(batch(calls)));

        assertEquals(Arrays.asList("begin", "end"), calls);
    }

    @Test
    public void rejectsMissingDependencies() {
        GameField field = new GameField(5, 4);
        RecordingRenderer<Tree> renderer = new RecordingRenderer<>();
        assertThrows(NullPointerException.class,
                () -> new ObservedObjectLayer<>(null, Tree.class, tree -> true, renderer));
        assertThrows(NullPointerException.class,
                () -> new ObservedObjectLayer<>(field, null, tree -> true, renderer));
        assertThrows(NullPointerException.class,
                () -> new ObservedObjectLayer<>(field, Tree.class, null, renderer));
        assertThrows(NullPointerException.class,
                () -> new ObservedObjectLayer<>(field, Tree.class, tree -> true, null));
    }

    private Batch batch(List<String> calls) {
        return (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class<?>[]{Batch.class},
                (proxy, method, args) -> {
                    calls.add(method.getName());
                    return null;
                });
    }

    private static final class RecordingRenderer<T> implements ObjectRenderer<T> {
        private final List<T> rendered = new ArrayList<>();
        private int disposeCount;

        @Override
        public void render(Batch batch, T object) {
            rendered.add(object);
        }

        @Override
        public void dispose() {
            disposeCount++;
        }
    }
}
