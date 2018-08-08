package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.ActionContext;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Verb;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ActionsTest {

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
