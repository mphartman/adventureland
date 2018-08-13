package hartman.games.adventureland.engine;

import org.junit.Test;

import static hartman.games.adventureland.engine.Noun.ANY;
import static hartman.games.adventureland.engine.Noun.NONE;
import static hartman.games.adventureland.engine.Noun.UNRECOGNIZED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NounTest {

    @Test
    public void nounEqualityIsCaseSensitive() {
        Noun north = new Noun("north", "n");
        assertTrue(north.equals(new Noun("north")));
        assertTrue(new Noun("north").equals(north));
        assertFalse(north.equals(new Noun("NORTH")));
        assertFalse(new Noun("NORTH").equals(north));
    }

    @Test
    public void synonymousNounsShouldMatch() {
        Noun north = new Noun("north", "n");

        assertFalse(north.equals(new Noun("n")));

        assertTrue(north.matches(new Noun("n")));
        assertTrue(north.matches(new Noun("north")));
        assertTrue(new Noun("n").matches(north));
        assertTrue(new Noun("north").matches(north));
        assertTrue(new Noun("north", "n").matches(north));
        assertTrue(new Noun("n", "north").matches(north));
        assertTrue(new Noun("NORTH", "N").matches(north));

        assertFalse(north.matches(new Noun("west")));

        assertTrue(north.matches(ANY));
        assertTrue(ANY.matches(north));

        assertFalse(north.matches(NONE));
        assertFalse(NONE.matches(north));
    }

    @Test
    public void anyEqualsAnyNoun() {
        assertTrue(ANY.equals(ANY));

        assertTrue(ANY.matches(ANY));

        assertTrue(ANY.matches(new Noun("crown")));
        assertTrue(ANY.matches(new Noun("hat", "cap", "crown")));

        assertFalse(ANY.equals(new Noun("ANY")));
        assertFalse(new Noun("ANY").equals(ANY));

        assertTrue(new Noun("ANY").matches(ANY));

        assertFalse(ANY.equals(UNRECOGNIZED));
        assertFalse(UNRECOGNIZED.equals(ANY));

        assertTrue(ANY.matches(UNRECOGNIZED));
        assertTrue(UNRECOGNIZED.matches(ANY));

        assertFalse(ANY.equals(NONE));
        assertFalse(ANY.matches(NONE));
    }

    @Test
    public void noneEqualsNoNoun() {
        assertTrue(NONE.equals(NONE));
        assertTrue(NONE.matches(NONE));

        assertFalse(NONE.equals(ANY));
        assertFalse(NONE.matches(ANY));

        assertFalse(NONE.equals(new Noun("NONE")));
        assertFalse(NONE.matches(new Noun("NONE")));

        assertFalse(NONE.equals(new Noun("ring")));
        assertFalse(NONE.matches(new Noun("ring")));

        assertFalse(new Noun("ring").matches(NONE));
        assertFalse(new Noun("ring").equals(NONE));
    }
}