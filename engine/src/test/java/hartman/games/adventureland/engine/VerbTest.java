package hartman.games.adventureland.engine;

import org.junit.Test;

import static hartman.games.adventureland.engine.Verb.ANY;
import static hartman.games.adventureland.engine.Verb.NONE;
import static hartman.games.adventureland.engine.Verb.UNRECOGNIZED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerbTest {

    @Test
    public void synonymsShouldEquateToTrue() {
        Verb go = new Verb("go", "g", "run", "enter", "walk");
        assertTrue(go.equals(new Verb("go")));
        assertTrue(go.equals(new Verb("GO")));
        assertTrue(go.equals(new Verb("g")));
        assertTrue(go.equals(new Verb("run")));
        assertTrue(go.equals(new Verb("enter")));
        assertTrue(go.equals(new Verb("walk")));
        assertFalse(go.equals(new Verb("stroll")));
        assertTrue(go.equals(ANY));
        assertFalse(go.equals(NONE));
    }

    @Test
    public void anyEqualsAnyVerb() {
        assertTrue(ANY.equals(new Verb("any")));
        assertTrue(ANY.equals(new Verb("jump")));
        assertTrue(ANY.equals(new Verb("say", "yell", "scream")));
        assertTrue(new Verb("ANY").equals(ANY));
        assertTrue(ANY.equals(ANY));
        assertTrue(ANY.equals(UNRECOGNIZED));
        assertTrue(UNRECOGNIZED.equals(ANY));
        assertFalse(ANY.equals(NONE));
    }

    @Test
    public void noneEqualsNoVerb() {
        assertTrue(NONE.equals(NONE));
        assertFalse(NONE.equals(ANY));
        assertFalse(NONE.equals(new Verb("NONE")));
        assertFalse(NONE.equals(new Verb("Nil", "NONE")));
        assertFalse(NONE.equals(new Verb("Jump")));
        assertFalse(new Verb("Jump").equals(NONE));
    }
}