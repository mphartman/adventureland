package hartman.games.adventureland.engine;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        GameState gameState;

        Item cup = new Item.Builder().named("cup").alias("glass").build();
        assertFalse(cup.isCarried());
        assertFalse(cup.isPortable());

        gameState = new GameState(Room.NOWHERE);
        gameState.putInInventory(cup);
        assertFalse("cup wasn't registered with game state", cup.isCarried());

        gameState = new GameState(Room.NOWHERE, singleton(cup));
        gameState.putInInventory(cup);
        assertFalse("cup isn't portable", cup.isCarried());

        Item bowl = new Item.Builder().named("bowl").portable().build();
        assertFalse(bowl.isCarried());
        assertTrue(bowl.isPortable());

        gameState = new GameState(Room.NOWHERE, singleton(bowl));
        gameState.putInInventory(bowl);
        assertTrue(bowl.isCarried());

        Item dog = new Item.Builder().named("dog").alias("archie").portable().build();
        gameState = new GameState(Room.NOWHERE, singleton(dog));
        gameState.putInInventory(Item.newItem("archie").build());
        assertTrue("words matching items can be put into inventory", dog.isCarried());
    }

    @Test
    public void describeShouldVisitRoomAndItems() {
        Room forest = new Room("forest", "I'm in a lush, green forest.");

        Item tree = new Item.Builder().named("tree").alias("Tim").describedAs("An American Sycamore tree.").in(forest).build();
        Item key = new Item.Builder().named("key").describedAs("A skeleton key.").build();
        Set<Item> items = new LinkedHashSet<>(asList(tree, key));

        AtomicReference<Room> roomRef = new AtomicReference<>();
        AtomicReference<Item> itemRef = new AtomicReference<>();

        GameState gameState = new GameState(forest, items);
        gameState.describe(new TestDisplay() {
            @Override
            public void look(Room room, List<Item> itemsInRoom) {
                roomRef.set(room);
                itemRef.set(itemsInRoom.stream().filter(i -> i.matches(tree)).findFirst().orElseThrow(AssertionError::new));
            }
        });

        assertEquals(forest, roomRef.get());
        assertEquals(tree, itemRef.get());
    }

    @Test
    public void inventoryShouldVisitItems() {
        Item tree = new Item.Builder().named("tree").alias("Tim").describedAs("An American Sycamore tree.").build();
        Item key = new Item.Builder().named("key").describedAs("A skeleton key.").build();
        Item sandwich = new Item.Builder().named("sandwich").describedAs("A ham and cheese sandwich on rye bread.").inInventory().build();
        Set<Item> items = new LinkedHashSet<>(asList(tree, key, sandwich));

        GameState gameState = new GameState(Room.NOWHERE, items);

        List<Item> itemList = new ArrayList<>();
        gameState.inventory(new TestDisplay() {
            @Override
            public void inventory(List<Item> itemsCarried) {
                itemList.addAll(itemsCarried);
            }
        });
        assertEquals(1, itemList.size());
        assertEquals(sandwich, itemList.get(0));
    }

    @Test
    public void dropShouldPlaceItemInCurrentRoom() {
        Room conferenceRoom = new Room("conferenceRoom", "A brightly lit corporate conference room");

        Item marker = new Item.Builder().named("marker").in(conferenceRoom).portable().build();

        assertFalse(marker.isCarried());
        assertTrue(marker.isHere(conferenceRoom));

        GameState gameState;

        gameState = new GameState(conferenceRoom, singleton(marker));
        gameState.drop(marker);
        assertTrue("marker should already be here", marker.isHere(conferenceRoom));

        Item wallet = new Item.Builder().named("wallet").inInventory().build();
        gameState = new GameState(conferenceRoom /* no items on purpose */);
        gameState.drop(wallet);
        assertFalse("item missing from gamestate", wallet.isHere(conferenceRoom));

        gameState = new GameState(conferenceRoom, singleton(wallet));
        gameState.drop(wallet);
        assertTrue(wallet.isHere(conferenceRoom));

        Item carrot = new Item.Builder().named("carrot").build();
        gameState = new GameState(conferenceRoom, singleton(carrot));
        gameState.drop(carrot);
        assertTrue(carrot.isHere(conferenceRoom));

        Item candy = new Item.Builder().named("pez").alias("candy").build();
        gameState = new GameState(conferenceRoom, singleton(candy));
        gameState.drop(candy);
        assertTrue(candy.isHere(conferenceRoom));
    }

    @Test
    public void destroyShouldRemoveItemFromGameState() {
        Room shed = new Room("shed", "A rickety old tool shed.");
        Item hammer = new Item.Builder().named("hammer").in(shed).build();

        GameState gameState;

        gameState = new GameState(Room.NOWHERE, singleton(hammer));
        assertTrue(gameState.exists(hammer));

        gameState.destroy(hammer);
        assertFalse(gameState.exists(hammer));
        assertTrue(hammer.isDestroyed());

        Item screwdriver = new Item.Builder().named("screwdriver").alias("flathead screwdriver").in(shed).build();
        gameState = new GameState(Room.NOWHERE, singleton(screwdriver));
        assertTrue(gameState.exists(Item.newItem("flathead screwdriver").build()));

        gameState.destroy(screwdriver);
        assertFalse(gameState.exists(screwdriver));
        assertTrue(screwdriver.isDestroyed());

    }

    @Test
    public void serializedAndDeserializedObjectsShouldEqual() throws IOException, ClassNotFoundException {
        Room office = new Room("office", "A dank corporate office.");
        office.setExitTowardsSelf(new Word("left"));
        Set<Item> items = singleton(new Item.Builder().named("axe").build());
        GameState gameState = new GameState(office, items);
        gameState.setCounter("kills", 100);
        gameState.setFlag("alive", true);
        gameState.setString("color", "blue");

        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(gameState);
            oos.flush();
        }

        GameState actual;
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            actual = (GameState) ois.readObject();
        }

        assertEquals(gameState, actual);

    }
}
