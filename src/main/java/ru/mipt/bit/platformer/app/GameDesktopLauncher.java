package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.level.LevelLayout;
import ru.mipt.bit.platformer.level.TankGroup;
import ru.mipt.bit.platformer.level.TankSpawner;

import java.io.IOException;
import java.util.Random;

public final class GameDesktopLauncher {
    private static final float MOVEMENT_DURATION_SECONDS = 0.4f;
    private static final int WINDOW_WIDTH_PIXELS = 960;
    private static final int WINDOW_HEIGHT_PIXELS = 640;

    private GameDesktopLauncher() {
    }

    public static void main(String[] args) {
        LevelLayout layout;
        TankGroup tanks;
        try {
            GameOptions options = GameOptions.fromArguments(args);
            layout = options.getLevelSource().load();
            tanks = new TankSpawner(new Random()).spawn(layout, options.getBotCount(), MOVEMENT_DURATION_SECONDS);
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Cannot create game: " + exception.getMessage());
            System.exit(1);
            return;
        }
        Lwjgl3ApplicationConfiguration windowConfiguration = new Lwjgl3ApplicationConfiguration();
        windowConfiguration.setWindowedMode(WINDOW_WIDTH_PIXELS, WINDOW_HEIGHT_PIXELS);
        new Lwjgl3Application(new TankGame(() -> DefaultGameFactory.create(layout, tanks)), windowConfiguration);
    }
}
