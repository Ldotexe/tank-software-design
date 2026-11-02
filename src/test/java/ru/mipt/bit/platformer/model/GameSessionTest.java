package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.game.Updatable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GameSessionTest {
    @Test
    public void updatesAllObjectsInOrderWithSameElapsedTime() {
        List<String> updates = new ArrayList<>();
        Updatable player = time -> updates.add("player:" + time);
        Updatable projectile = time -> updates.add("projectile:" + time);
        GameSession session = new GameSession(Arrays.asList(player, projectile));
        session.update(0.25f);
        assertEquals(Arrays.asList("player:0.25", "projectile:0.25"), updates);
    }

    @Test
    public void usesSuppliedPlayerAndCollisionRules() {
        GameField field = new GameField();
        field.addObstacle(new Tree(4, 6));
        Tank player = new Tank(field, 4, 4, 0.2f);
        GameSession session = new GameSession(Collections.singletonList(player));
        player.tryStartMoving(Direction.UP);
        session.update(0.2f);
        assertEquals(new GridPoint2(4, 5), player.getTilePosition());
        player.tryStartMoving(Direction.UP);
        session.update(0.2f);
        assertEquals(new GridPoint2(4, 5), player.getTilePosition());
        assertFalse(player.isMoving());
    }

    @Test
    public void copiesListButUpdatesOriginalObjects() {
        Tank player = new Tank(tile -> true, 1, 1, 0.4f);
        List<Updatable> objects = new ArrayList<>();
        objects.add(player);
        GameSession session = new GameSession(objects);
        objects.clear();
        player.tryStartMoving(Direction.RIGHT);
        session.update(0.4f);
        assertEquals(new GridPoint2(2, 1), player.getTilePosition());
    }

    @Test
    public void supportsEmptySession() {
        new GameSession(Collections.emptyList()).update(0.4f);
    }

    @Test
    public void rejectsNullObjects() {
        assertThrows(NullPointerException.class,
                () -> new GameSession(Arrays.asList(time -> { }, null)));
    }
}
