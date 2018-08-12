package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Item;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ItemsTest {

    @Test
    public void itemSetContainsItemsBuiltUsingIt() {
        Items itemSet = Items.newItemSet();
        Item mug = itemSet.newItem().named("mug").build();
        Item mouse = itemSet.newItem().named("mouse").build();
        Set<Item> items = itemSet.copyOfItems();
        assertTrue(items.contains(mug));
        assertTrue(items.contains(mouse));
    }
}
