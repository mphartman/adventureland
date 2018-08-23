package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CommandTest {

    @Test
    public void secondWordIsNoneGivenOnlyOneWord() {
        assertEquals(Word.NONE, new Command(new Word("jam")).getSecondWord());
    }

    @Test
    public void toStringIsPretty() {
        assertEquals("cut tree", new Command(new Word("cut"), new Word("tree")).toString());
    }

    @Test
    public void returnFirstWordIfSecondWordIsNone() {
        assertEquals(new Word("first"), new Command(new Word("first"), Word.NONE).getSecondThenFirst());
        assertEquals(new Word("second"), new Command(new Word("first"), new Word("second")).getSecondThenFirst());
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
        assertEquals(new Word("apple"), command.getFirstWord());
        assertEquals(new Word("banana"), command.getSecondWord());
    }

    @Test
    public void commandEqualityConsidersFullWordListAndIsOrderSensitive() {
        Command command = new Command(new Word("apple"), new Word("banana"), new Word("carrot"), new Word("dog"));
        Command command2 = new Command(new Word("apple"), new Word("banana"), new Word("dog"), new Word("carrot"));
        assertNotEquals(command, command2);
    }

}