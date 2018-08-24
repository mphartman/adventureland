package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CommandTest {

    @Test
    public void secondWordIsNoneGivenOnlyOneWord() {
        assertEquals(Word.NONE, new Command(new Word("jam")).getWordOrNone(2));
    }

    @Test
    public void toStringIsPretty() {
        assertEquals("cut tree", new Command(new Word("cut"), new Word("tree")).toString());
    }

    @Test
    public void commandEquality() {
        Command c1 = Command.NONE;
        Command c2 = new Command(new Word("a"), new Word("b"));
        Command c3 = new Command(new Word("a"), new Word("b"));
        Command c4 = new Command(new Word("a"), new Word("c"));
        Command c5 = new Command(new Word("d"), new Word("b"));

        assertNotEquals(c1, c2);
        assertNotEquals(c2, c1);
        assertEquals(c2, c3);
        assertEquals(c3, c2);
        assertNotEquals(c3, c4);
        assertNotEquals(c4, c3);
        assertNotEquals(c4, c5);
        assertNotEquals(c5, c4);
    }

    @Test
    public void commandAcceptsListOfWords() {
        Command command = new Command(new Word("apple"), new Word("banana"), new Word("carrot"), new Word("dog"));
        assertEquals(new Word("apple"), command.getWordOrNone(1));
        assertEquals(new Word("banana"), command.getWordOrNone(2));
    }

    @Test
    public void commandEqualityConsidersFullWordListAndIsOrderSensitive() {
        Command command = new Command(new Word("apple"), new Word("banana"), new Word("carrot"), new Word("dog"));
        Command command2 = new Command(new Word("apple"), new Word("banana"), new Word("dog"), new Word("carrot"));
        assertNotEquals(command, command2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void commandMustHaveAtLeastOneWord() {
        new Command();
    }

    @Test(expected = IllegalArgumentException.class)
    public void commandMustHaveAtLeastOneNonNullWord() {
        new Command(null, null);
    }

    @Test
    public void commandFiltersOutNullWords() {
        Command command = new Command(null, new Word("river"), null);
        assertTrue(command.getWord(1).orElseThrow(AssertionError::new).matches(new Word("river")));
    }

    @Test
    public void commandWordOrNoneReturnsNoneGivenWordNotFound() {
        Command command = new Command(Word.of("foo"));
        assertEquals(Word.NONE, command.getWordOrNone(2));
    }

    @Test
    public void commandNoneHasNoWords() {
        assertFalse(Command.NONE.getWord(1).isPresent());
        assertFalse(Command.NONE.getWord(2).isPresent());
        assertFalse(Command.NONE.getWord(3).isPresent());
        assertEquals(Word.NONE, Command.NONE.getWordOrNone(1));
        assertEquals(Word.NONE, Command.NONE.getWordOrNone(2));
        assertEquals(Word.NONE, Command.NONE.getWordOrNone(3));
    }

}