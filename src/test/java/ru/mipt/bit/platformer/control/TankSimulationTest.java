package ru.mipt.bit.platformer.control;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.Test;
import ru.mipt.bit.platformer.command.Command;
import ru.mipt.bit.platformer.command.MoveCommand;
import ru.mipt.bit.platformer.game.GameLoop;
import ru.mipt.bit.platformer.game.GameView;
import ru.mipt.bit.platformer.level.LevelLayout;
import ru.mipt.bit.platformer.level.TankGroup;
import ru.mipt.bit.platformer.level.TankSpawner;
import ru.mipt.bit.platformer.model.GameSession;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

public class TankSimulationTest {
    @Test
    public void botsMoveWithoutCollisionsWhilePlayerWaitsForKeyboard() {
        LevelLayout layout = new LevelLayout(8, 6,
                Arrays.asList(new Tree(2, 2), new Tree(5, 3)), new GridPoint2(0, 0));
        TankGroup group = new TankSpawner(new Random(77)).spawn(layout, 3, 0.4f);
        List<TankController> controllers = new ArrayList<>();
        controllers.add(new TankController(group.getPlayer(), new KeyboardControlStrategy(key -> false)));
        List<GridPoint2> initialBotPositions = new ArrayList<>();
        for (int index = 0; index < group.getBots().size(); index++) {
            Tank bot = group.getBots().get(index);
            initialBotPositions.add(bot.getTilePosition());
            Random random = new Random(100 + index);
            controllers.add(new TankController(bot, tank -> tank.isMoving() ? Collections.emptyList()
                    : Collections.singletonList(new MoveCommand(tank,
                    Direction.values()[random.nextInt(Direction.values().length)]))));
        }
        GameLoop loop = new GameLoop(() -> {
            List<Command> commands = new ArrayList<>();
            for (TankController controller : controllers) {
                commands.addAll(controller.getCommands());
            }
            return commands;
        }, new GameSession(group.getField()), new GameView() {
                    @Override
                    public void render() {
                    }

                    @Override
                    public void dispose() {
                    }
                });
        Set<Tank> movedBots = new HashSet<>();
        for (int frame = 0; frame < 1000; frame++) {
            loop.update(0.05f);
            assertNoCollisions(layout, group);
            for (int index = 0; index < group.getBots().size(); index++) {
                Tank bot = group.getBots().get(index);
                if (!bot.getTilePosition().equals(initialBotPositions.get(index))) {
                    movedBots.add(bot);
                }
            }
        }
        assertEquals(3, movedBots.size());
        assertEquals(layout.getPlayerStart(), group.getPlayer().getTilePosition());
    }

    private void assertNoCollisions(LevelLayout layout, TankGroup group) {
        Set<GridPoint2> occupied = new HashSet<>();
        for (Tree tree : layout.getTrees()) {
            occupied.add(tree.getTilePosition());
        }
        for (Tank tank : group.getTanks()) {
            GridPoint2 position = tank.getTilePosition();
            GridPoint2 destination = tank.getDestinationTile();
            assertInsideLevel(layout, position);
            assertInsideLevel(layout, destination);
            assertTrue("Occupied source: " + position, occupied.add(position));
            if (!destination.equals(position)) {
                assertTrue("Occupied destination: " + destination, occupied.add(destination));
            }
        }
    }

    private void assertInsideLevel(LevelLayout layout, GridPoint2 position) {
        assertTrue(position.x >= 0 && position.x < layout.getWidth());
        assertTrue(position.y >= 0 && position.y < layout.getHeight());
    }
}
