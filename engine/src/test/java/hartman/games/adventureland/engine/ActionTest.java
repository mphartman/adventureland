package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Nouns;
import org.junit.Test;

import static hartman.games.adventureland.engine.Vocabulary.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ActionTest {

    @Test
    public void shouldApplyResultWhenVerbMatches() {
        GameState gameState = new GameState(new Player("Archie"), Room.NOWHERE);

        Action action = new Action(new Verb("SLAP"), ((pc, gs) -> gs.setFlag("MESSAGE", "Ouch! That hurts!")));

        action.run(new PlayerCommand(new Verb("SHOUT"), Nouns.ANY), gameState);
        assertNull(gameState.getFlag("MESSAGE"));

        action.run(new PlayerCommand(new Verb("SLAP"), Nouns.ANY), gameState);
        assertEquals("Ouch! That hurts!", gameState.getFlag("MESSAGE"));
    }
}
