package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.command.ShootCommand;
import ru.mipt.bit.platformer.game.GameLoop;
import ru.mipt.bit.platformer.game.GameView;
import ru.mipt.bit.platformer.model.Bullet;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.GameObject;
import ru.mipt.bit.platformer.model.GameSession;
import ru.mipt.bit.platformer.model.LevelListener;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class LevelCombatTest {
    @Test
    public void adjacentShotsDamageTankAndNotifyRemovalBeforeRendering() {
        LevelLayout layout = new LevelLayout(2, 1, Collections.emptyList(), new GridPoint2(0, 0));
        TankGroup group = new TankSpawner(new Random(1)).spawn(layout, 1, 0.4f);
        GameField field = group.getField();
        List<GameObject> observed = new ArrayList<>();
        List<GameObject> removed = new ArrayList<>();
        field.addListener(new LevelListener() {
            @Override
            public void objectAdded(GameObject object) {
                observed.add(object);
            }

            @Override
            public void objectRemoved(GameObject object) {
                observed.remove(object);
                removed.add(object);
            }
        });
        assertEquals(field.getObjects(), observed);
        GameLoop game = new GameLoop(() -> Collections.singletonList(new ShootCommand(group.getPlayer())),
                new GameSession(field), checkingView(field, observed));
        Tank target = group.getBots().get(0);
        for (int shot = 1; shot <= 4; shot++) {
            game.update(0f);
            assertEquals(100 - shot * 25, target.getHealth());
            assertTrue(field.contains(target));
        }
        game.update(0f);
        assertEquals(-25, target.getHealth());
        assertFalse(field.contains(target));
        assertTrue(field.isTileUnoccupied(new GridPoint2(1, 0)));
        assertEquals(5, removed.stream().filter(object -> object instanceof Bullet).count());
        assertEquals(1, removed.stream().filter(object -> object == target).count());
        assertEquals(Collections.singletonList(group.getPlayer()), field.getObjects());
    }

    @Test
    public void firingOutsideSingleTileLevelLeavesOnlyPlayer() {
        LevelLayout layout = new LevelLayout(1, 1, Collections.emptyList(), new GridPoint2(0, 0));
        TankGroup group = new TankSpawner(new Random(1)).spawn(layout, 0, 0.4f);
        group.getPlayer().shoot();
        assertEquals(2, group.getField().getObjects().size());
        new GameSession(group.getField()).update(0f);
        assertEquals(Collections.singletonList(group.getPlayer()), group.getField().getObjects());
        assertEquals(Tank.DEFAULT_HEALTH, group.getPlayer().getHealth());
    }

    private GameView checkingView(GameField field, List<GameObject> observed) {
        return new GameView() {
            @Override
            public void render() {
                assertEquals(field.getObjects(), observed);
                assertFalse(observed.stream().anyMatch(object -> object instanceof Bullet));
            }

            @Override
            public void dispose() {
            }
        };
    }
}
