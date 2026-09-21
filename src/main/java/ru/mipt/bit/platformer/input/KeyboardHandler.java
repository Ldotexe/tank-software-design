package ru.mipt.bit.platformer.input;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntPredicate;

public final class KeyboardHandler {
    private final IntPredicate isKeyPressed;
    private final IntPredicate isKeyJustPressed;
    private final List<Runnable> bindings = new ArrayList<>();

    public KeyboardHandler(IntPredicate isKeyPressed, IntPredicate isKeyJustPressed) {
        this.isKeyPressed = Objects.requireNonNull(isKeyPressed);
        this.isKeyJustPressed = Objects.requireNonNull(isKeyJustPressed);
    }

    public void bindHeld(Runnable action, int... alternativeKeys) {
        register(isKeyPressed, action, alternativeKeys);
    }

    public void bindJustPressed(Runnable action, int... alternativeKeys) {
        register(isKeyJustPressed, action, alternativeKeys);
    }

    private void register(IntPredicate keyState, Runnable action, int[] alternativeKeys) {
        Objects.requireNonNull(action);
        if (alternativeKeys.length == 0) {
            throw new IllegalArgumentException("A binding must have at least one key");
        }
        int[] keys = alternativeKeys.clone();
        bindings.add(() -> {
            for (int key : keys) {
                if (keyState.test(key)) {
                    action.run();
                    break;
                }
            }
        });
    }

    public void handleInput() {
        for (Runnable binding : bindings) {
            binding.run();
        }
    }
}
