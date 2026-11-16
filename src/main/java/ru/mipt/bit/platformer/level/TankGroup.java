package ru.mipt.bit.platformer.level;

import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TankGroup {
    private final GameField field;
    private final Tank player;
    private final List<Tank> bots;
    private final List<Tank> tanks;

    TankGroup(GameField field, Tank player, List<Tank> bots) {
        this.field = Objects.requireNonNull(field);
        this.player = Objects.requireNonNull(player);
        this.bots = Collections.unmodifiableList(new ArrayList<>(bots));
        List<Tank> allTanks = new ArrayList<>();
        allTanks.add(player);
        allTanks.addAll(this.bots);
        tanks = Collections.unmodifiableList(allTanks);
    }

    public GameField getField() {
        return field;
    }

    public Tank getPlayer() {
        return player;
    }

    public List<Tank> getBots() {
        return bots;
    }

    public List<Tank> getTanks() {
        return tanks;
    }
}
