package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Word;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static hartman.games.adventureland.engine.Word.NONE;
import static hartman.games.adventureland.engine.core.Results.decrementCounter;
import static hartman.games.adventureland.engine.core.Results.destroy;
import static hartman.games.adventureland.engine.core.Results.drop;
import static hartman.games.adventureland.engine.core.Results.get;
import static hartman.games.adventureland.engine.core.Results.go;
import static hartman.games.adventureland.engine.core.Results.gotoRoom;
import static hartman.games.adventureland.engine.core.Results.incrementCounter;
import static hartman.games.adventureland.engine.core.Results.inventory;
import static hartman.games.adventureland.engine.core.Results.look;
import static hartman.games.adventureland.engine.core.Results.print;
import static hartman.games.adventureland.engine.core.Results.put;
import static hartman.games.adventureland.engine.core.Results.putWith;
import static hartman.games.adventureland.engine.core.Results.quit;
import static hartman.games.adventureland.engine.core.Results.resetCounter;
import static hartman.games.adventureland.engine.core.Results.resetFlag;
import static hartman.games.adventureland.engine.core.Results.setCounter;
import static hartman.games.adventureland.engine.core.Results.setFlag;
import static hartman.games.adventureland.engine.core.Results.setString;
import static hartman.games.adventureland.engine.core.Results.swap;
import static hartman.games.adventureland.engine.core.Words.DOWN;
import static hartman.games.adventureland.engine.core.Words.DROP;
import static hartman.games.adventureland.engine.core.Words.GET;
import static hartman.games.adventureland.engine.core.Words.GO;
import static hartman.games.adventureland.engine.core.Words.UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResultsTest {

    @Test
    public void quitShouldChangeGameStateRunning() {
        GameState gameState = new GameState(Room.NOWHERE);
        assertTrue(gameState.isRunning());
        quit.execute(Command.NONE, gameState, msg -> {});
        assertFalse(gameState.isRunning());
    }

    @Test
    public void goShouldMovePlayerInDirectionOfGivenNoun() {
        Room tower_second_floor = new Room("tower_second_floor", "Second story room of the tower.");
        Room tower_first_floor = new Room("tower_first_floor", "The first floor of a tall stone tower.");
        tower_first_floor.setExit(UP, tower_second_floor);

        GameState gameState = new GameState(tower_first_floor);

        go.execute(new Command(GO, UP), gameState, msg -> {});

        assertEquals(tower_second_floor, gameState.getCurrentRoom());
    }

    @Test
    public void goShouldMovePlayerInDirectionOfFirstWordGivenSecondWordIsNone() {
        Room secondFloor = new Room("tower_second_floor", "Second story room of the tower.");
        Room firstFloor = new Room("tower_first_floor", "The first floor of a tall stone tower.");
        firstFloor.setExit(UP, secondFloor);
        secondFloor.setExit(DOWN, firstFloor);

        GameState gameState = new GameState(firstFloor);

        go.execute(new Command(UP, NONE), gameState, msg -> {});
        assertEquals(secondFloor, gameState.getCurrentRoom());

        go.execute(new Command(DOWN, NONE), gameState, msg -> {});
        assertEquals(firstFloor, gameState.getCurrentRoom());
    }

    @Test(expected = IllegalStateException.class)
    public void goShouldThrowExceptionIfDirectionIsNotValidExitFromCurrentRoom() {
        Room sealed_tomb = new Room("sealed_tomb", "There is no escape.");
        GameState gameState = new GameState(sealed_tomb);
        Command command = new Command(GO, UP);

        go.execute(command, gameState, msg -> {});
    }

    @Test
    public void lookShouldCollectCurrentRoomExitsAndItems() {
        Room house = new Room("house", "A little white house.");
        Room garage = new Room("garage", "A two stall, attached garage.");
        garage.setExit(UP, house);

        Items itemSet = Items.newItemSet();
        Item car = itemSet.newItem().named("car").describedAs("A BMW 325XI sedan.").in(garage).build();
        Item mailbox = itemSet.newItem().named("mailbox").describedAs("A wooden mailbox.").build();
        GameState gameState = new GameState(garage, itemSet.copyOfItems());

        AtomicReference<Room> roomRef = new AtomicReference<>();
        AtomicReference<List<Room.Exit>> exitsRef = new AtomicReference<>();
        AtomicReference<List<Item>> itemsRef = new AtomicReference<>();
        Action.Result look = look(((room, exits, roomItems) -> {
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
        assertEquals(new Room.Exit(UP, house), exitsRef.get().get(0));
    }

    @Test
    public void printShouldPrintToDisplayGivenAString() {
        StringBuilder display = new StringBuilder();
        print("Fly, you fools!").execute(Command.NONE, null, display::append);
        assertEquals("Fly, you fools!", display.toString());
    }

    @Test
    public void printShouldValuesWhenGivenTemplateMessage() {
        StringBuilder buf = new StringBuilder();

        print("This is the noun \"{noun}\"").execute(new Command(NONE, new Word("pop")), new GameState(Room.NOWHERE), buf::append);
        assertEquals("This is the noun \"pop\"", buf.toString());

        buf.setLength(0); // clears it

        print("I don't know how to \"{verb}\"").execute(new Command(new Word("Dance"), NONE), new GameState(Room.NOWHERE), buf::append);
        assertEquals("I don't know how to \"Dance\"", buf.toString());
    }

    @Test
    public void inventoryShouldCollectItemsGivenItemsAreCarried() {
        Room house = new Room("house", "A little white house.");
        Items itemSet = Items.newItemSet();
        Item phone = itemSet.newItem().named("phone").describedAs("an iPhone 8").inInventory().build();
        Item vacuum = itemSet.newItem().named("vacuum").describedAs("A Hoover upright vacuum.").in(house).build();
        GameState gameState = new GameState(house, itemSet.copyOfItems());

        AtomicReference<List<Item>> itemsRef = new AtomicReference<>();
        Action.Result inventory = inventory(((roomItems) -> {
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
        Room bedroom = new Room("bedroom", "A bedroom");
        Item lockedChest = new Item.Builder().named("locked_chest").in(bedroom).build();
        Item openedChest = new Item.Builder().named("opened_chest").build();

        assertTrue(lockedChest.isHere(bedroom));
        assertTrue(openedChest.isHere(Room.NOWHERE));

        GameState gameState = new GameState(bedroom);
        Display noDisplay = msg -> {};
        swap(lockedChest, openedChest).execute(Command.NONE, gameState, noDisplay);

        assertTrue(lockedChest.isHere(Room.NOWHERE));
        assertTrue(openedChest.isHere(bedroom));

        swap(lockedChest, openedChest).execute(Command.NONE, gameState, noDisplay);

        assertTrue(lockedChest.isHere(bedroom));
        assertTrue(openedChest.isHere(Room.NOWHERE));
    }

    @Test
    public void gotoRoomShouldUpdateCurrentRoomGivenARoom() {
        Room atrium = new Room("atrium", "A spacious atrium.");
        GameState gameState = new GameState(Room.NOWHERE);
        assertEquals(Room.NOWHERE, gameState.getCurrentRoom());
        gotoRoom(atrium).execute(Command.NONE, gameState, msg -> {});
        assertEquals(atrium, gameState.getCurrentRoom());
    }

    @Test
    public void putShouldPlaceItemGivenRoomAndItem() {
        Item fly = new Item.Builder().named("fly").describedAs("A house fly").build();
        Room kitchen = new Room("kitchen", "A dirty kitchen");

        assertFalse(fly.isHere(kitchen));

        put(fly, kitchen).execute(Command.NONE, new GameState(kitchen), msg -> {});

        assertTrue(fly.isHere(kitchen));
    }

    @Test
    public void getShouldUpdateInventoryGivenAnItem() {
        Items itemSet = Items.newItemSet();
        Item bowl = itemSet.newItem().named("bowl").describedAs("A wooden bowl.").portable().in(Room.NOWHERE).build();
        GameState gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());

        assertTrue(bowl.isPortable());
        assertFalse(bowl.isCarried());

        get.execute(new Command(GET, bowl), gameState, msg -> {});

        assertFalse(bowl.isHere(Room.NOWHERE));
        assertTrue(bowl.isCarried());
    }

    @Test
    public void dropShouldPlaceItemInCurrentRoomGivenItsBeingCarried() {
        Item potato = new Item.Builder().named("potato").inInventory().build();
        Set<Item> items = new LinkedHashSet<>();
        items.add(potato);
        Room cellar = new Room("cellar", "a potato cellar");
        GameState gameState = new GameState(cellar, items);

        assertTrue(potato.isCarried());
        assertFalse(potato.isHere(cellar));

        drop.execute(new Command(DROP, potato), gameState, msg -> {});

        assertFalse(potato.isCarried());
        assertTrue(potato.isHere(cellar));

    }

    @Test
    public void putWithShouldPlaceFirstItemInSameRoomAsSecondItem() {
        Room pants = new Room("pants", "A pair of jorts");
        Item grenade = new Item.Builder().named("grenade").build();
        Item phone = new Item.Builder().named("phone").in(pants).build();

        assertFalse(grenade.isHere(pants));
        assertTrue(phone.isHere(pants));

        putWith(grenade, phone).execute(Command.NONE, new GameState(Room.NOWHERE), msg -> {});

        assertTrue(grenade.isHere(pants));
        assertTrue(phone.isHere(pants));
    }

    @Test
    public void destroyShouldAnnihilateItem() {
        Room xandar = new Room("xandar", "The plant Xandar");
        Items itemSet = Items.newItemSet();
        Item orb = itemSet.newItem().named("orb").in(xandar).build();

        GameState gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        assertTrue(gameState.exists(orb));
        assertFalse(orb.isDestroyed());

        destroy(orb).execute(Command.NONE, gameState, msg -> {});
        assertFalse(gameState.exists(orb));
        assertTrue(orb.isDestroyed());
    }

    @Test
    public void setFlagShouldSetFlagToTrue() {
        GameState gameState = new GameState(Room.NOWHERE);

        assertFalse(gameState.getFlag("hostile"));
        setFlag("hostile", true).execute(Command.NONE, gameState, message -> {});
        assertTrue(gameState.getFlag("hostile"));

        setFlag("hostile", false).execute(Command.NONE, gameState, message -> {});
        assertFalse(gameState.getFlag("hostile"));
    }

    @Test
    public void resetFlagShouldSetFlagToFalse() {
        GameState gameState = new GameState(Room.NOWHERE);

        gameState.setFlag("hostile");
        assertTrue(gameState.getFlag("hostile"));

        resetFlag("hostile").execute(Command.NONE, gameState, message -> {});
        assertFalse(gameState.getFlag("hostile"));
    }

    @Test
    public void setCounterShouldSetCounterToGivenInteger() {
        GameState gameState = new GameState(Room.NOWHERE);

        assertEquals(0, gameState.getCounter("kills"));

        setCounter("kills", 100).execute(Command.NONE, gameState, message -> {});
        assertEquals(100, gameState.getCounter("kills"));
    }

    @Test
    public void incrementCounterShouldIncreaseCounterValueByOne() {
        GameState gameState = new GameState(Room.NOWHERE);

        assertEquals(0, gameState.getCounter("kills"));

        incrementCounter("kills").execute(Command.NONE, gameState, message -> {});
        assertEquals(1, gameState.getCounter("kills"));

        incrementCounter("kills").execute(Command.NONE, gameState, message -> {});
        assertEquals(2, gameState.getCounter("kills"));
    }

    @Test
    public void decrementCounterShouldDecreaseCounterValueByOne() {
        GameState gameState = new GameState(Room.NOWHERE);

        gameState.setCounter("kills", 1);
        assertEquals(1, gameState.getCounter("kills"));

        decrementCounter("kills").execute(Command.NONE, gameState, message -> {});
        assertEquals(0, gameState.getCounter("kills"));

        decrementCounter("kills").execute(Command.NONE, gameState, message -> {});
        assertEquals(-1, gameState.getCounter("kills"));
    }

    @Test
    public void resetCounterShouldSetCounterToZero() {
        GameState gameState = new GameState(Room.NOWHERE);

        gameState.setCounter("kills", 1);
        assertEquals(1, gameState.getCounter("kills"));

        resetCounter("kills").execute(Command.NONE, gameState, message -> {});
        assertEquals(0, gameState.getCounter("kills"));
    }

    @Test
    public void setStringShouldSetStringToGivenValue() {
        GameState gameState = new GameState(Room.NOWHERE);

        assertEquals("", gameState.getString("sign"));

        setString("sign", "BEWARE OF DOG").execute(Command.NONE, gameState, message -> {});
        assertEquals("BEWARE OF DOG", gameState.getString("sign"));
    }
}