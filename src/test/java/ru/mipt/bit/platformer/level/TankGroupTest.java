package ru.mipt.bit.platformer.level;

import org.junit.Test;
import ru.mipt.bit.platformer.model.Tank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TankGroupTest {
    @Test
    public void keepsPlayerSeparateFromBotsAndCopiesCollection() {
        Tank player = new Tank(tile -> true, 0, 0, 0.4f);
        Tank bot = new Tank(tile -> true, 1, 0, 0.4f);
        List<Tank> bots = new ArrayList<>();
        bots.add(bot);
        TankGroup group = new TankGroup(player, bots);
        bots.clear();
        assertSame(player, group.getPlayer());
        assertEquals(Arrays.asList(bot), group.getBots());
        assertEquals(Arrays.asList(player, bot), group.getTanks());
    }

    @Test
    public void callersCannotRemoveRegisteredTanks() {
        Tank player = new Tank(tile -> true, 0, 0, 0.4f);
        TankGroup group = new TankGroup(player, new ArrayList<>());
        assertThrows(UnsupportedOperationException.class, () -> group.getBots().clear());
        assertThrows(UnsupportedOperationException.class, () -> group.getTanks().clear());
    }
}
