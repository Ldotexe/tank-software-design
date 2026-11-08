package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import org.junit.Test;
import ru.mipt.bit.platformer.model.BulletSettings;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;

import static org.junit.Assert.*;

public class HealthBarLayoutTest {
    @Test
    public void barStaysInsideEveryCornerTile() {
        TiledMapTileLayer tiles = new TiledMapTileLayer(5, 3, 128, 64);
        HealthBarLayout layout = new HealthBarLayout(tiles, Interpolation.smooth);
        for (int x : new int[]{0, 4}) {
            for (int y : new int[]{0, 2}) {
                Rectangle bar = layout.boundsFor(new Tank(tile -> true, x, y, 0.4f));

                assertTrue(bar.x >= x * tiles.getTileWidth());
                assertTrue(bar.y >= y * tiles.getTileHeight());
                assertTrue(bar.x + bar.width <= (x + 1) * tiles.getTileWidth());
                assertTrue(bar.y + bar.height <= (y + 1) * tiles.getTileHeight());
                assertEquals((x + 0.5f) * tiles.getTileWidth(), bar.x + bar.width / 2f, 0.0001f);
            }
        }
    }

    @Test
    public void followsTankWithTheSameInterpolationInEveryDirection() {
        TiledMapTileLayer tiles = new TiledMapTileLayer(5, 5, 128, 64);
        for (Direction direction : Direction.values()) {
            HealthBarLayout layout = new HealthBarLayout(tiles, Interpolation.smooth);
            Tank tank = new Tank(tile -> true, 2, 2, 0.4f);
            Rectangle start = new Rectangle(layout.boundsFor(tank));
            tank.tryStartMoving(direction);
            tank.update(0.1f);

            Rectangle moving = layout.boundsFor(tank);
            float progress = Interpolation.smooth.apply(tank.getMovementProgress());
            int deltaX = tank.getDestinationTile().x - tank.getTilePosition().x;
            int deltaY = tank.getDestinationTile().y - tank.getTilePosition().y;

            assertEquals(start.x + deltaX * tiles.getTileWidth() * progress, moving.x, 0.0001f);
            assertEquals(start.y + deltaY * tiles.getTileHeight() * progress, moving.y, 0.0001f);
        }
    }

    @Test
    public void repeatedFramesDoNotAccumulateTheVerticalOffset() {
        HealthBarLayout layout = new HealthBarLayout(new TiledMapTileLayer(5, 3, 128, 128),
                Interpolation.smooth);
        Tank tank = new Tank(tile -> true, 2, 1, 0.4f);
        Rectangle first = new Rectangle(layout.boundsFor(tank));

        for (int frame = 0; frame < 10; frame++) {
            Rectangle next = layout.boundsFor(tank);
            assertEquals(first.x, next.x, 0f);
            assertEquals(first.y, next.y, 0f);
        }
    }

    @Test
    public void fillingUsesInitialHealthAsItsMaximum() {
        HealthBarLayout layout = new HealthBarLayout(new TiledMapTileLayer(5, 3, 128, 128),
                Interpolation.smooth);
        Tank tank = new Tank(tile -> true, 0, 0, 0.4f, 80, BulletSettings.DEFAULT);
        assertEquals(1f, layout.healthFraction(tank), 0f);

        tank.takeDamage(20);
        assertEquals(0.75f, layout.healthFraction(tank), 0f);
        tank.takeDamage(60);
        assertEquals(0f, layout.healthFraction(tank), 0f);
        tank.takeDamage(1);
        assertEquals(0f, layout.healthFraction(tank), 0f);
    }

    @Test
    public void zeroInitialHealthProducesAnEmptyFiniteBar() {
        HealthBarLayout layout = new HealthBarLayout(new TiledMapTileLayer(5, 3, 128, 128),
                Interpolation.smooth);
        Tank tank = new Tank(tile -> true, 0, 0, 0.4f, 0, BulletSettings.DEFAULT);

        assertEquals(0f, layout.healthFraction(tank), 0f);
        assertTrue(Float.isFinite(layout.healthFraction(tank)));
    }
}
