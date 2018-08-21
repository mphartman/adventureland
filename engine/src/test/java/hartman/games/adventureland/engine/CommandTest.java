package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

}