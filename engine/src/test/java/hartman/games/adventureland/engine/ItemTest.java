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

    @Test(expected = IllegalStateException.class)
    public void dropThrowsExceptionWhenItemIsDestroyed() {
        Item ribbon = new Item.Builder().named("ribbon").build();
        ribbon.destroy();
        ribbon.drop(Room.NOWHERE);
    }

    @Test(expected = IllegalStateException.class)
    public void stowThrowsExceptionWhenItemIsDestroyed() {
        Item dongle = new Item.Builder().named("dongle").build();
        dongle.destroy();
        dongle.stow();
    }
}