package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import org.junit.Test;
import ru.mipt.bit.platformer.game.HealthBarVisibility;
import ru.mipt.bit.platformer.model.Tank;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class HealthBarRendererTest {
    @Test
    public void hiddenBarsStillRenderTheTankWithoutChangingBatchState() {
        RecordingRenderer base = new RecordingRenderer();
        HealthBarVisibility visibility = new HealthBarVisibility();
        visibility.toggle();
        HealthBarRenderer renderer = renderer(base, visibility);
        List<String> batchCalls = new ArrayList<>();
        Batch batch = batch(batchCalls);
        Tank tank = new Tank(tile -> true, 0, 0, 0.4f);

        renderer.render(batch, tank);

        assertSame(tank, base.tank);
        assertSame(batch, base.batch);
        assertEquals(1, base.renderCount);
        assertTrue(batchCalls.isEmpty());
    }

    @Test
    public void callsTheBaseRendererBeforeAddingTheVisibleBar() {
        ObjectRenderer<Tank> base = new ObjectRenderer<Tank>() {
            @Override
            public void render(Batch batch, Tank object) {
                throw new IllegalStateException("Cannot draw tank");
            }

            @Override
            public void dispose() {
            }
        };
        HealthBarRenderer renderer = renderer(base, new HealthBarVisibility());
        List<String> batchCalls = new ArrayList<>();

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> renderer.render(batch(batchCalls), new Tank(tile -> true, 0, 0, 0.4f)));

        assertEquals("Cannot draw tank", error.getMessage());
        assertTrue(batchCalls.isEmpty());
        renderer.dispose();
    }

    @Test
    public void disposesTheBaseOnceAndStopsRendering() {
        RecordingRenderer base = new RecordingRenderer();
        HealthBarRenderer renderer = renderer(base, new HealthBarVisibility());

        renderer.dispose();
        renderer.dispose();
        renderer.render(batch(new ArrayList<>()), new Tank(tile -> true, 0, 0, 0.4f));

        assertEquals(1, base.disposeCount);
        assertEquals(0, base.renderCount);
    }

    @Test
    public void rejectsMissingDependencies() {
        RecordingRenderer base = new RecordingRenderer();
        TiledMapTileLayer tiles = new TiledMapTileLayer(5, 3, 128, 128);
        HealthBarVisibility visibility = new HealthBarVisibility();

        assertThrows(NullPointerException.class,
                () -> new HealthBarRenderer(null, tiles, Interpolation.smooth, visibility));
        assertThrows(NullPointerException.class,
                () -> new HealthBarRenderer(base, null, Interpolation.smooth, visibility));
        assertThrows(NullPointerException.class,
                () -> new HealthBarRenderer(base, tiles, null, visibility));
        assertThrows(NullPointerException.class,
                () -> new HealthBarRenderer(base, tiles, Interpolation.smooth, null));
    }

    private HealthBarRenderer renderer(ObjectRenderer<Tank> base, HealthBarVisibility visibility) {
        return new HealthBarRenderer(base, new TiledMapTileLayer(5, 3, 128, 128),
                Interpolation.smooth, visibility);
    }

    private Batch batch(List<String> calls) {
        return (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class<?>[]{Batch.class},
                (proxy, method, args) -> {
                    calls.add(method.getName());
                    return null;
                });
    }

    private static final class RecordingRenderer implements ObjectRenderer<Tank> {
        private Batch batch;
        private Tank tank;
        private int renderCount;
        private int disposeCount;

        @Override
        public void render(Batch batch, Tank tank) {
            this.batch = batch;
            this.tank = tank;
            renderCount++;
        }

        @Override
        public void dispose() {
            disposeCount++;
        }
    }
}
