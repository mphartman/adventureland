package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Items;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    public void getShouldUpdateInventoryWhenItemIsPortable() {
        Items.ItemSet itemSet = Items.newItemSet();
        Item icecreamCone = itemSet.newItem().named("cone").build();

        assertFalse(icecreamCone.isCarried());

        GameState gameState = new GameState(Room.NOWHERE);
        gameState.putInInventory(icecreamCone);

        assertFalse("ice cream cone wasn't registered with gamestate", icecreamCone.isCarried());

        gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        gameState.putInInventory(icecreamCone);
        assertFalse("ice cream cone isn't portable", icecreamCone.isCarried());

        Item cup = itemSet.newItem().named("cup").portable().build();
        assertFalse(cup.isCarried());
        gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        gameState.putInInventory(cup);
        assertTrue(cup.isCarried());
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
    public void dropShouldPlaceItemInCurrentRoom() {
        Room conferenceRoom = new Room("conferenceRoom", "A brightly lit corporate conference room");

        Items.ItemSet itemSet = Items.newItemSet();
        Item marker = itemSet.newItem().named("marker").in(conferenceRoom).portable().build();

        assertFalse(marker.isCarried());
        assertTrue(marker.isHere(conferenceRoom));

        GameState gameState = new GameState(conferenceRoom, itemSet.copyOfItems());
        gameState.drop(marker);
        assertTrue("marker should already be here", marker.isHere(conferenceRoom));

        Item wallet = itemSet.newItem().named("wallet").inInventory().build();
        gameState = new GameState(conferenceRoom /* no items on purpose */);
        gameState.drop(wallet);
        assertFalse("item missing from gamestate", wallet.isHere(conferenceRoom));

        gameState = new GameState(conferenceRoom, itemSet.copyOfItems());
        gameState.drop(wallet);
        assertTrue(wallet.isHere(conferenceRoom));

        Item carrot = itemSet.newItem().named("carrot").build();
        gameState = new GameState(conferenceRoom, itemSet.copyOfItems());
        gameState.drop(carrot);
        assertTrue(carrot.isHere(conferenceRoom));
    }

    @Test
    public void destroyShouldRemoveItemFromGameState() {
        Items.ItemSet itemSet = Items.newItemSet();
        Item hammer = itemSet.newItem().named("hammer").build();

        GameState gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());

        assertTrue(gameState.exists(hammer));
        gameState.destroy(hammer);
        assertFalse(gameState.exists(hammer));
        assertTrue(hammer.isDestroyed());
    }
}
