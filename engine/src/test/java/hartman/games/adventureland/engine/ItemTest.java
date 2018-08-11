package hartman.games.adventureland.engine;

import org.junit.Test;

public class ItemTest {

    @Test(expected = NullPointerException.class)
    public void itemThrowsExceptionWithoutName() {
        new Item(null, null, false, Room.NOWHERE);
    }

    @Test(expected = NullPointerException.class)
    public void itemBuilderThrowsExceptionWithoutName() {
        new Item.Builder().build();
    }

}