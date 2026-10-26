package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class RandomLevelSourceTest {
    @Test
    public void placesUniqueTreesAndPlayerInsideLevel() {
        LevelLayout layout = new RandomLevelSource(10, 8, 20, new Random(17)).load();
        Set<GridPoint2> occupiedTiles = new HashSet<>();

        assertEquals(10, layout.getWidth());
        assertEquals(8, layout.getHeight());
        assertEquals(20, layout.getTrees().size());
        for (Tree tree : layout.getTrees()) {
            GridPoint2 position = tree.getTilePosition();
            assertTrue(position.x >= 0 && position.x < layout.getWidth());
            assertTrue(position.y >= 0 && position.y < layout.getHeight());
            assertTrue(occupiedTiles.add(position));
        }
        GridPoint2 playerStart = layout.getPlayerStart();
        assertTrue(playerStart.x >= 0 && playerStart.x < layout.getWidth());
        assertTrue(playerStart.y >= 0 && playerStart.y < layout.getHeight());
        assertFalse(occupiedTiles.contains(playerStart));
    }

    @Test
    public void sameSeedProducesSameLayout() {
        LevelLayout first = new RandomLevelSource(10, 8, 20, new Random(42)).load();
        LevelLayout second = new RandomLevelSource(10, 8, 20, new Random(42)).load();

        assertEquals(treePositions(first), treePositions(second));
        assertEquals(first.getPlayerStart(), second.getPlayerStart());
    }

    @Test
    public void loadsSingleTileWithoutTrees() {
        LevelLayout layout = new RandomLevelSource(1, 1, 0).load();

        assertTrue(layout.getTrees().isEmpty());
        assertEquals(new GridPoint2(0, 0), layout.getPlayerStart());
    }

    @Test(timeout = 1000)
    public void fillsAllTilesExceptPlayerStart() {
        LevelLayout layout = new RandomLevelSource(7, 5, 34, new Random(8)).load();
        Set<GridPoint2> positions = new HashSet<>(treePositions(layout));

        assertEquals(34, positions.size());
        assertTrue(positions.add(layout.getPlayerStart()));
        assertEquals(35, positions.size());
    }

    @Test
    public void canGenerateLevelWithoutTrees() {
        LevelLayout layout = new RandomLevelSource(4, 3, 0, new Random(8)).load();

        assertEquals(4, layout.getWidth());
        assertEquals(3, layout.getHeight());
        assertTrue(layout.getTrees().isEmpty());
    }

    @Test
    public void rejectsNonPositiveDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(0, 2, 0));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(2, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(-1, 2, 0));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(2, -1, 0));
    }

    @Test
    public void rejectsCountsThatCannotLeavePlayerTile() {
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(2, 2, -1));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(2, 2, 4));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelSource(2, 2, 5));
    }

    @Test
    public void rejectsTileCountOverflow() {
        assertThrows(IllegalArgumentException.class,
                () -> new RandomLevelSource(Integer.MAX_VALUE, 2, 0));
    }

    @Test
    public void rejectsNullRandom() {
        assertThrows(NullPointerException.class, () -> new RandomLevelSource(2, 2, 0, null));
    }

    private List<GridPoint2> treePositions(LevelLayout layout) {
        List<GridPoint2> positions = new ArrayList<>();
        for (Tree tree : layout.getTrees()) {
            positions.add(tree.getTilePosition());
        }
        return positions;
    }
}
