package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import ru.mipt.bit.platformer.graphics.GameRenderer;
import ru.mipt.bit.platformer.input.KeyboardHandler;
import ru.mipt.bit.platformer.input.PlayerControls;

public final class TankGame extends ApplicationAdapter {
    private GameSession session;
    private KeyboardHandler keyboard;
    private GameRenderer renderer;

    @Override
    public void create() {
        session = new GameSession();
        keyboard = new KeyboardHandler(Gdx.input::isKeyPressed, Gdx.input::isKeyJustPressed);
        PlayerControls.bindMovement(keyboard, session.getPlayer());
        renderer = new GameRenderer(session);
    }

    @Override
    public void render() {
        keyboard.handleInput();
        session.update(Gdx.graphics.getDeltaTime());
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
