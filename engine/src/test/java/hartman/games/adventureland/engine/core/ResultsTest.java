package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import org.junit.Test;

import java.util.List;
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

        Results.go.execute(command, gameState, msg -> {});

        assertEquals(tower_second_floor, gameState.getCurrentRoom());
    }

    @Test(expected = IllegalStateException.class)
    public void gotoRoomShouldThrowExceptionIfDirectionIsNotValidExitFromCurrentRoom() {
        Room sealed_tomb = new Room("sealed_tomb", "There is no escape.");
        GameState gameState = new GameState(sealed_tomb);
        Command command = new Command(Verbs.GO, Nouns.UP);

        Results.go.execute(command, gameState, msg -> {});
    }

    @Test
    public void quitShouldChangeGameStateRunning() {
        GameState gameState = new GameState(Room.NOWHERE);
        assertTrue(gameState.isRunning());
        Results.quit.execute(Command.NONE, gameState, msg -> {});
        assertFalse(gameState.isRunning());
    }

    @Test
    public void lookShouldCollectCurrentRoomExitsAndItems() {
        Room house = new Room("house", "A little white house.");
        Room garage = new Room("garage", "A two stall, attached garage.");
        garage.setExit(Nouns.UP, house);

        Items.ItemSet itemSet = Items.newItemSet();
        Item car = itemSet.newItem().named("car").describedAs("A BMW 325XI sedan.").in(garage).build();
        Item mailbox = itemSet.newItem().named("mailbox").describedAs("A wooden mailbox.").build();
        GameState gameState = new GameState(garage, itemSet.copyOfItems());

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
        Items.ItemSet itemSet = Items.newItemSet();
        Item bowl = itemSet.newItem().named("bowl").describedAs("A wooden bowl.").portable().in(Room.NOWHERE).build();
        GameState gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());

        assertTrue(bowl.isPortable());
        assertFalse(bowl.isCarried());

        Results.get.execute(new Command(Verbs.GET, bowl.asNoun()), gameState, msg -> {});

        assertFalse(bowl.isHere(Room.NOWHERE));
        assertTrue(bowl.isCarried());
    }

    @Test
    public void inventoryShouldCollectItemsGivenItemsAreCarried() {
        Room house = new Room("house", "A little white house.");
        Items.ItemSet itemSet = Items.newItemSet();
        Item phone = itemSet.newItem().named("phone").describedAs("an iPhone 8").inInventory().build();
        Item vacuum = itemSet.newItem().named("vacuum").describedAs("A Hoover upright vacuum.").in(house).build();
        GameState gameState = new GameState(house, itemSet.copyOfItems());

        AtomicReference<List<Item>> itemsRef = new AtomicReference<>();
        Action.Result inventory = Results.inventory(((roomItems) -> {
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

    @Test
    public void putShouldPlaceItemGivenRoomAndItem() {
        Item fly = new Item.Builder().named("fly").describedAs("A house fly").build();
        Room kitchen = new Room("kitchen", "A dirty kitchen");

        assertFalse(fly.isHere(kitchen));

        Results.put(fly, kitchen).execute(Command.NONE, new GameState(kitchen), msg -> {});

        assertTrue(fly.isHere(kitchen));
    }
}