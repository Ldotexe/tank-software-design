package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

public interface ObjectRenderer<T> extends Disposable {
    void render(Batch batch, T object);
}
