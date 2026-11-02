package ru.mipt.bit.platformer.input;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class KeyboardHandlerTest {
    @Test
    public void idleAndUnboundKeysDoNotExecuteActions() {
        Set<Integer> pressed = new HashSet<>();
        KeyboardHandler keyboard = new KeyboardHandler(pressed::contains, key -> false);
        AtomicInteger calls = new AtomicInteger();
        keyboard.bindHeld(calls::incrementAndGet, 1);
        keyboard.handleInput();
        pressed.add(2);
        keyboard.handleInput();
        assertEquals(0, calls.get());
    }

    @Test
    public void eitherAlternativeRunsActionOnceEvenWhenBothAreHeld() {
        Set<Integer> pressed = new HashSet<>();
        KeyboardHandler keyboard = new KeyboardHandler(pressed::contains, key -> false);
        AtomicInteger calls = new AtomicInteger();
        keyboard.bindHeld(calls::incrementAndGet, 1, 2);
        pressed.add(1);
        keyboard.handleInput();
        pressed.clear();
        pressed.add(2);
        keyboard.handleInput();
        pressed.add(1);
        keyboard.handleInput();
        assertEquals(3, calls.get());
    }

    @Test
    public void heldActionsRepeatEveryFrameButJustPressedActionsDoNot() {
        Set<Integer> justPressed = new HashSet<>(Arrays.asList(1));
        KeyboardHandler keyboard = new KeyboardHandler(key -> key == 1, justPressed::contains);
        AtomicInteger heldCalls = new AtomicInteger();
        AtomicInteger singlePressCalls = new AtomicInteger();
        keyboard.bindHeld(heldCalls::incrementAndGet, 1);
        keyboard.bindJustPressed(singlePressCalls::incrementAndGet, 1);
        keyboard.handleInput();
        justPressed.clear();
        keyboard.handleInput();
        assertEquals(2, heldCalls.get());
        assertEquals(1, singlePressCalls.get());
        justPressed.add(1);
        keyboard.handleInput();
        assertEquals(2, singlePressCalls.get());
    }

    @Test
    public void supportsAdditionalActionsAndPreservesRegistrationOrder() {
        KeyboardHandler keyboard = new KeyboardHandler(key -> true, key -> true);
        List<String> actions = new ArrayList<>();
        keyboard.bindHeld(() -> actions.add("move"), 1);
        keyboard.bindJustPressed(() -> actions.add("shoot"), 2);
        keyboard.handleInput();
        assertEquals(Arrays.asList("move", "shoot"), actions);
    }

    @Test
    public void copiesKeyBindings() {
        KeyboardHandler keyboard = new KeyboardHandler(key -> key == 1, key -> false);
        AtomicInteger calls = new AtomicInteger();
        int[] keys = {1};
        keyboard.bindHeld(calls::incrementAndGet, keys);
        keys[0] = 2;
        keyboard.handleInput();
        assertEquals(1, calls.get());
    }

    @Test
    public void rejectsBindingsWithoutKeysOrActions() {
        KeyboardHandler keyboard = new KeyboardHandler(key -> false, key -> false);
        assertThrows(IllegalArgumentException.class, () -> keyboard.bindHeld(() -> { }));
        assertThrows(IllegalArgumentException.class, () -> keyboard.bindJustPressed(() -> { }));
        assertThrows(NullPointerException.class, () -> keyboard.bindHeld(null, 1));
    }
}
