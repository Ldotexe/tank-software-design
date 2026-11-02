package ru.mipt.bit.platformer.app;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.mipt.bit.platformer.level.LevelLayout;
import ru.mipt.bit.platformer.level.LevelSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class LevelSourceSelectorTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void usesRandomDefaultsWithNoArgumentsOrRandomFlag() throws IOException {
        for (String[] args : new String[][]{new String[0], {"--random"}}) {
            LevelLayout level = LevelSourceSelector.fromArguments(args).load();
            assertEquals(10, level.getWidth());
            assertEquals(8, level.getHeight());
            assertEquals(20, level.getTrees().size());
        }
    }

    @Test
    public void passesCustomDimensionsAndTreeCountToGenerator() throws IOException {
        LevelLayout level = LevelSourceSelector.fromArguments(
                new String[]{"--random", "3", "2", "4"}).load();
        assertEquals(3, level.getWidth());
        assertEquals(2, level.getHeight());
        assertEquals(4, level.getTrees().size());
    }

    @Test
    public void loadsChosenFileIncludingPathWithSpaces() throws IOException {
        Path path = temporaryFolder.newFile("small level.txt").toPath();
        Files.write(path, "T_X\n___\n".getBytes(StandardCharsets.UTF_8));
        LevelSource source = LevelSourceSelector.fromArguments(new String[]{"--level", path.toString()});
        LevelLayout level = source.load();
        assertEquals(3, level.getWidth());
        assertEquals(2, level.getHeight());
        assertEquals(1, level.getTrees().size());
        assertEquals(2, level.getPlayerStart().x);
        assertEquals(1, level.getPlayerStart().y);
    }

    @Test
    public void rejectsUnknownMissingAndExtraArguments() {
        for (String[] args : new String[][]{
                {"--unknown"}, {"--level"}, {"--random", "3"},
                {"--level", "map.txt", "extra"}, {"--random", "3", "2", "1", "extra"}}) {
            assertThrows(IllegalArgumentException.class, () -> LevelSourceSelector.fromArguments(args));
        }
    }

    @Test
    public void rejectsNonNumericDimensionsAndInvalidRandomSettings() {
        assertThrows(IllegalArgumentException.class, () -> LevelSourceSelector.fromArguments(
                new String[]{"--random", "wide", "2", "1"}));
        assertThrows(IllegalArgumentException.class, () -> LevelSourceSelector.fromArguments(
                new String[]{"--random", "0", "2", "1"}));
        assertThrows(IllegalArgumentException.class, () -> LevelSourceSelector.fromArguments(
                new String[]{"--random", "2", "2", "4"}));
    }
}
