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
        start.setExit(new Word("LEFT"), end);

        GameState gameState = new GameState(start);
        Room former = gameState.exitTowards(new Word("LEFT"));

        assertEquals(former, start);
        assertEquals(end, gameState.getCurrentRoom());
    }

    @Test
    public void putInInventoryShouldUpdateInventoryWhenItemIsPortable() {
        Items itemSet = Items.newItemSet();
        Item cup = itemSet.newItem().named("cup").alias("glass").build();
        assertFalse(cup.isCarried());

        GameState gameState = new GameState(Room.NOWHERE);
        gameState.putInInventory(cup);
        assertFalse("cup wasn't registered with game state", cup.isCarried());

        gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        gameState.putInInventory(cup);
        assertFalse("cup isn't portable", cup.isCarried());

        Item bowl = itemSet.newItem().named("bowl").portable().build();
        assertFalse(bowl.isCarried());

        gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        gameState.putInInventory(bowl);
        assertTrue(bowl.isCarried());

        Item dog = itemSet.newItem().named("dog").alias("archie").portable().build();
        gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        gameState.putInInventory(new Word("archie"));
        assertTrue(dog.isCarried());

        AtomicReference<Item> dogInInventory = new AtomicReference<>();
        gameState.inventory(new GameElementVisitor() {
            @Override
            public void visit(Item item) {
                dogInInventory.set(item);
            }

            @Override
            public void visit(Room room) {

            }

            @Override
            public void visit(Room.Exit exit) {

            }
        });
        assertTrue(dog.matches(dogInInventory.get()));
    }

    @Test
    public void describeShouldVisitRoomAndItems() {
        Room forest = new Room("forest", "I'm in a lush, green forest.");

        Items itemSet = Items.newItemSet();
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
        Items itemSet = Items.newItemSet();
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

        Items itemSet = Items.newItemSet();
        Item marker = itemSet.newItem().named("marker").in(conferenceRoom).portable().build();

        assertFalse(marker.isCarried());
        assertTrue(marker.isHere(conferenceRoom));

        GameState gameState;

        gameState = new GameState(conferenceRoom, itemSet.copyOfItems());
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

        Item candy = itemSet.newItem().named("pez").alias("candy").build();
        gameState = new GameState(conferenceRoom, itemSet.copyOfItems());
        gameState.drop(new Word("candy"));
        assertTrue(candy.isHere(conferenceRoom));

        AtomicReference<Item> candyInRoom = new AtomicReference<>();
        gameState.describe(new GameElementVisitor() {
            @Override
            public void visit(Item item) {
                candyInRoom.set(item);
            }

            @Override
            public void visit(Room room) {

            }

            @Override
            public void visit(Room.Exit exit) {

            }
        });
        assertTrue(candy.matches(candyInRoom.get()));

    }

    @Test
    public void destroyShouldRemoveItemFromGameState() {
        Room shed = new Room("shed", "A rickety old tool shed.");
        Items itemSet = Items.newItemSet();
        Item hammer = itemSet.newItem().named("hammer").in(shed).build();

        GameState gameState;

        gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        assertTrue(gameState.exists(hammer));

        gameState.destroy(hammer);
        assertFalse(gameState.exists(hammer));
        assertTrue(hammer.isDestroyed());

        Item screwdriver = itemSet.newItem().named("screwdriver").alias("flathead screwdriver").in(shed).build();
        gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        assertTrue(gameState.exists(new Word("flathead screwdriver")));

        gameState.destroy(new Word("flathead screwdriver"));
        assertFalse(gameState.exists(new Word("flathead screwdriver")));
        assertTrue(screwdriver.isDestroyed());

    }
}
