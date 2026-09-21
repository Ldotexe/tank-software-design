package ru.mipt.bit.platformer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public final class GameDesktopLauncher {
    private static final int WINDOW_WIDTH_PIXELS = 1280;
    private static final int WINDOW_HEIGHT_PIXELS = 1024;

    private GameDesktopLauncher() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration windowConfiguration = new Lwjgl3ApplicationConfiguration();
        windowConfiguration.setWindowedMode(WINDOW_WIDTH_PIXELS, WINDOW_HEIGHT_PIXELS);
        new Lwjgl3Application(new TankGame(), windowConfiguration);
    }
}
