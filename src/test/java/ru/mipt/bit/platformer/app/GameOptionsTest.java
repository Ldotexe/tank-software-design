package ru.mipt.bit.platformer.app;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.mipt.bit.platformer.level.LevelLayout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class GameOptionsTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void defaultsToThreeBotsAndRandomLevel() throws IOException {
        GameOptions options = GameOptions.fromArguments(new String[0]);
        assertEquals(3, options.getBotCount());
        LevelLayout layout = options.getLevelSource().load();
        assertEquals(10, layout.getWidth());
        assertEquals(8, layout.getHeight());
        assertEquals(20, layout.getTrees().size());
    }

    @Test
    public void acceptsBotCountBeforeOrAfterLevelArguments() throws IOException {
        for (String[] args : new String[][]{
                {"--bots", "5", "--random", "6", "4", "2"},
                {"--random", "6", "4", "2", "--bots", "5"}}) {
            GameOptions options = GameOptions.fromArguments(args);
            assertEquals(5, options.getBotCount());
            LevelLayout layout = options.getLevelSource().load();
            assertEquals(6, layout.getWidth());
            assertEquals(4, layout.getHeight());
            assertEquals(2, layout.getTrees().size());
        }
    }

    @Test
    public void acceptsZeroBotsWithoutExplicitLevelMode() {
        assertEquals(0, GameOptions.fromArguments(new String[]{"--bots", "0"}).getBotCount());
    }

    @Test
    public void preservesFileModeAndPathWithSpaces() throws IOException {
        Path path = temporaryFolder.newFile("test level.txt").toPath();
        Files.write(path, "X__\n__T\n".getBytes(StandardCharsets.UTF_8));
        GameOptions options = GameOptions.fromArguments(new String[]{"--level", path.toString(), "--bots", "1"});
        assertEquals(1, options.getBotCount());
        LevelLayout layout = options.getLevelSource().load();
        assertEquals(3, layout.getWidth());
        assertEquals(2, layout.getHeight());
        assertEquals(1, layout.getTrees().size());
    }

    @Test
    public void rejectsInvalidMissingAndRepeatedBotCounts() {
        for (String[] args : new String[][]{
                {"--bots"}, {"--bots", "-1"}, {"--bots", "many"},
                {"--bots", "2147483648"}, {"--bots", "1", "--bots", "2"}}) {
            assertThrows(IllegalArgumentException.class, () -> GameOptions.fromArguments(args));
        }
    }

    @Test
    public void stillRejectsInvalidLevelArguments() {
        assertThrows(IllegalArgumentException.class,
                () -> GameOptions.fromArguments(new String[]{"--random", "2", "2", "1", "--unknown"}));
    }
}
