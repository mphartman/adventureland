package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.TestDisplay;
import hartman.games.adventureland.engine.Word;
import org.junit.Before;
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
import static hartman.games.adventureland.engine.core.TestWords.DOWN;
import static hartman.games.adventureland.engine.core.TestWords.DROP;
import static hartman.games.adventureland.engine.core.TestWords.GET;
import static hartman.games.adventureland.engine.core.TestWords.GO;
import static hartman.games.adventureland.engine.core.TestWords.UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResultsTest {

    private TestDisplay display;

    @Before
    public void setupDisplay() {
        display = new TestDisplay();
    }

    @Test
    public void quitShouldChangeGameStateRunning() {
        GameState gameState = new GameState(Room.NOWHERE);
        assertTrue(gameState.isRunning());
        quit.execute(Command.NONE, gameState, display);
        assertFalse(gameState.isRunning());
    }

    @Test
    public void goShouldMovePlayerInDirectionOfGivenNoun() {
        Room tower_second_floor = new Room("tower_second_floor", "Second story room of the tower.");
        Room tower_first_floor = new Room("tower_first_floor", "The first floor of a tall stone tower.");
        tower_first_floor.setExit(UP, tower_second_floor);

        GameState gameState = new GameState(tower_first_floor);

        go(UP).execute(new Command(GO, UP), gameState, display);

        assertEquals(tower_second_floor, gameState.getCurrentRoom());
    }

    @Test
    public void goShouldMovePlayerInDirectionOfFirstWordGivenSecondWordIsNone() {
        Room secondFloor = new Room("tower_second_floor", "Second story room of the tower.");
        Room firstFloor = new Room("tower_first_floor", "The first floor of a tall stone tower.");
        firstFloor.setExit(UP, secondFloor);
        secondFloor.setExit(DOWN, firstFloor);

        GameState gameState = new GameState(firstFloor);

        go(UP).execute(new Command(UP, NONE), gameState, display);
        assertEquals(secondFloor, gameState.getCurrentRoom());

        go(DOWN).execute(new Command(DOWN, NONE), gameState, display);
        assertEquals(firstFloor, gameState.getCurrentRoom());
    }

    @Test(expected = IllegalStateException.class)
    public void goShouldThrowExceptionIfDirectionIsNotValidExitFromCurrentRoom() {
        Room sealed_tomb = new Room("sealed_tomb", "There is no escape.");
        GameState gameState = new GameState(sealed_tomb);
        Command command = new Command(GO, UP);

        go(UP).execute(command, gameState, display);
    }

    @Test
    public void lookShouldCollectCurrentRoomExitsAndItems() {
        Room house = new Room("house", "A little white house.");
        Room garage = new Room("garage", "A two stall, attached garage.");
        garage.setExit(UP, house);

        Items itemSet = Items.newItemSet();
        Item car = itemSet.newItem().named("car").describedAs("A BMW 325XI sedan.").in(garage).build();
        Item mailbox = itemSet.newItem().named("mailbox").describedAs("A wooden mailbox.").build();

        AtomicReference<Room> roomRef = new AtomicReference<>();
        AtomicReference<List<Item>> itemsRef = new AtomicReference<>();

        Display display = new TestDisplay() {
            @Override
            public void look(Room room, List<Item> itemsInRoom) {
                roomRef.set(room);
                itemsRef.set(itemsInRoom);
            }
        };

        GameState gameState = new GameState(garage, itemSet.copyOfItems());

        look.execute(Command.NONE, gameState, display);

        assertEquals(garage, roomRef.get());
        assertEquals(1, itemsRef.get().size());
        assertEquals(car, itemsRef.get().iterator().next());
        assertEquals(1, roomRef.get().getExits().size());
        assertEquals(new Room.Exit(UP, house), roomRef.get().getExits().iterator().next());
    }

    @Test
    public void printShouldPrintToDisplayGivenAString() {
        print("Fly, you fools!").execute(Command.NONE, new GameState(Room.NOWHERE), display);
        assertEquals("Fly, you fools!", display.toString());
    }

    @Test
    public void printShouldReplaceNounPlaceholderGivenTemplateMessage() {
        print("This is the noun \"{word:2}\"").execute(new Command(NONE, new Word("pop")), new GameState(Room.NOWHERE), display);
        assertEquals("This is the noun \"pop\"", display.toString());
    }

    @Test
    public void printShouldReplaceVerbPlaceholderGivenTemplateMessage() {
        print("I don't know how to \"{word:1}\"").execute(new Command(new Word("Dance"), NONE), new GameState(Room.NOWHERE), display);
        assertEquals("I don't know how to \"Dance\"", display.toString());
    }

    @Test
    public void inventoryShouldCollectItemsGivenItemsAreCarried() {
        Room house = new Room("house", "A little white house.");
        Items itemSet = Items.newItemSet();
        Item phone = itemSet.newItem().named("phone").describedAs("an iPhone 8").inInventory().build();
        Item vacuum = itemSet.newItem().named("vacuum").describedAs("A Hoover upright vacuum.").in(house).build();
        GameState gameState = new GameState(house, itemSet.copyOfItems());

        AtomicReference<Item> itemRef = new AtomicReference<>();

        Display display = new TestDisplay() {
            @Override
            public void inventory(List<Item> itemsCarried) {
                assertEquals(1, itemsCarried.size());
                itemRef.set(itemsCarried.iterator().next());
            }
        };

        inventory.execute(Command.NONE, gameState, display);
        assertEquals(phone, itemRef.get());
    }

    @Test
    public void swapShouldSwitchRoomsGivenTwoItems() {
        Room bedroom = new Room("bedroom", "A bedroom");
        Item lockedChest = new Item.Builder().named("locked_chest").in(bedroom).build();
        Item openedChest = new Item.Builder().named("opened_chest").build();

        assertTrue(lockedChest.isHere(bedroom));
        assertTrue(openedChest.isHere(Room.NOWHERE));

        GameState gameState = new GameState(bedroom);
        Display noDisplay = display;
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
        gotoRoom(atrium).execute(Command.NONE, gameState, display);
        assertEquals(atrium, gameState.getCurrentRoom());
    }

    @Test
    public void putShouldPlaceItemGivenRoomAndItem() {
        Item fly = new Item.Builder().named("fly").describedAs("A house fly").build();
        Room kitchen = new Room("kitchen", "A dirty kitchen");

        assertFalse(fly.isHere(kitchen));

        put(fly, kitchen).execute(Command.NONE, new GameState(kitchen), display);

        assertTrue(fly.isHere(kitchen));
    }

    @Test
    public void getShouldUpdateInventoryGivenAnItem() {
        Items itemSet = Items.newItemSet();
        Item bowl = itemSet.newItem().named("bowl").describedAs("A wooden bowl.").portable().in(Room.NOWHERE).build();
        GameState gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());

        assertTrue(bowl.isPortable());
        assertFalse(bowl.isCarried());

        get(bowl).execute(new Command(GET, bowl), gameState, display);

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

        drop(potato).execute(new Command(DROP, potato), gameState, display);

        assertFalse(potato.isCarried());
        assertTrue(potato.isHere(cellar));
    }

    @Test
    public void dropShouldPlaceItemInCurrentRoom() {
        Item potato = new Item.Builder().named("potato").inInventory().build();
        Item peeler = new Item.Builder().named("peeler").inInventory().build();
        Set<Item> items = new LinkedHashSet<>();
        items.add(potato);
        items.add(peeler);
        Room cellar = new Room("cellar", "a potato cellar");
        GameState gameState = new GameState(cellar, items);

        assertTrue(peeler.isCarried());
        assertFalse(peeler.isHere(cellar));

        drop(peeler).execute(new Command(DROP, peeler), gameState, display);

        assertFalse(peeler.isCarried());
        assertTrue(peeler.isHere(cellar));
    }

    @Test
    public void putWithShouldPlaceFirstItemInSameRoomAsSecondItem() {
        Room pants = new Room("pants", "A pair of jorts");
        Item grenade = new Item.Builder().named("grenade").build();
        Item phone = new Item.Builder().named("phone").in(pants).build();

        assertFalse(grenade.isHere(pants));
        assertTrue(phone.isHere(pants));

        putWith(grenade, phone).execute(Command.NONE, new GameState(Room.NOWHERE), display);

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

        destroy(orb).execute(Command.NONE, gameState, display);
        assertFalse(gameState.exists(orb));
        assertTrue(orb.isDestroyed());
    }

    @Test
    public void setFlagShouldSetFlagToTrue() {
        GameState gameState = new GameState(Room.NOWHERE);

        assertFalse(gameState.getFlag("hostile"));
        setFlag("hostile", true).execute(Command.NONE, gameState, display);
        assertTrue(gameState.getFlag("hostile"));

        setFlag("hostile", false).execute(Command.NONE, gameState, display);
        assertFalse(gameState.getFlag("hostile"));
    }

    @Test
    public void resetFlagShouldSetFlagToFalse() {
        GameState gameState = new GameState(Room.NOWHERE);

        gameState.setFlag("hostile");
        assertTrue(gameState.getFlag("hostile"));

        resetFlag("hostile").execute(Command.NONE, gameState, display);
        assertFalse(gameState.getFlag("hostile"));
    }

    @Test
    public void setCounterShouldSetCounterToGivenInteger() {
        GameState gameState = new GameState(Room.NOWHERE);

        assertEquals(0, gameState.getCounter("kills"));

        setCounter("kills", 100).execute(Command.NONE, gameState, display);
        assertEquals(100, gameState.getCounter("kills"));
    }

    @Test
    public void incrementCounterShouldIncreaseCounterValueByOne() {
        GameState gameState = new GameState(Room.NOWHERE);

        assertEquals(0, gameState.getCounter("kills"));

        incrementCounter("kills").execute(Command.NONE, gameState, display);
        assertEquals(1, gameState.getCounter("kills"));

        incrementCounter("kills").execute(Command.NONE, gameState, display);
        assertEquals(2, gameState.getCounter("kills"));
    }

    @Test
    public void decrementCounterShouldDecreaseCounterValueByOne() {
        GameState gameState = new GameState(Room.NOWHERE);

        gameState.setCounter("kills", 1);
        assertEquals(1, gameState.getCounter("kills"));

        decrementCounter("kills").execute(Command.NONE, gameState, display);
        assertEquals(0, gameState.getCounter("kills"));

        decrementCounter("kills").execute(Command.NONE, gameState, display);
        assertEquals(-1, gameState.getCounter("kills"));
    }

    @Test
    public void resetCounterShouldSetCounterToZero() {
        GameState gameState = new GameState(Room.NOWHERE);

        gameState.setCounter("kills", 1);
        assertEquals(1, gameState.getCounter("kills"));

        resetCounter("kills").execute(Command.NONE, gameState, display);
        assertEquals(0, gameState.getCounter("kills"));
    }

    @Test
    public void setStringShouldSetStringToGivenValue() {
        GameState gameState = new GameState(Room.NOWHERE);

        assertEquals("", gameState.getString("sign"));

        setString("sign", "BEWARE OF DOG").execute(Command.NONE, gameState, display);
        assertEquals("BEWARE OF DOG", gameState.getString("sign"));
    }

    @Test
    public void printShouldResolveCounterPlaceholdersWithCounterValues() {
        TestDisplay display = new TestDisplay();
        GameState gameState = new GameState(Room.NOWHERE);
        gameState.setCounter("kills", 3);

        print("{counter:kills} kills").execute(Command.NONE, gameState, display);
        assertEquals("3 kills", display.toString());
        display.reset();

        print("{counter:foo} foos").execute(Command.NONE, gameState, display);
        assertEquals("0 foos", display.toString());
    }

    @Test
    public void printShouldResolveFlagPlaceholdersWithFlagValues() {
        TestDisplay display = new TestDisplay();
        GameState gameState = new GameState(Room.NOWHERE);
        gameState.setFlag("winning", true);

        print("Am I winning? {flag:winning}").execute(Command.NONE, gameState, display);
        assertEquals("Am I winning? true", display.toString());
        display.reset();

        print("{flag:foo} foos").execute(Command.NONE, gameState, display);
        assertEquals("false foos", display.toString());
    }

    @Test
    public void printShouldResolveStringPlaceholdersWithStringValues() {
        TestDisplay display = new TestDisplay();
        GameState gameState = new GameState(Room.NOWHERE);
        gameState.setString("foo", "bar");

        print("Every programmer knows that foo equals {string:foo}").execute(Command.NONE, gameState, display);
        assertEquals("Every programmer knows that foo equals bar", display.toString());
        display.reset();

        print("{string:bar} does not exist.").execute(Command.NONE, gameState, display);
        assertEquals(" does not exist.", display.toString());
    }

    @Test
    public void printShouldResolvePlaceholdersWithValues() {
        TestDisplay display = new TestDisplay();
        GameState gameState = new GameState(Room.NOWHERE);
        gameState.setString("term", "x");
        gameState.setCounter("operand", 42);
        gameState.setFlag("result", true);

        print("Every programmer knows that {word:1}ing a {word:2} is like {string:term} + {counter:operand} is {flag:result}").execute(new Command(new Word("fly"), new Word("kite")), gameState, display);
        assertEquals("Every programmer knows that flying a kite is like x + 42 is true", display.toString());
    }
}