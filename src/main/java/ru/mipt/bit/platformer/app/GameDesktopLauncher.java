package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.mipt.bit.platformer.level.LevelLayout;

import java.io.IOException;

public final class GameDesktopLauncher {
    private static final int WINDOW_WIDTH_PIXELS = 960;
    private static final int WINDOW_HEIGHT_PIXELS = 640;

    private GameDesktopLauncher() {
    }

    public static void main(String[] args) {
        LevelLayout layout;
        try {
            layout = LevelSourceSelector.fromArguments(args).load();
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Cannot load level: " + exception.getMessage());
            System.exit(1);
            return;
        }
        Lwjgl3ApplicationConfiguration windowConfiguration = new Lwjgl3ApplicationConfiguration();
        windowConfiguration.setWindowedMode(WINDOW_WIDTH_PIXELS, WINDOW_HEIGHT_PIXELS);
        new Lwjgl3Application(new TankGame(() -> DefaultGameFactory.create(layout)), windowConfiguration);
    }
}
