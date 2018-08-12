package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Nouns;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ActionTest {

    @Test
    public void shouldApplyResultWhenVerbMatches() {
        GameState gameState = new GameState(Room.NOWHERE);

        Action action = new Action.Builder()
                .on(new Verb("SLAP"))
                .then((pc, gs, dsp) -> gs.setFlag("MESSAGE", "Ouch! That hurts!"))
                .build();

        action.run(gameState, msg -> {}, new Command(new Verb("SHOUT"), Noun.NONE));
        assertNull(gameState.getFlag("MESSAGE"));

        action.run(gameState, msg -> {}, new Command(new Verb("SLAP"), Noun.NONE));
        assertEquals("Ouch! That hurts!", gameState.getFlag("MESSAGE"));
    }

    @Test
    public void runShouldReturnTrueGivenCommandMatchingAnyNoun() {
        Action action = new Action.Builder().withNoVerb().withAnyOf(Nouns.NORTH, Nouns.SOUTH, Nouns.EAST, Nouns.WEST).build();
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("n"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("s"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("e"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("w"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("north"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("south"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("east"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("west"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, Nouns.NORTH)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, Nouns.SOUTH)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, Nouns.EAST)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, Nouns.WEST)));
        assertFalse(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("u"))));
        assertFalse(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("d"))));
    }
}
