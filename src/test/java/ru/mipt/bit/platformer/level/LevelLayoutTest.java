package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class LevelLayoutTest {
    @Test
    public void storesDimensionsTreesAndPlayerStart() {
        Tree tree = new Tree(2, 1);
        LevelLayout layout = new LevelLayout(4, 3, Collections.singletonList(tree),
                new GridPoint2(0, 2));

        assertEquals(4, layout.getWidth());
        assertEquals(3, layout.getHeight());
        assertEquals(Collections.singletonList(tree), layout.getTrees());
        assertEquals(new GridPoint2(0, 2), layout.getPlayerStart());
    }

    @Test
    public void copiesInputListAndPlayerStart() {
        List<Tree> trees = new ArrayList<>();
        trees.add(new Tree(1, 1));
        GridPoint2 playerStart = new GridPoint2(0, 0);
        LevelLayout layout = new LevelLayout(2, 2, trees, playerStart);

        trees.clear();
        playerStart.set(1, 1);

        assertEquals(1, layout.getTrees().size());
        assertEquals(new GridPoint2(0, 0), layout.getPlayerStart());
    }

    @Test
    public void returnedDataCannotChangeLayout() {
        LevelLayout layout = new LevelLayout(2, 2, Collections.singletonList(new Tree(1, 1)),
                new GridPoint2(0, 0));

        layout.getPlayerStart().set(1, 1);
        layout.getTrees().get(0).getTilePosition().set(0, 0);

        assertEquals(new GridPoint2(0, 0), layout.getPlayerStart());
        assertEquals(new GridPoint2(1, 1), layout.getTrees().get(0).getTilePosition());
        assertThrows(UnsupportedOperationException.class, () -> layout.getTrees().clear());
    }

    @Test
    public void rejectsNonPositiveDimensions() {
        assertThrows(IllegalArgumentException.class,
                () -> new LevelLayout(0, 2, Collections.emptyList(), new GridPoint2()));
        assertThrows(IllegalArgumentException.class,
                () -> new LevelLayout(2, 0, Collections.emptyList(), new GridPoint2()));
        assertThrows(IllegalArgumentException.class,
                () -> new LevelLayout(-1, 2, Collections.emptyList(), new GridPoint2()));
        assertThrows(IllegalArgumentException.class,
                () -> new LevelLayout(2, -1, Collections.emptyList(), new GridPoint2()));
    }

    @Test
    public void rejectsPlayerStartOutsideLevel() {
        List<GridPoint2> invalidPositions = Arrays.asList(new GridPoint2(-1, 0),
                new GridPoint2(0, -1), new GridPoint2(2, 0), new GridPoint2(0, 2));

        for (GridPoint2 position : invalidPositions) {
            assertThrows(IllegalArgumentException.class,
                    () -> new LevelLayout(2, 2, Collections.emptyList(), position));
        }
    }

    @Test
    public void rejectsTreesOutsideLevel() {
        List<Tree> invalidTrees = Arrays.asList(new Tree(-1, 0), new Tree(0, -1),
                new Tree(2, 0), new Tree(0, 2));

        for (Tree tree : invalidTrees) {
            assertThrows(IllegalArgumentException.class,
                    () -> new LevelLayout(2, 2, Collections.singletonList(tree), new GridPoint2()));
        }
    }

    @Test
    public void rejectsDuplicateTrees() {
        assertThrows(IllegalArgumentException.class,
                () -> new LevelLayout(2, 2, Arrays.asList(new Tree(1, 1), new Tree(1, 1)),
                        new GridPoint2(0, 0)));
    }

    @Test
    public void rejectsPlayerStartOccupiedByTree() {
        assertThrows(IllegalArgumentException.class,
                () -> new LevelLayout(2, 2, Collections.singletonList(new Tree(1, 1)),
                        new GridPoint2(1, 1)));
    }

    @Test
    public void rejectsNullData() {
        assertThrows(NullPointerException.class,
                () -> new LevelLayout(2, 2, null, new GridPoint2()));
        assertThrows(NullPointerException.class,
                () -> new LevelLayout(2, 2, Collections.emptyList(), null));
        assertThrows(NullPointerException.class,
                () -> new LevelLayout(2, 2, Collections.singletonList(null), new GridPoint2()));
    }
}
