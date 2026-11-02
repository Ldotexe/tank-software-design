package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

public class LevelViewportTest {
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 768f;
    private GL20 previousGl;
    private Graphics previousGraphics;

    @Before
    public void setUp() {
        GdxNativesLoader.load();
        previousGl = Gdx.gl;
        previousGraphics = Gdx.graphics;
        Gdx.gl = (GL20) Proxy.newProxyInstance(GL20.class.getClassLoader(), new Class<?>[]{GL20.class},
                (proxy, method, args) -> null);
    }

    @After
    public void tearDown() {
        Gdx.gl = previousGl;
        Gdx.graphics = previousGraphics;
    }

    @Test
    public void fitsWholeLevelIntoSmallWindowWithoutStretching() {
        FitViewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        resize(viewport, 640, 360);
        assertCorners(viewport, 20f, 0f, 620f, 360f);
    }

    @Test
    public void keepsWholeLevelVisibleAfterPortraitResize() {
        FitViewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        resize(viewport, 640, 360);
        resize(viewport, 200, 400);
        assertCorners(viewport, 0f, 140f, 200f, 260f);
    }

    private void resize(FitViewport viewport, int width, int height) {
        Gdx.graphics = (Graphics) Proxy.newProxyInstance(Graphics.class.getClassLoader(),
                new Class<?>[]{Graphics.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getWidth":
                        case "getBackBufferWidth":
                            return width;
                        case "getHeight":
                        case "getBackBufferHeight":
                            return height;
                        default:
                            return null;
                    }
                });
        viewport.update(width, height, true);
    }

    private void assertCorners(FitViewport viewport, float left, float bottom, float right, float top) {
        Vector2 bottomLeft = viewport.project(new Vector2(0, 0));
        Vector2 topRight = viewport.project(new Vector2(WORLD_WIDTH, WORLD_HEIGHT));
        assertEquals(left, bottomLeft.x, 0.001f);
        assertEquals(bottom, bottomLeft.y, 0.001f);
        assertEquals(right, topRight.x, 0.001f);
        assertEquals(top, topRight.y, 0.001f);
        assertEquals((right - left) / WORLD_WIDTH, (top - bottom) / WORLD_HEIGHT, 0.001f);
    }
}
