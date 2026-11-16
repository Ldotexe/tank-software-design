package ru.mipt.bit.platformer.level;

import java.io.IOException;

/**
 * Выходной порт получения описания уровня.
 */
public interface LevelSource {
    LevelLayout load() throws IOException;
}
