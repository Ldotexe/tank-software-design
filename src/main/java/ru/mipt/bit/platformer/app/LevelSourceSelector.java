package ru.mipt.bit.platformer.app;

import ru.mipt.bit.platformer.level.FileLevelSource;
import ru.mipt.bit.platformer.level.LevelSource;
import ru.mipt.bit.platformer.level.RandomLevelSource;

import java.nio.file.Paths;

public final class LevelSourceSelector {
    private static final int DEFAULT_WIDTH = 10;
    private static final int DEFAULT_HEIGHT = 8;
    private static final int DEFAULT_TREE_COUNT = 20;
    private static final String USAGE = "Use --random [width height treeCount] or --level path";

    private LevelSourceSelector() {
    }

    public static LevelSource fromArguments(String[] args) {
        if (args.length == 0 || args.length == 1 && args[0].equals("--random")) {
            return new RandomLevelSource(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TREE_COUNT);
        }
        if (args.length == 4 && args[0].equals("--random")) {
            try {
                return new RandomLevelSource(Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Width, height and treeCount must be integers. " + USAGE,
                        exception);
            }
        }
        if (args.length == 2 && args[0].equals("--level")) {
            return new FileLevelSource(Paths.get(args[1]));
        }
        throw new IllegalArgumentException(USAGE);
    }
}
