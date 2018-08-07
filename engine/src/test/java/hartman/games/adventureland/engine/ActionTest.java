package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ActionTest {

    @Test
    public void shouldApplyResultWhenVerbMatches() {
        GameState gameState = new GameState(Room.NOWHERE);

        Action action = new Action(new Verb("SLAP"), ((pc, gs, dsp) -> gs.setFlag("MESSAGE", "Ouch! That hurts!")));

        Display noop = msg -> {};

        action.run(new ActionContext(gameState, noop, new PlayerCommand(new Verb("SHOUT"), Noun.NONE)));
        assertNull(gameState.getFlag("MESSAGE"));

        action.run(new ActionContext(gameState, noop, new PlayerCommand(new Verb("SLAP"), Noun.NONE)));
        assertEquals("Ouch! That hurts!", gameState.getFlag("MESSAGE"));
    }
}
