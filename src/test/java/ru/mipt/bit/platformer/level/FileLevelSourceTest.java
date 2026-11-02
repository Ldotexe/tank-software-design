package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.mipt.bit.platformer.model.Tree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FileLevelSourceTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void readsTreesAndPlayerWithTopRowAtHighestY() throws IOException {
        LevelLayout layout = source("T__\n_X_\n__T\n").load();

        assertEquals(3, layout.getWidth());
        assertEquals(3, layout.getHeight());
        assertEquals(Arrays.asList(new GridPoint2(0, 2), new GridPoint2(2, 0)),
                treePositions(layout));
        assertEquals(new GridPoint2(1, 1), layout.getPlayerStart());
    }

    @Test
    public void readsRectangularLevelWithPlayerInTopRow() throws IOException {
        LevelLayout layout = source("___X_\nT___T").load();

        assertEquals(5, layout.getWidth());
        assertEquals(2, layout.getHeight());
        assertEquals(new GridPoint2(3, 1), layout.getPlayerStart());
        assertEquals(Arrays.asList(new GridPoint2(0, 0), new GridPoint2(4, 0)),
                treePositions(layout));
    }

    @Test
    public void acceptsWindowsLineEndings() throws IOException {
        LevelLayout layout = source("T_\r\n_X\r\n").load();

        assertEquals(2, layout.getHeight());
        assertEquals(new GridPoint2(1, 0), layout.getPlayerStart());
    }

    @Test
    public void readsSinglePlayerTile() throws IOException {
        LevelLayout layout = source("X").load();

        assertEquals(1, layout.getWidth());
        assertEquals(1, layout.getHeight());
        assertEquals(new GridPoint2(0, 0), layout.getPlayerStart());
        assertTrue(layout.getTrees().isEmpty());
    }

    @Test
    public void rejectsEmptyFile() throws IOException {
        FileLevelSource source = source("");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, source::load);

        assertTrue(error.getMessage().contains("empty"));
    }

    @Test
    public void rejectsEmptyFirstRow() throws IOException {
        FileLevelSource source = source("\nX");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, source::load);

        assertTrue(error.getMessage().contains("Row 1"));
    }

    @Test
    public void rejectsRowsOfDifferentWidths() throws IOException {
        FileLevelSource source = source("X__\nTT");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, source::load);

        assertTrue(error.getMessage().contains("Row 2"));
        assertTrue(error.getMessage().contains("expected 3"));
    }

    @Test
    public void rejectsBlankRowInsideLevel() throws IOException {
        FileLevelSource source = source("X_\n\n_T");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, source::load);

        assertTrue(error.getMessage().contains("Row 2"));
    }

    @Test
    public void rejectsLevelWithoutPlayerStart() throws IOException {
        FileLevelSource source = source("T_\n_T");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, source::load);

        assertTrue(error.getMessage().contains("exactly one player start"));
    }

    @Test
    public void reportsPositionOfSecondPlayerStart() throws IOException {
        FileLevelSource source = source("X_\n_X");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, source::load);

        assertTrue(error.getMessage().contains("row 2, column 2"));
    }

    @Test
    public void reportsPositionOfUnknownCharacter() throws IOException {
        FileLevelSource source = source("X_\nT?");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, source::load);

        assertTrue(error.getMessage().contains("'?'"));
        assertTrue(error.getMessage().contains("row 2, column 2"));
    }

    @Test
    public void doesNotTreatWhitespaceAsEmptyTile() throws IOException {
        FileLevelSource source = source("X ");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, source::load);

        assertTrue(error.getMessage().contains("row 1, column 2"));
    }

    @Test
    public void preservesIoExceptionForMissingFile() {
        Path missingFile = temporaryFolder.getRoot().toPath().resolve("missing.txt");

        assertThrows(IOException.class, () -> new FileLevelSource(missingFile).load());
    }

    @Test
    public void rejectsInvalidUtf8WithIoException() throws IOException {
        Path path = temporaryFolder.newFile().toPath();
        Files.write(path, new byte[]{(byte) 0xC3, (byte) 0x28});

        assertThrows(IOException.class, () -> new FileLevelSource(path).load());
    }

    @Test
    public void rejectsNullPath() {
        assertThrows(NullPointerException.class, () -> new FileLevelSource(null));
    }

    private FileLevelSource source(String text) throws IOException {
        Path path = temporaryFolder.newFile().toPath();
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
        return new FileLevelSource(path);
    }

    private List<GridPoint2> treePositions(LevelLayout layout) {
        List<GridPoint2> positions = new ArrayList<>();
        for (Tree tree : layout.getTrees()) {
            positions.add(tree.getTilePosition());
        }
        return positions;
    }
}
