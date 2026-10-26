package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class TankSpawnerTest {
    @Test
    public void placesBotsOnDistinctFreeTilesAndPreservesPlayerStart() {
        LevelLayout layout = new LevelLayout(5, 4,
                Arrays.asList(new Tree(0, 0), new Tree(2, 3)), new GridPoint2(1, 1));
        TankGroup group = new TankSpawner(new Random(17)).spawn(layout, 6, 0.4f);
        assertEquals(new GridPoint2(1, 1), group.getPlayer().getTilePosition());
        assertEquals(6, group.getBots().size());
        assertEquals(7, group.getTanks().size());
        Set<GridPoint2> occupied = new HashSet<>();
        for (Tree tree : layout.getTrees()) {
            occupied.add(tree.getTilePosition());
        }
        for (Tank tank : group.getTanks()) {
            GridPoint2 position = tank.getTilePosition();
            assertTrue(position.x >= 0 && position.x < layout.getWidth());
            assertTrue(position.y >= 0 && position.y < layout.getHeight());
            assertTrue(occupied.add(position));
        }
    }

    @Test
    public void sameSeedReproducesBotPositions() {
        LevelLayout layout = new LevelLayout(5, 4, Collections.emptyList(), new GridPoint2(0, 0));
        TankGroup first = new TankSpawner(new Random(42)).spawn(layout, 5, 0.4f);
        TankGroup second = new TankSpawner(new Random(42)).spawn(layout, 5, 0.4f);
        assertEquals(positions(first.getBots()), positions(second.getBots()));
    }

    @Test(timeout = 1000)
    public void fillsEveryRemainingCellWithoutRepeats() {
        LevelLayout layout = new LevelLayout(3, 2,
                Collections.singletonList(new Tree(0, 1)), new GridPoint2(0, 0));
        TankGroup group = new TankSpawner(new Random(1)).spawn(layout, 4, 0.4f);
        assertEquals(5, new HashSet<>(positions(group.getTanks())).size());
    }

    @Test
    public void spawnedTanksShareCollisionRulesAndReserveTheirCells() {
        LevelLayout layout = new LevelLayout(3, 1, Collections.emptyList(), new GridPoint2(0, 0));
        TankGroup group = new TankSpawner(new Random(1)).spawn(layout, 2, 0.4f);
        Tank player = group.getPlayer();
        player.tryStartMoving(Direction.RIGHT);
        assertFalse(player.isMoving());
        Tank neighbor = group.getBots().stream()
                .filter(tank -> tank.getTilePosition().x == 1).findFirst().get();
        neighbor.tryStartMoving(Direction.LEFT);
        assertFalse(neighbor.isMoving());
    }

    @Test
    public void spawnedPlayerCannotLeaveLevel() {
        LevelLayout layout = new LevelLayout(1, 1, Collections.emptyList(), new GridPoint2(0, 0));
        TankGroup group = new TankSpawner(new Random(1)).spawn(layout, 0, 0.4f);
        assertTrue(group.getBots().isEmpty());
        for (Direction direction : Direction.values()) {
            group.getPlayer().tryStartMoving(direction);
            assertFalse(group.getPlayer().isMoving());
        }
    }

    @Test
    public void usesSuppliedMovementDuration() {
        LevelLayout layout = new LevelLayout(2, 1, Collections.emptyList(), new GridPoint2(0, 0));
        Tank player = new TankSpawner(new Random(1)).spawn(layout, 0, 0.8f).getPlayer();
        player.tryStartMoving(Direction.RIGHT);
        player.update(0.2f);
        assertEquals(0.25f, player.getMovementProgress(), 0.00001f);
    }

    @Test
    public void doesNotModifyLayoutOrChangePlayerStartWhenSpawningAgain() {
        LevelLayout layout = new LevelLayout(4, 3,
                Collections.singletonList(new Tree(1, 2)), new GridPoint2(0, 0));
        TankSpawner spawner = new TankSpawner(new Random(17));
        TankGroup first = spawner.spawn(layout, 2, 0.4f);
        TankGroup second = spawner.spawn(layout, 2, 0.4f);
        assertNotSame(first.getPlayer(), second.getPlayer());
        assertEquals(new GridPoint2(0, 0), layout.getPlayerStart());
        assertEquals(1, layout.getTrees().size());
    }

    @Test
    public void rejectsNegativeCountAndInsufficientSpace() {
        LevelLayout layout = new LevelLayout(2, 1, Collections.emptyList(), new GridPoint2(0, 0));
        TankSpawner spawner = new TankSpawner(new Random(1));
        assertThrows(IllegalArgumentException.class, () -> spawner.spawn(layout, -1, 0.4f));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> spawner.spawn(layout, 2, 0.4f));
        assertTrue(error.getMessage().contains("requested 2"));
        assertTrue(error.getMessage().contains("available 1"));
    }

    @Test
    public void rejectsInvalidDurationAndMissingDependencies() {
        LevelLayout layout = new LevelLayout(2, 1, Collections.emptyList(), new GridPoint2(0, 0));
        assertThrows(NullPointerException.class, () -> new TankSpawner(null));
        TankSpawner spawner = new TankSpawner(new Random(1));
        assertThrows(NullPointerException.class, () -> spawner.spawn(null, 0, 0.4f));
        assertThrows(IllegalArgumentException.class, () -> spawner.spawn(layout, 0, 0f));
    }

    private List<GridPoint2> positions(List<Tank> tanks) {
        List<GridPoint2> positions = new ArrayList<>();
        for (Tank tank : tanks) {
            positions.add(tank.getTilePosition());
        }
        return positions;
    }
}
