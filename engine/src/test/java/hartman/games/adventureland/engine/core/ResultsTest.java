package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResultsTest {
    
    @Test
    public void gotoRoomShouldMovePlayerInDirectionOfGivenNoun() {
        Room tower_second_floor = new Room("tower_second_floor", "Second story room of the tower.");
        Room tower_first_floor = new Room("tower_first_floor", "The first floor of a tall stone tower.", new Room.Exit.Builder().exit(Nouns.UP).towards(tower_second_floor).build());
        GameState gameState = new GameState(tower_first_floor);
        Command command = new Command(Verbs.GO, Nouns.UP);

        Results.Go.execute(command, gameState, msg -> {});

        assertEquals(tower_second_floor, gameState.getCurrentRoom());
    }

    @Test(expected = IllegalStateException.class)
    public void gotoRoomShouldThrowExceptionIfDirectionIsNotValidExitFromCurrentRoom() {
        Room sealed_tomb = new Room("sealed_tomb", "There is no escape.");
        GameState gameState = new GameState(sealed_tomb);
        Command command = new Command(Verbs.GO, Nouns.UP);

        Results.Go.execute(command, gameState, msg -> {});
    }

    @Test
    public void quitShouldChangeGameStateRunning() {
        GameState gameState = new GameState(Room.NOWHERE);
        assertTrue(gameState.isRunning());
        Results.Quit.execute(Command.NONE, gameState, msg -> {});
        assertFalse(gameState.isRunning());
    }

    @Test
    public void lookShouldCollectCurrentRoomExitsAndItems() {
        Room house = new Room("house", "A little white house.");
        Room garage = new Room("garage", "A two stall, attached garage.");
        garage.setExit(Nouns.UP, house);
        Item car = Item.newSceneryRoomItem("car", "A BMW 325XI sedan.", garage);
        Item mailbox = Item.newSceneryRoomItem("mailbox", "A wooden mailbox.");
        Set<Item> items = new LinkedHashSet<>();
        items.add(car);
        items.add(mailbox);
        GameState gameState = new GameState(garage, items);

        AtomicReference<Room> roomRef = new AtomicReference<>();
        AtomicReference<List<Room.Exit>> exitsRef = new AtomicReference<>();
        AtomicReference<List<Item>> itemsRef = new AtomicReference<>();
        Action.Result look = Results.look(((room, exits, roomItems) -> {
            roomRef.set(room);
            exitsRef.set(exits);
            itemsRef.set(roomItems);
            return "okay";
        }));

        StringBuilder displayOutput = new StringBuilder();
        look.execute(Command.NONE, gameState, displayOutput::append);

        assertEquals("okay", displayOutput.toString());
        assertEquals(garage, roomRef.get());
        assertEquals(1, itemsRef.get().size());
        assertEquals(car, itemsRef.get().get(0));
        assertEquals(1, exitsRef.get().size());
        assertEquals(new Room.Exit(Nouns.UP, house), exitsRef.get().get(0));
    }

    @Test
    public void printShouldPrintToDisplayGivenAString() {
        StringBuilder display = new StringBuilder();
        Results.print("Fly, you fools!").execute(Command.NONE, null, display::append);
        assertEquals("Fly, you fools!", display.toString());
    }

    @Test
    public void getShouldSetInventoryItemGivenCommandNoun() {
        Item bowl = Item.newPortableObjectItem("bowl", "A wooden bowl.", Room.NOWHERE);
        Set<Item> items = new LinkedHashSet<>();
        items.add(bowl);
        GameState gameState = new GameState(Room.NOWHERE, items);

        assertTrue(bowl.isPortable());
        assertFalse(bowl.isCarried());

        Results.Get.execute(new Command(Verbs.GET, bowl.asNoun()), gameState, msg -> {});

        assertFalse(bowl.isHere(Room.NOWHERE));
        assertTrue(bowl.isCarried());
    }

    @Test
    public void inventoryShouldCollectItemsGivenItemsAreCarried() {
        Room house = new Room("house", "A little white house.");
        Item phone = Item.newInventoryItem("phone", "A iPhone 8");
        Item vacuum = Item.newSceneryRoomItem("vacuum", "A Hoover upright vacuum.", house);
        Set<Item> items = new LinkedHashSet<>();
        items.add(phone);
        items.add(vacuum);
        GameState gameState = new GameState(house, items);

        AtomicReference<List<Item>> itemsRef = new AtomicReference<>();
        Results.Inventory inventory = Results.inventory(((roomItems) -> {
            itemsRef.set(roomItems);
            return "okie dokie";
        }));

        StringBuilder displayOutput = new StringBuilder();
        inventory.execute(Command.NONE, gameState, displayOutput::append);

        assertEquals("okie dokie", displayOutput.toString());
        assertEquals(1, itemsRef.get().size());
        assertEquals(phone, itemsRef.get().get(0));
    }

    @Test
    public void swapShouldSwitchRoomsGivenTwoItems() {
        // TODO
    }

    @Test
    public void gotoShouldUpdateCurrentRoomGivenARoom() {
        // TODO
    }

    @Test
    public void dropShouldPlaceItemGivenCurrentRoom() {
        // TODO
    }
}