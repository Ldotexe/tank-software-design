package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ObjectLayerTest {
    @Test
    public void supportsNewObjectTypeAndRenderer() {
        List<String> calls = new ArrayList<>();
        Batch batch = batch(calls);
        ObjectLayer<String> layer = new ObjectLayer<>(Arrays.asList("wall", "stone"),
                renderer(calls, false));
        layer.render(batch);
        layer.dispose();
        assertEquals(Arrays.asList("begin", "wall", "stone", "end", "dispose"), calls);
    }

    @Test
    public void endsBatchWhenObjectRendererFails() {
        List<String> calls = new ArrayList<>();
        ObjectLayer<String> layer = new ObjectLayer<>(Collections.singletonList("wall"),
                renderer(calls, true));
        assertThrows(IllegalStateException.class, () -> layer.render(batch(calls)));
        assertEquals(Arrays.asList("begin", "wall", "end"), calls);
    }

    @Test
    public void rendersObjectsAddedToSuppliedCollection() {
        List<String> calls = new ArrayList<>();
        List<String> objects = new ArrayList<>();
        ObjectLayer<String> layer = new ObjectLayer<>(objects, renderer(calls, false));
        objects.add("wall");
        layer.render(batch(calls));
        assertEquals(Arrays.asList("begin", "wall", "end"), calls);
    }

    private Batch batch(List<String> calls) {
        return (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class<?>[]{Batch.class},
                (proxy, method, args) -> {
                    calls.add(method.getName());
                    return null;
                });
    }

    private ObjectRenderer<String> renderer(List<String> calls, boolean fail) {
        return new ObjectRenderer<String>() {
            @Override
            public void render(Batch batch, String object) {
                calls.add(object);
                if (fail) {
                    throw new IllegalStateException("Cannot draw object");
                }
            }

            @Override
            public void dispose() {
                calls.add("dispose");
            }
        };
    }
}
