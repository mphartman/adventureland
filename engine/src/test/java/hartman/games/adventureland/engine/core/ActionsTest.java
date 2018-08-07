package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.*;
import org.junit.Test;

import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Nouns.UP;
import static hartman.games.adventureland.engine.core.Verbs.GO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ActionsTest {

    @Test
    public void goActionShouldMovePlayerToRoom() {
        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");
        Room chamber = new Room("chamber", "A small, round chamber with stone walls and floor.");
        chamber.setExit(DOWN, dungeon);

        GameState gameState = new GameState(chamber);
        Command command = new Command(GO, DOWN);

        Actions.GO_ACTION.run(new ActionContext(gameState, msg -> {}, command));

        assertEquals(dungeon, gameState.getCurrentRoom());
    }

    @Test
    public void goActionShouldNotFailGivenAnInvalidExit() {
        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");
        dungeon.setExit(DOWN, dungeon);

        GameState gameState = new GameState(dungeon);
        Command command = new Command(GO, UP);

        Actions.GO_ACTION.run(new ActionContext(gameState, msg -> {}, command));

        assertEquals(dungeon, gameState.getCurrentRoom());
    }

    @Test
    public void quitActionShouldQuitGivenQuitVerb() {
        GameState gameState = new GameState(Room.NOWHERE);
        assertTrue(gameState.isRunning());
        Actions.QUIT_ACTION.run(new ActionContext(gameState, msg -> {}, new Command(new Verb("stop"), Noun.NONE)));
        assertTrue(gameState.isRunning());
        Actions.QUIT_ACTION.run(new ActionContext(gameState, msg -> {}, new Command(Verbs.QUIT, Noun.NONE)));
        assertFalse(gameState.isRunning());
    }

}
