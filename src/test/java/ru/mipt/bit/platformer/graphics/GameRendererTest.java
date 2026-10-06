package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GameRendererTest {
    @Test
    public void rendersInjectedLayersInOrderWithConfiguredBackground() {
        List<String> calls = new ArrayList<>();
        List<Float> clearColor = new ArrayList<>();
        GL20 previousGl = Gdx.gl;
        Gdx.gl = (GL20) Proxy.newProxyInstance(GL20.class.getClassLoader(), new Class<?>[]{GL20.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("glClearColor")) {
                        for (Object component : args) {
                            clearColor.add((Float) component);
                        }
                    } else if (method.getName().equals("glClear")) {
                        calls.add("clear");
                    }
                    return null;
                });
        try {
            Color background = new Color(0.1f, 0.2f, 0.3f, 1f);
            List<RenderLayer> layers = new ArrayList<>(Arrays.asList(
                    layer("field", calls), layer("new objects", calls)));
            GameRenderer renderer = new GameRenderer(batch(calls), layers, background);
            background.set(Color.BLACK);
            layers.clear();
            renderer.render();
            assertEquals(Arrays.asList("clear", "field", "new objects"), calls);
            assertEquals(Arrays.asList(0.1f, 0.2f, 0.3f, 1f), clearColor);
        } finally {
            Gdx.gl = previousGl;
        }
    }

    @Test
    public void disposesEachLayerThenSharedBatch() {
        List<String> calls = new ArrayList<>();
        GameRenderer renderer = new GameRenderer(batch(calls),
                Arrays.asList(layer("field", calls), layer("objects", calls)), Color.BLACK);
        renderer.dispose();
        assertEquals(Arrays.asList("dispose field", "dispose objects", "dispose batch"), calls);
    }

    private Batch batch(List<String> calls) {
        return (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class<?>[]{Batch.class},
                (proxy, method, args) -> {
                    calls.add(method.getName() + " batch");
                    return null;
                });
    }

    private RenderLayer layer(String name, List<String> calls) {
        return new RenderLayer() {
            @Override
            public void render(Batch batch) {
                calls.add(name);
            }

            @Override
            public void dispose() {
                calls.add("dispose " + name);
            }
        };
    }
}
