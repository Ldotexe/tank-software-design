package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createSpriteBounds;
import static ru.mipt.bit.platformer.util.GdxGameUtils.drawSpriteAtOriginalSize;

public abstract class GameObject implements Disposable {
    private static final float DEFAULT_ROTATION_DEGREES = 0f;

    private final Texture texture;
    private final TextureRegion spriteRegion;
    protected final GridPoint2 tilePosition;
    protected final Rectangle spriteBounds;

    protected GameObject(String texturePath, int tileX, int tileY) {
        texture = new Texture(texturePath);
        spriteRegion = new TextureRegion(texture);
        spriteBounds = createSpriteBounds(spriteRegion);
        tilePosition = new GridPoint2(tileX, tileY);
    }

    protected float getRotationDegrees() {
        return DEFAULT_ROTATION_DEGREES;
    }

    public void render(Batch batch) {
        drawSpriteAtOriginalSize(batch, spriteRegion, spriteBounds, getRotationDegrees());
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
