package hartman.games.adventureland.engine;

import org.junit.Test;

import static hartman.games.adventureland.engine.Verb.ANY;
import static hartman.games.adventureland.engine.Verb.NONE;
import static hartman.games.adventureland.engine.Verb.UNRECOGNIZED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VerbTest {

    @Test
    public void verbEqualityIsCaseInsensitive() {
        Verb swim = new Verb("swim", "s");
        assertTrue(swim.equals(new Verb("swim")));
        assertTrue(new Verb("swim").equals(swim));
        assertTrue(swim.equals(new Verb("SWIM")));
        assertTrue(new Verb("SWIM").equals(swim));
    }

    @Test
    public void synonymousVerbsShouldMatch() {
        Verb swim = new Verb("swim", "s");

        assertFalse(swim.equals(new Verb("s")));

        assertTrue(swim.matches(new Verb("s")));
        assertTrue(swim.matches(new Verb("swim")));

        assertFalse(swim.matches(new Verb("jump")));

        assertTrue(swim.matches(ANY));
        assertTrue(ANY.matches(swim));

        assertFalse(swim.matches(NONE));
        assertFalse(NONE.matches(swim));
    }

    @Test
    public void anyEqualsAnyVerb() {
        assertTrue(ANY.equals(ANY));

        assertTrue(ANY.matches(ANY));

        assertTrue(ANY.matches(new Verb("fall")));
        assertTrue(ANY.matches(new Verb("jump", "leap", "spring")));

        assertFalse(ANY.equals(new Verb("ANY")));
        assertFalse(new Verb("ANY").equals(ANY));

        assertTrue(new Verb("ANY").matches(ANY));

        assertFalse(ANY.equals(UNRECOGNIZED));
        assertFalse(UNRECOGNIZED.equals(ANY));

        assertTrue(ANY.matches(UNRECOGNIZED));
        assertTrue(UNRECOGNIZED.matches(ANY));

        assertFalse(ANY.equals(NONE));
        assertFalse(ANY.matches(NONE));
    }

    @Test
    public void noneEqualsNoVerb() {
        assertTrue(NONE.equals(NONE));
        assertTrue(NONE.matches(NONE));

        assertFalse(NONE.equals(ANY));
        assertFalse(NONE.matches(ANY));

        assertFalse(NONE.equals(new Verb("NONE")));
        assertFalse(NONE.matches(new Verb("NONE")));

        assertFalse(NONE.equals(new Verb("poke")));
        assertFalse(NONE.matches(new Verb("poke")));

        assertFalse(new Verb("poke").matches(NONE));
        assertFalse(new Verb("poke").equals(NONE));
    }
}