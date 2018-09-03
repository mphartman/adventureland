package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Condition;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Word;
import org.junit.Test;

import java.util.stream.IntStream;

import static hartman.games.adventureland.engine.core.Conditions.and;
import static hartman.games.adventureland.engine.core.Conditions.carrying;
import static hartman.games.adventureland.engine.core.Conditions.compareCounter;
import static hartman.games.adventureland.engine.core.Conditions.exists;
import static hartman.games.adventureland.engine.core.Conditions.hasExit;
import static hartman.games.adventureland.engine.core.Conditions.hasMoved;
import static hartman.games.adventureland.engine.core.Conditions.here;
import static hartman.games.adventureland.engine.core.Conditions.in;
import static hartman.games.adventureland.engine.core.Conditions.isFlagSet;
import static hartman.games.adventureland.engine.core.Conditions.not;
import static hartman.games.adventureland.engine.core.Conditions.or;
import static hartman.games.adventureland.engine.core.Conditions.present;
import static hartman.games.adventureland.engine.core.Conditions.random;
import static hartman.games.adventureland.engine.core.Conditions.stringEquals;
import static hartman.games.adventureland.engine.core.Conditions.there;
import static hartman.games.adventureland.engine.core.TestWords.DOWN;
import static hartman.games.adventureland.engine.core.TestWords.GO;
import static hartman.games.adventureland.engine.core.TestWords.NORTH;
import static hartman.games.adventureland.engine.core.TestWords.UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionsTest {

    @Test
    public void hasExitShouldReturnFalseWhenRoomHasNoExits() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(GO, DOWN);
        assertFalse(hasExit(DOWN).matches(command, gameState));
        command = new Command(GO, UP);
        assertFalse(hasExit(UP).matches(command, gameState));
    }

    @Test
    public void hasExitShouldReturnTrueWhenRoomHasRequestedExit() {
        Room end = new Room("end", "end");
        Room start = new Room("start", "start");
        start.setExit(DOWN, end);
        GameState gameState = new GameState(start);
        Command command = new Command(GO, DOWN);
        assertTrue(hasExit(DOWN).matches(command, gameState));
        command = new Command(GO, UP);
        assertFalse(hasExit(UP).matches(command, gameState));
    }

    @Test
    public void inRoomShouldReturnFalseWhenPlayerIsNotInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(GO, Word.ANY);
        Condition inRoom = in(Room.NOWHERE);
        assertFalse(inRoom.matches(command, gameState));
    }

    @Test
    public void inRoomShouldReturnTrueWhenPlayerIsInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(GO, Word.ANY);
        Condition inRoom = in(start);
        assertTrue(inRoom.matches(command, gameState));
    }

    @Test
    public void itemCarriedShouldReturnFalseWhenPlayerInventoryDoesNotHaveItem() {
        Item dagger = new Item.Builder().named("dagger").describedAs("A dull, chipped blade.").portable().in(Room.NOWHERE).build();
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(GO, Word.ANY);
        Condition itemCarried = carrying(dagger);
        assertFalse(itemCarried.matches(command, gameState));
    }

    @Test
    public void itemCarriedShouldReturnTrueWhenPlayerInventoryHasItem() {
        Item torch = new Item.Builder().named("torch").describedAs("An unlit wooden torch dipped in pitch.").inInventory().build();
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(GO, Word.ANY);
        Condition itemCarried = carrying(torch);
        assertTrue(itemCarried.matches(command, gameState));
    }

    @Test
    public void itemHereShouldReturnTrueWhenItemIsInRoom() {
        Room entryway = new Room("entryway", "A dark, narrow entry way into the house.");
        Item dog = new Item.Builder().named("dog").describedAs("A large, rapid dog growls at me.").in(entryway).build();
        GameState gameState = new GameState(entryway);
        Command command = new Command(GO, Word.ANY);
        Condition itemHere = here(dog);
        assertTrue(itemHere.matches(command, gameState));
    }

    @Test
    public void itemHereShouldReturnFalseWhenItemIsNotInRoom() {
        Room bathroom = new Room("bathroom", "A luxurious master bathroom with a claw-foot tub.");
        Item microwave = new Item.Builder().named("microwave").describedAs("A 1200-watt microwave.").build();
        GameState gameState = new GameState(bathroom);
        Command command = new Command(GO, Word.ANY);
        Condition itemHere = here(microwave);
        assertFalse(itemHere.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInRoom() {
        Room doghouse = new Room("doghouse", "A cozy, warm kennel.");
        Item dog = new Item.Builder().named("dog").describedAs("A small sleeps here.").in(doghouse).build();
        GameState gameState = new GameState(doghouse);
        Command command = new Command(GO, Word.ANY);
        Condition isPresent = present(dog);
        assertTrue(isPresent.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInInventory() {
        Item key = new Item.Builder().named("key").describedAs("A tarnished brass skeleton key.").inInventory().build();
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(GO, Word.ANY);
        Condition isPresent = present(key);
        assertTrue(isPresent.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnFalseWhenItemIsNeitherInInventoryOrInRoom() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = new Item.Builder().named("key").describedAs("A small key").portable().build();
        GameState gameState = new GameState(cell);
        Command command = new Command(GO, NORTH);
        Condition isPresent = present(key);
        assertFalse(isPresent.matches(command, gameState));
    }

    @Test
    public void notShouldReturnLogicalComplementOfGivenCondition() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = new Item.Builder().named("key").describedAs("A small key.").in(cell).build();
        GameState gameState = new GameState(cell);
        Command command = new Command(GO, NORTH);
        Condition isPresent = present(key);
        Condition isNotPresent = not(isPresent);
        assertEquals(!isPresent.matches(command, gameState), isNotPresent.matches(command, gameState));
    }

    @Test
    public void randomShouldReturnTrueGiven100PercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Word.ANY, Word.unrecognized());

        IntStream.range(0, 100).forEachOrdered(i -> assertTrue(random(100, () -> i).matches(command, gameState)));
    }

    @Test
    public void randomShouldReturnFalseGivenZeroPercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Word.ANY, Word.unrecognized());

        IntStream.range(0, 100).forEachOrdered(i -> assertFalse(random(0, () -> i).matches(command, gameState)));
    }

    @Test
    public void randomShouldReturnTrueGivenSuppliedNumberIsLessThenProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Word.ANY, Word.unrecognized());

        IntStream.range(0, 100).forEachOrdered(i ->
                assertTrue(random(i + 1, () -> i).matches(command, gameState)));
    }

    @Test
    public void itemMovedShouldReturnTrueIfItemsCurrentLocationDoesNotMatchItsStartingLocation() {
        Item item = new Item.Builder().named("chalice").describedAs("A jewel-encrusted golden chalice.").portable().build();
        Command command = new Command(new Word("PICKUP"), new Word("chalice"));
        GameState gameState = new GameState(Room.NOWHERE);
        assertFalse(hasMoved(item).matches(command, gameState));
        item.stow();
        assertTrue(hasMoved(item).matches(command, gameState));
        item.drop(Room.NOWHERE);
        assertFalse(hasMoved(item).matches(command, gameState));
    }

    @Test
    public void itemIsInRoomShouldReturnTrueIfItemIsInRoom() {
        Room bus = new Room("bus", "A city bus.");
        Item vomit = new Item.Builder().named("vomit").in(bus).build();
        Item driver = new Item.Builder().named("driver").build();

        assertTrue(there(vomit, bus).matches(Command.NONE, new GameState(bus)));
        assertFalse(there(driver, bus).matches(Command.NONE, new GameState(bus)));
    }

    @Test
    public void existsShouldReturnTrueForNonDestroyedGameItems() {
        Room desk = new Room("desk", "A school desk.");
        Items itemSet = Items.newItemSet();
        Item pencil = itemSet.newItem().named("pencil").in(desk).build();

        GameState gameState;

        gameState = new GameState(Room.NOWHERE);
        assertFalse("item is not in game", exists(pencil).matches(Command.NONE, gameState));

        gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        assertTrue(exists(pencil).matches(Command.NONE, gameState));

        gameState.destroy(pencil);
        assertFalse(exists(pencil).matches(Command.NONE, gameState));
    }

    @Test
    public void orShouldReturnTrueGivenOneConditionReturnsTrue() {
        assertTrue(or((command, gameState) -> true, (command, gameState) -> true).matches(Command.NONE, new GameState(Room.NOWHERE)));
        assertTrue(or((command, gameState) -> false, (command, gameState) -> true).matches(Command.NONE, new GameState(Room.NOWHERE)));
        assertTrue(or((command, gameState) -> true, (command, gameState) -> false).matches(Command.NONE, new GameState(Room.NOWHERE)));
        assertFalse(or((command, gameState) -> false, (command, gameState) -> false).matches(Command.NONE, new GameState(Room.NOWHERE)));
    }

    @Test
    public void andShouldReturnTrueGivenBothConditionsReturnTrue() {
        assertTrue(and((command, gameState) -> true, (command, gameState) -> true).matches(Command.NONE, new GameState(Room.NOWHERE)));
        assertFalse(and((command, gameState) -> false, (command, gameState) -> true).matches(Command.NONE, new GameState(Room.NOWHERE)));
        assertFalse(and((command, gameState) -> true, (command, gameState) -> false).matches(Command.NONE, new GameState(Room.NOWHERE)));
        assertFalse(and((command, gameState) -> false, (command, gameState) -> false).matches(Command.NONE, new GameState(Room.NOWHERE)));
    }

    @Test
    public void isFlagSetReturnsFalseGivenFlagHasNotBeenSet() {
        assertFalse(isFlagSet("light_is_green").matches(Command.NONE, new GameState(Room.NOWHERE)));
    }

    @Test
    public void isFlagSetReturnsTrueGivenFlagHasBeenSet() {
        GameState gameState  = new GameState(Room.NOWHERE);

        gameState.setFlag("happy", true);
        assertTrue(isFlagSet("happy").matches(Command.NONE, gameState));

        gameState.setFlag("happy", false);
        assertFalse(isFlagSet("happy").matches(Command.NONE, gameState));
    }

    @Test
    public void compareCounterReturnsResultOfComparisonFunction() {
        GameState gameState = new GameState(Room.NOWHERE);

        assertTrue(compareCounter("flies_killed", i -> i == 0).matches(Command.NONE, gameState));
        assertFalse(compareCounter("flies_killed", i -> i > 0).matches(Command.NONE, gameState));

        gameState.setCounter("flies_killed", 1);
        assertFalse(compareCounter("flies_killed", i -> i == 0).matches(Command.NONE, gameState));
        assertTrue(compareCounter("flies_killed", i -> i > 0).matches(Command.NONE, gameState));
    }

    @Test
    public void stringEqualsReturnsTrueIfStringsAreEqual() {
        GameState gameState = new GameState(Room.NOWHERE);
        gameState.setString("bar", "foo");
        assertTrue(stringEquals("bar", "foo").matches(Command.NONE, gameState));
    }
}
