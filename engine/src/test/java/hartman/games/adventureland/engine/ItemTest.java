package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ItemTest {

    @Test(expected = NullPointerException.class)
    public void itemThrowsExceptionWithoutName() {
        new Item(null, null, false, Room.NOWHERE);
    }

    @Test(expected = NullPointerException.class)
    public void itemBuilderThrowsExceptionWithoutName() {
        new Item.Builder().build();
    }

    @Test
    public void itemShouldMatchNounOfSameName() {
        Item dog = new Item.Builder().named("dog").alias("archie").build();
        assertTrue(dog.matches(new Word("dog")));
        assertTrue(dog.matches(new Word("archie")));
        assertTrue(new Word("dog").matches(dog));
        assertTrue(new Word("archie").matches(dog));
        assertTrue(Word.ANY.matches(dog));
        assertTrue(dog.matches(Word.ANY));
        assertFalse(dog.equals(new Word("dog")));
    }
}