package ru.mipt.bit.platformer.model;

import ru.mipt.bit.platformer.game.Updatable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GameSession implements Updatable {
    private final List<Updatable> objects;

    public GameSession(List<? extends Updatable> objects) {
        this.objects = new ArrayList<>(objects);
        this.objects.forEach(Objects::requireNonNull);
    }

    @Override
    public void update(float elapsedSeconds) {
        for (Updatable object : objects) {
            object.update(elapsedSeconds);
        }
    }
}
