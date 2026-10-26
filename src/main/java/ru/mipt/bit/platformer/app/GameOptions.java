package ru.mipt.bit.platformer.app;

import ru.mipt.bit.platformer.level.LevelSource;

import java.util.ArrayList;
import java.util.List;

public final class GameOptions {
    private static final int DEFAULT_BOT_COUNT = 3;
    private final LevelSource levelSource;
    private final int botCount;

    private GameOptions(LevelSource levelSource, int botCount) {
        this.levelSource = levelSource;
        this.botCount = botCount;
    }

    public static GameOptions fromArguments(String[] args) {
        List<String> levelArguments = new ArrayList<>();
        int botCount = DEFAULT_BOT_COUNT;
        boolean botCountSpecified = false;
        for (int index = 0; index < args.length; index++) {
            if (!args[index].equals("--bots")) {
                levelArguments.add(args[index]);
                continue;
            }
            if (botCountSpecified || index + 1 == args.length) {
                throw new IllegalArgumentException("Specify --bots count once");
            }
            try {
                botCount = Integer.parseInt(args[++index]);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Bot count must be an integer", exception);
            }
            if (botCount < 0) {
                throw new IllegalArgumentException("Bot count must be non-negative");
            }
            botCountSpecified = true;
        }
        LevelSource source = LevelSourceSelector.fromArguments(levelArguments.toArray(new String[0]));
        return new GameOptions(source, botCount);
    }

    public LevelSource getLevelSource() {
        return levelSource;
    }

    public int getBotCount() {
        return botCount;
    }
}
