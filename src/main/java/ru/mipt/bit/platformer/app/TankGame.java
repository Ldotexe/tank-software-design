package ru.mipt.bit.platformer.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import ru.mipt.bit.platformer.game.GameLoop;

import java.util.Objects;
import java.util.function.Supplier;

public final class TankGame extends ApplicationAdapter {
    private final Supplier<GameLoop> gameFactory;
    private GameLoop game;

    public TankGame(Supplier<GameLoop> gameFactory) {
        this.gameFactory = Objects.requireNonNull(gameFactory);
    }

    @Override
    public void create() {
        game = gameFactory.get();
    }

    @Override
    public void render() {
        game.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        if (game != null) {
            game.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        game.dispose();
    }
}
