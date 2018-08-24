package hartman.games.adventureland.engine;

import org.junit.Test;

import static hartman.games.adventureland.engine.Word.ANY;
import static hartman.games.adventureland.engine.Word.NONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class WordTest {

    private Word UnrecognizedWord = Word.unrecognized("poppycock");

    @Test
    public void nounEqualityIsCaseSensitive() {
        Word north = new Word("north", "n");
        assertEquals(north, new Word("north"));
        assertEquals(new Word("north"), north);
        assertNotEquals(north, new Word("NORTH"));
        assertNotEquals(new Word("NORTH"), north);
    }

    @Test
    public void synonymousNounsShouldMatch() {
        Word north = new Word("north", "n");

        assertNotEquals(north, new Word("n"));

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
    public void anyMatchesAnyWord() {

        assertTrue(ANY.matches(ANY));

        assertTrue(ANY.matches(new Word("crown")));
        assertTrue(ANY.matches(new Word("hat", "cap", "crown")));

        assertNotEquals(ANY, new Word("ANY"));
        assertNotEquals(new Word("ANY"), ANY);

        assertTrue(new Word("ANY").matches(ANY));

        assertNotEquals(ANY, UnrecognizedWord);
        assertNotEquals(UnrecognizedWord, ANY);

        assertTrue(ANY.matches(UnrecognizedWord));
        assertTrue(UnrecognizedWord.matches(ANY));

        assertNotEquals(ANY, NONE);
        assertFalse(ANY.matches(NONE));
    }

    @Test
    public void noneDoesNotMatchAnything() {

        assertTrue(NONE.matches(NONE));

        assertNotEquals(NONE, ANY);
        assertFalse(NONE.matches(ANY));

        assertNotEquals(NONE, new Word("NONE"));
        assertFalse(NONE.matches(new Word("NONE")));

        assertNotEquals(NONE, new Word("ring"));
        assertFalse(NONE.matches(new Word("ring")));

        assertNotEquals(new Word("ring"), NONE);
        assertFalse(new Word("ring").matches(NONE));
    }

    @Test
    public void anyAndNone() {
        Word something = new Word("something)");
        assertTrue(NONE.matches(NONE));
        assertFalse(NONE.matches(ANY));
        assertFalse(ANY.matches(NONE));
        assertTrue(ANY.matches(ANY));
        assertTrue(ANY.matches(UnrecognizedWord));
        assertTrue(UnrecognizedWord.matches(ANY));
        assertFalse(NONE.matches(UnrecognizedWord));
        assertFalse(UnrecognizedWord.matches(NONE));
        assertTrue(UnrecognizedWord.matches(UnrecognizedWord));
        assertFalse(something.matches(NONE));
        assertFalse(NONE.matches(something));
        assertTrue(ANY.matches(something));
        assertTrue(something.matches(ANY));
        assertFalse(UnrecognizedWord.matches(something));
        assertFalse(something.matches(UnrecognizedWord));
    }
}