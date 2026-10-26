package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FileLevelSource implements LevelSource {
    private final Path path;

    public FileLevelSource(Path path) {
        this.path = Objects.requireNonNull(path);
    }

    @Override
    public LevelLayout load() throws IOException {
        List<String> rows = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Level file is empty: " + path);
        }
        int width = rows.get(0).length();
        int height = rows.size();
        if (width == 0) {
            throw new IllegalArgumentException("Row 1 must not be empty");
        }
        List<Tree> trees = new ArrayList<>();
        GridPoint2 playerStart = null;
        for (int rowIndex = 0; rowIndex < height; rowIndex++) {
            String row = rows.get(rowIndex);
            if (row.length() != width) {
                throw new IllegalArgumentException("Row " + (rowIndex + 1) + " has "
                        + row.length() + " columns; expected " + width);
            }
            int tileY = height - rowIndex - 1;
            for (int columnIndex = 0; columnIndex < width; columnIndex++) {
                char tile = row.charAt(columnIndex);
                switch (tile) {
                    case 'T':
                        trees.add(new Tree(columnIndex, tileY));
                        break;
                    case 'X':
                        if (playerStart != null) {
                            throw new IllegalArgumentException("Multiple player starts at "
                                    + location(rowIndex, columnIndex));
                        }
                        playerStart = new GridPoint2(columnIndex, tileY);
                        break;
                    case '_':
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown tile '" + tile + "' at "
                                + location(rowIndex, columnIndex));
                }
            }
        }
        if (playerStart == null) {
            throw new IllegalArgumentException("Level must contain exactly one player start (X)");
        }
        return new LevelLayout(width, height, trees, playerStart);
    }

    private String location(int rowIndex, int columnIndex) {
        return "row " + (rowIndex + 1) + ", column " + (columnIndex + 1);
    }
}
