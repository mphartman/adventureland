package hartman.games.adventureland.engine;

import org.junit.Test;

import static hartman.games.adventureland.engine.Word.ANY;
import static hartman.games.adventureland.engine.Word.NONE;
import static hartman.games.adventureland.engine.Word.UNRECOGNIZED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WordTest {

    @Test
    public void nounEqualityIsCaseSensitive() {
        Word north = new Word("north", "n");
        assertTrue(north.equals(new Word("north")));
        assertTrue(new Word("north").equals(north));
        assertFalse(north.equals(new Word("NORTH")));
        assertFalse(new Word("NORTH").equals(north));
    }

    @Test
    public void synonymousNounsShouldMatch() {
        Word north = new Word("north", "n");

        assertFalse(north.equals(new Word("n")));

        assertTrue(north.matches(new Word("n")));
        assertTrue(north.matches(new Word("north")));
        assertTrue(new Word("n").matches(north));
        assertTrue(new Word("north").matches(north));
        assertTrue(new Word("north", "n").matches(north));
        assertTrue(new Word("n", "north").matches(north));
        assertTrue(new Word("NORTH", "N").matches(north));

        assertFalse(north.matches(new Word("west")));

        assertTrue(north.matches(ANY));
        assertTrue(ANY.matches(north));

        assertFalse(north.matches(NONE));
        assertFalse(NONE.matches(north));
    }

    @Test
    public void anyEqualsAnyNoun() {
        assertTrue(ANY.equals(ANY));

        assertTrue(ANY.matches(ANY));

        assertTrue(ANY.matches(new Word("crown")));
        assertTrue(ANY.matches(new Word("hat", "cap", "crown")));

        assertFalse(ANY.equals(new Word("ANY")));
        assertFalse(new Word("ANY").equals(ANY));

        assertTrue(new Word("ANY").matches(ANY));

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

        assertFalse(NONE.equals(new Word("NONE")));
        assertFalse(NONE.matches(new Word("NONE")));

        assertFalse(NONE.equals(new Word("ring")));
        assertFalse(NONE.matches(new Word("ring")));

        assertFalse(new Word("ring").matches(NONE));
        assertFalse(new Word("ring").equals(NONE));
    }
}