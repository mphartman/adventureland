package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.ActionContext;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.PlayerCommand;
import hartman.games.adventureland.engine.Room;
import org.junit.Test;

import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Nouns.UP;
import static hartman.games.adventureland.engine.core.Verbs.GO;
import static org.junit.Assert.assertEquals;

public class ActionsTest {

    @Test
    public void goActionShouldMovePlayerToRoom() {
        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");
        Room chamber = new Room("chamber", "A small, round chamber with stone walls and floor.");
        chamber.setExit(DOWN, dungeon);

        GameState gameState = new GameState(chamber);
        PlayerCommand playerCommand = new PlayerCommand(GO, DOWN);

        Actions.GO_ACTION.run(new ActionContext(gameState, msg -> {}, playerCommand));

        assertEquals(dungeon, gameState.getCurrentRoom());
    }

    @Test
    public void goActionShouldNotFailGivenAnInvalidExit() {
        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");
        dungeon.setExit(DOWN, dungeon);

        GameState gameState = new GameState(dungeon);
        PlayerCommand playerCommand = new PlayerCommand(GO, UP);

        Actions.GO_ACTION.run(new ActionContext(gameState, msg -> {}, playerCommand));

        assertEquals(dungeon, gameState.getCurrentRoom());
    }
}
