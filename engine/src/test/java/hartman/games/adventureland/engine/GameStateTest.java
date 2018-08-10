package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Items;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GameStateTest {

    @Test
    public void exitTowardsShouldUpdateCurrentRoomGivenValidExit() {
        Room start = new Room("start", "I am here.");
        Room end = new Room("end", "I am here now.");
        start.setExit(new Noun("LEFT"), end);

        GameState gameState = new GameState(start);
        Room former = gameState.exitTowards(new Noun("LEFT"));

        assertEquals(former, start);
        assertEquals(end, gameState.getCurrentRoom());
    }

    @Test
    public void describeShouldVisitRoomAndItems() {
        Room forest = new Room("forest", "I'm in a lush, green forest.");

        Items.ItemSet itemSet = Items.newItemSet();
        Item tree = itemSet.newItem().named("tree").alias("Tim").describedAs("An American Sycamore tree.").in(forest).build();
        Item key = itemSet.newItem().named("key").describedAs("A skeleton key.").build();

        Set<Item> items = itemSet.copyOfItems();

        GameState gameState = new GameState(forest, items);

        AtomicReference<Room> roomRef = new AtomicReference<>();
        AtomicReference<Item> itemRef = new AtomicReference<>();
        gameState.describe(new GameElementVisitor() {
            @Override
            public void visit(Item item) {
                assertEquals(tree, item); // checks that we don't see key Item
                itemRef.set(item);
            }

            @Override
            public void visit(Room room) {
                roomRef.set(room);
            }

            @Override
            public void visit(Room.Exit exit) {
                fail("There are no exits. Should not have visited any.");
            }
        });
        assertEquals(forest, roomRef.get());
        assertEquals(tree, itemRef.get());
    }

    @Test
    public void inventoryShouldVisitItems() {
        Items.ItemSet itemSet = Items.newItemSet();
        Item tree = itemSet.newItem().named("tree").alias("Tim").describedAs("An American Sycamore tree.").build();
        Item key = itemSet.newItem().named("key").describedAs("A skeleton key.").build();
        Item sandwich = itemSet.newItem().named("sandwich").describedAs("A ham and cheese sandwich on rye bread.").inInventory().build();
        Set<Item> items = itemSet.copyOfItems();

        GameState gameState = new GameState(Room.NOWHERE, items);

        List<Item> itemList = new ArrayList<>();
        gameState.inventory(new GameElementVisitor() {
            @Override
            public void visit(Item item) {
                assertEquals(sandwich, item); // checks that we only see carried items
                itemList.add(item);
            }

            @Override
            public void visit(Room room) {
                fail("Inventory should not visit rooms");
            }

            @Override
            public void visit(Room.Exit exit) {
                fail("There are no exits. Should not have visited any.");
            }
        });
        assertEquals(1, itemList.size());
        assertEquals(sandwich, itemList.get(0));
    }

    @Test
    public void dropShouldPlaceCarriedItemInCurrentRoom() {
        // TODO
    }

    @Test
    public void getShouldPlaceItemInInventory() {
        // TODO
    }
}
