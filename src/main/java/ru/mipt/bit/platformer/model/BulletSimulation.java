package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class BulletSimulation {
    private final GameField field;

    BulletSimulation(GameField field) {
        this.field = field;
    }

    Map<Bullet, GridPoint2> positions(List<Bullet> bullets) {
        Map<Bullet, GridPoint2> positions = new LinkedHashMap<>();
        for (Bullet bullet : bullets) {
            if (field.contains(bullet)) {
                positions.put(bullet, bullet.getTilePosition());
            }
        }
        return positions;
    }

    void advance(List<Bullet> bullets, double elapsedSeconds) {
        for (Bullet bullet : bullets) {
            if (field.contains(bullet)) {
                bullet.update(elapsedSeconds);
            }
        }
    }

    void resolveContacts(List<Bullet> bullets, Map<Bullet, GridPoint2> previousPositions) {
        List<Bullet> activeBullets = new ArrayList<>();
        for (Bullet bullet : bullets) {
            if (field.contains(bullet)) {
                activeBullets.add(bullet);
            }
        }
        Set<Bullet> removed = new LinkedHashSet<>();
        Map<Bullet, Tank> hits = new LinkedHashMap<>();
        List<Obstacle> obstacles = new ArrayList<>(field.getObstacles());
        for (Bullet bullet : activeBullets) {
            GridPoint2 position = bullet.getTilePosition();
            if (!field.isInside(position)) {
                removed.add(bullet);
                continue;
            }
            for (Obstacle obstacle : obstacles) {
                if (obstacle != bullet.getOwner() && obstacle.occupiesTile(position)) {
                    removed.add(bullet);
                    if (obstacle instanceof Tank) {
                        hits.put(bullet, (Tank) obstacle);
                    }
                    break;
                }
            }
        }
        for (int firstIndex = 0; firstIndex < activeBullets.size(); firstIndex++) {
            Bullet first = activeBullets.get(firstIndex);
            for (int secondIndex = firstIndex + 1; secondIndex < activeBullets.size(); secondIndex++) {
                Bullet second = activeBullets.get(secondIndex);
                if (first.getTilePosition().equals(second.getTilePosition())
                        || swappedTiles(first, second, previousPositions)) {
                    removed.add(first);
                    removed.add(second);
                }
            }
        }
        for (Bullet bullet : removed) {
            field.removeObject(bullet);
        }
        for (Map.Entry<Bullet, Tank> hit : hits.entrySet()) {
            hit.getValue().takeDamage(hit.getKey().getDamage());
        }
    }

    private boolean swappedTiles(Bullet first, Bullet second, Map<Bullet, GridPoint2> previousPositions) {
        GridPoint2 firstPrevious = previousPositions.get(first);
        GridPoint2 secondPrevious = previousPositions.get(second);
        return firstPrevious != null && secondPrevious != null
                && firstPrevious.equals(second.getTilePosition())
                && secondPrevious.equals(first.getTilePosition());
    }
}
