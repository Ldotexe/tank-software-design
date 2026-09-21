package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameSessionTest {
    @Test
    public void initializesPlayerAndTreeInOriginalPositions() {
        GameSession session = new GameSession();
        assertEquals(new GridPoint2(1, 1), session.getPlayer().getTilePosition());
        assertEquals(1, session.getField().getTrees().size());
        assertFalse(session.getField().isTileUnoccupied(new GridPoint2(1, 3)));
    }

    @Test
    public void updatesPlayerAndUsesSameFieldForCollisions() {
        GameSession session = new GameSession();
        Tank player = session.getPlayer();
        player.tryStartMoving(Direction.UP);
        session.update(Tank.TILE_MOVEMENT_DURATION_SECONDS);
        assertEquals(new GridPoint2(1, 2), player.getTilePosition());
        player.tryStartMoving(Direction.UP);
        session.update(Tank.TILE_MOVEMENT_DURATION_SECONDS);
        assertEquals(new GridPoint2(1, 2), player.getTilePosition());
        assertFalse(player.isMoving());
    }

    @Test
    public void sessionsDoNotShareMutableState() {
        GameSession first = new GameSession();
        GameSession second = new GameSession();
        first.getField().addTree(5, 5);
        first.getPlayer().tryStartMoving(Direction.RIGHT);
        first.update(Tank.TILE_MOVEMENT_DURATION_SECONDS);
        assertTrue(second.getField().isTileUnoccupied(new GridPoint2(5, 5)));
        assertEquals(new GridPoint2(1, 1), second.getPlayer().getTilePosition());
    }
}
