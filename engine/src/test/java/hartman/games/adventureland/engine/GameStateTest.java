package hartman.games.adventureland.engine;

import org.junit.Test;

import java.util.LinkedHashSet;
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

        Item tree = Item.newSceneryRoomItem("tree", "An American Sycamore tree.", forest);
        Item key = Item.newSceneryRoomItem("key", "A skeleton key.");

        Set<Item> items = new LinkedHashSet<>();
        items.add(tree);
        items.add(key);

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
}
