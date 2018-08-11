package hartman.games.adventureland.engine;

import org.junit.Test;

import static hartman.games.adventureland.engine.Noun.ANY;
import static hartman.games.adventureland.engine.Noun.NONE;
import static hartman.games.adventureland.engine.Noun.UNRECOGNIZED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NounTest {

    @Test
    public void synonymsShouldEquateToTrue() {
        Noun north = new Noun("north", "n");
        assertTrue(north.equals(new Noun("north")));
        assertTrue(north.equals(new Noun("NORTH")));
        assertTrue(north.equals(new Noun("n")));
        assertFalse(north.equals(new Noun("west")));
        assertTrue(north.equals(ANY));
        assertFalse(north.equals(NONE));
    }

    @Test
    public void anyEqualsAnyNoun() {
        assertTrue(ANY.equals(new Noun("any")));
        assertTrue(ANY.equals(new Noun("crown")));
        assertTrue(ANY.equals(new Noun("hat", "cap", "crown")));
        assertTrue(new Noun("ANY").equals(ANY));
        assertTrue(ANY.equals(ANY));
        assertTrue(ANY.equals(UNRECOGNIZED));
        assertTrue(UNRECOGNIZED.equals(ANY));
        assertFalse(ANY.equals(NONE));
    }

    @Test
    public void noneEqualsNoNoun() {
        assertTrue(NONE.equals(NONE));
        assertFalse(NONE.equals(ANY));
        assertFalse(NONE.equals(new Noun("NONE")));
        assertFalse(NONE.equals(new Noun("Nil", "NONE")));
        assertFalse(NONE.equals(new Noun("ring")));
        assertFalse(new Noun("ring").equals(NONE));
    }
}