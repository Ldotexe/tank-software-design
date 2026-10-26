package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.GameField;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.badlogic.gdx.Input.Keys.*;
import static org.junit.Assert.*;

public class PlayerControlsTest {
    @Test
    public void bindsAllArrowsAndWasdKeys() {
        assertMovement(UP, Direction.UP);
        assertMovement(W, Direction.UP);
        assertMovement(LEFT, Direction.LEFT);
        assertMovement(A, Direction.LEFT);
        assertMovement(DOWN, Direction.DOWN);
        assertMovement(S, Direction.DOWN);
        assertMovement(RIGHT, Direction.RIGHT);
        assertMovement(D, Direction.RIGHT);
    }

    private void assertMovement(int key, Direction direction) {
        Tank tank = new Tank(new GameField(10, 8), 1, 1, 0.4f);
        KeyboardHandler keyboard = new KeyboardHandler(candidate -> candidate == key, candidate -> false);
        PlayerControls.bindMovement(keyboard, tank::tryStartMoving);
        keyboard.handleInput();
        assertEquals(direction, tank.getDirection());
        assertEquals(direction.calculateAdjacentTile(new GridPoint2(1, 1)), tank.getDestinationTile());
        assertTrue(tank.isMoving());
    }

    @Test
    public void simultaneousMovementKeysPreserveOriginalPriority() {
        Tank tank = new Tank(new GameField(10, 8), 1, 1, 0.4f);
        KeyboardHandler keyboard = new KeyboardHandler(key -> true, key -> false);
        PlayerControls.bindMovement(keyboard, tank::tryStartMoving);
        keyboard.handleInput();
        assertEquals(Direction.UP, tank.getDirection());
        assertEquals(new GridPoint2(1, 2), tank.getDestinationTile());
    }

    @Test
    public void blockedDirectionAllowsNextHeldDirection() {
        GameField field = new GameField(10, 8);
        field.addObstacle(new Tree(1, 2));
        Tank tank = new Tank(field, 1, 1, 0.4f);
        Set<Integer> pressed = new HashSet<>(Arrays.asList(UP, LEFT));
        KeyboardHandler keyboard = new KeyboardHandler(pressed::contains, key -> false);
        PlayerControls.bindMovement(keyboard, tank::tryStartMoving);
        keyboard.handleInput();
        assertEquals(Direction.LEFT, tank.getDirection());
        assertEquals(new GridPoint2(0, 1), tank.getDestinationTile());
    }

    @Test
    public void holdingMovementContinuesAfterPreviousTileIsReached() {
        Tank tank = new Tank(new GameField(10, 8), 1, 1, 0.4f);
        KeyboardHandler keyboard = new KeyboardHandler(key -> key == RIGHT, key -> false);
        PlayerControls.bindMovement(keyboard, tank::tryStartMoving);
        keyboard.handleInput();
        tank.update(0.4f);
        keyboard.handleInput();
        tank.update(0.4f);
        assertEquals(new GridPoint2(3, 1), tank.getTilePosition());
    }

    @Test
    public void canBindAnotherMovementReceiverWithoutTank() {
        List<Direction> requestedDirections = new ArrayList<>();
        KeyboardHandler keyboard = new KeyboardHandler(key -> key == W, key -> false);
        PlayerControls.bindMovement(keyboard, requestedDirections::add);
        keyboard.handleInput();
        assertEquals(Collections.singletonList(Direction.UP), requestedDirections);
    }
}
