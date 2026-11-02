package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class LevelLifecycleTest {
    @Test
    public void listenerReceivesInitialObjectsAndLaterChanges() {
        GameField field = new GameField(5, 3);
        Tree initial = new Tree(1, 1);
        Tree added = new Tree(2, 1);
        field.addObstacle(initial);
        RecordingListener listener = new RecordingListener();

        field.addListener(listener);
        field.addObject(added);
        field.removeObject(initial);

        assertEquals(Arrays.asList(initial, added), listener.added);
        assertEquals(Collections.singletonList(initial), listener.removed);
        assertEquals(Collections.singletonList(added), field.getObjects());
        assertTrue(field.isTileUnoccupied(new GridPoint2(1, 1)));
        assertFalse(field.isTileUnoccupied(new GridPoint2(2, 1)));
    }

    @Test
    public void registrationAndRemovalSendOnlyOneEvent() {
        GameField field = new GameField(5, 3);
        RecordingListener listener = new RecordingListener();
        Tree tree = new Tree(1, 1);
        field.addListener(listener);

        field.addObject(tree);
        field.addObstacle(tree);
        field.addListener(listener);
        field.removeObject(tree);
        field.removeObject(tree);

        assertEquals(Collections.singletonList(tree), listener.added);
        assertEquals(Collections.singletonList(tree), listener.removed);
        assertTrue(field.getObjects().isEmpty());
        assertTrue(field.getObstacles().isEmpty());
    }

    @Test
    public void removedListenerReceivesNoMoreEvents() {
        GameField field = new GameField(5, 3);
        RecordingListener listener = new RecordingListener();
        Tree tree = new Tree(1, 1);
        field.addListener(listener);
        field.removeListener(listener);

        field.addObject(tree);
        field.removeObject(tree);

        assertTrue(listener.added.isEmpty());
        assertTrue(listener.removed.isEmpty());
    }

    @Test
    public void objectCollectionIsReadOnlyAndTracksChanges() {
        GameField field = new GameField(5, 3);
        List<GameObject> objects = field.getObjects();
        Tree tree = new Tree(1, 1);
        field.addObject(tree);

        assertTrue(field.contains(tree));
        assertEquals(Collections.singletonList(tree), objects);
        assertThrows(UnsupportedOperationException.class, objects::clear);
        field.removeObject(tree);
        assertFalse(field.contains(tree));
        assertTrue(objects.isEmpty());
    }

    @Test
    public void customObstaclesStillWorkWithoutVisualObjects() {
        GameField field = new GameField(5, 3);
        RecordingListener listener = new RecordingListener();
        field.addListener(listener);
        field.addObstacle(tile -> tile.x == 2);

        assertFalse(field.isTileUnoccupied(new GridPoint2(2, 1)));
        assertTrue(field.getObjects().isEmpty());
        assertTrue(listener.added.isEmpty());
    }

    @Test
    public void registeredObjectCannotBelongToTwoLevels() {
        GameField first = new GameField(5, 3);
        GameField second = new GameField(5, 3);
        Tree tree = new Tree(1, 1);
        first.addObject(tree);

        assertThrows(IllegalArgumentException.class, () -> second.addObject(tree));
        assertTrue(first.contains(tree));
        assertFalse(second.contains(tree));
    }

    @Test
    public void rejectsNullObjectsAndListeners() {
        GameField field = new GameField(5, 3);
        assertThrows(NullPointerException.class, () -> field.addObject(null));
        assertThrows(NullPointerException.class, () -> field.addListener(null));
    }

    @Test
    public void removalInsideAddedCallbackDoesNotSendStaleAddition() {
        GameField field = new GameField(5, 3);
        Tree tree = new Tree(1, 1);
        field.addListener(new LevelListener() {
            @Override
            public void objectAdded(GameObject object) {
                field.removeObject(object);
            }

            @Override
            public void objectRemoved(GameObject object) {
            }
        });
        RecordingListener listener = new RecordingListener();
        field.addListener(listener);

        field.addObject(tree);

        assertFalse(field.contains(tree));
        assertTrue(listener.added.isEmpty());
        assertEquals(Collections.singletonList(tree), listener.removed);
    }

    @Test
    public void additionInsideRemovedCallbackDoesNotSendStaleRemoval() {
        GameField field = new GameField(5, 3);
        Tree tree = new Tree(1, 1);
        field.addObject(tree);
        field.addListener(new LevelListener() {
            @Override
            public void objectAdded(GameObject object) {
            }

            @Override
            public void objectRemoved(GameObject object) {
                field.addObject(object);
            }
        });
        RecordingListener listener = new RecordingListener();
        field.addListener(listener);
        listener.added.clear();

        field.removeObject(tree);

        assertTrue(field.contains(tree));
        assertEquals(Collections.singletonList(tree), listener.added);
        assertTrue(listener.removed.isEmpty());
    }

    private static final class RecordingListener implements LevelListener {
        private final List<GameObject> added = new ArrayList<>();
        private final List<GameObject> removed = new ArrayList<>();

        @Override
        public void objectAdded(GameObject object) {
            added.add(object);
        }

        @Override
        public void objectRemoved(GameObject object) {
            removed.add(object);
        }
    }
}
