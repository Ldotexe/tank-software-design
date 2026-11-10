package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class TankSpawner {
    private final Random random;

    public TankSpawner(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    public TankGroup spawn(LevelLayout layout, int botCount, float movementDurationSeconds) {
        Objects.requireNonNull(layout);
        if (botCount < 0) {
            throw new IllegalArgumentException("Bot count must be non-negative");
        }
        long tileCount = (long) layout.getWidth() * layout.getHeight();
        long freeCount = tileCount - layout.getTrees().size() - 1;
        if (botCount > freeCount) {
            throw new IllegalArgumentException("Not enough free tiles: requested " + botCount
                    + " bots, available " + freeCount);
        }
        if (tileCount > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Level contains too many tiles");
        }
        GameField field = new GameField(layout.getWidth(), layout.getHeight());
        for (Tree tree : layout.getTrees()) {
            field.addObstacle(tree);
        }
        GridPoint2 start = layout.getPlayerStart();
        Tank player = new Tank(field, start.x, start.y, movementDurationSeconds);
        field.addObstacle(player);
        if (botCount == 0) {
            return new TankGroup(player, Collections.emptyList());
        }
        List<GridPoint2> freeTiles = new ArrayList<>((int) freeCount);
        for (int y = 0; y < layout.getHeight(); y++) {
            for (int x = 0; x < layout.getWidth(); x++) {
                GridPoint2 position = new GridPoint2(x, y);
                if (field.isTileUnoccupied(position)) {
                    freeTiles.add(position);
                }
            }
        }
        Collections.shuffle(freeTiles, random);
        List<Tank> bots = new ArrayList<>(botCount);
        for (int index = 0; index < botCount; index++) {
            GridPoint2 position = freeTiles.get(index);
            Tank bot = new Tank(field, position.x, position.y, movementDurationSeconds);
            field.addObstacle(bot);
            bots.add(bot);
        }
        return new TankGroup(player, bots);
    }
}
