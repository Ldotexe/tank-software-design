package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.game.Updatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GameSession implements Updatable {
    private final GameField field;
    private final BulletSimulation bulletSimulation;

    public GameSession(GameField field) {
        this.field = Objects.requireNonNull(field);
        bulletSimulation = new BulletSimulation(field);
    }

    @Override
    public void update(float elapsedSeconds) {
        if (!Float.isFinite(elapsedSeconds) || elapsedSeconds < 0f) {
            throw new IllegalArgumentException("Elapsed time must be finite and non-negative");
        }
        List<GameObject> objects = new ArrayList<>(field.getObjects());
        List<Bullet> bullets = new ArrayList<>();
        for (GameObject object : objects) {
            if (object instanceof Bullet) {
                bullets.add((Bullet) object);
            }
        }
        bulletSimulation.resolveContacts(bullets, Collections.emptyMap());
        double remaining = elapsedSeconds;
        while (remaining > 0) {
            double step = nextStep(objects, remaining);
            Map<Bullet, GridPoint2> previousPositions = bulletSimulation.positions(bullets);
            for (GameObject object : objects) {
                if (!field.contains(object)) {
                    continue;
                }
                if (object instanceof Tank) {
                    ((Tank) object).advance(step);
                } else if (object instanceof Updatable && !(object instanceof Bullet)) {
                    ((Updatable) object).update((float) step);
                }
            }
            bulletSimulation.advance(bullets, step);
            bulletSimulation.resolveContacts(bullets, previousPositions);
            remaining -= step;
        }
    }

    private double nextStep(List<GameObject> objects, double remaining) {
        double step = remaining;
        for (GameObject object : objects) {
            if (!field.contains(object)) {
                continue;
            }
            if (object instanceof Bullet) {
                step = Math.min(step, ((Bullet) object).timeUntilNextTile());
            } else if (object instanceof Tank && ((Tank) object).isMoving()) {
                step = Math.min(step, ((Tank) object).timeUntilArrival());
            }
        }
        return step;
    }
}
